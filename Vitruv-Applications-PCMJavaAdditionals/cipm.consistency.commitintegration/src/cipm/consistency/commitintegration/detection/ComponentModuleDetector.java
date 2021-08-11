package cipm.consistency.commitintegration.detection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.Origin;

import tools.vitruv.framework.userinteraction.InternalUserInteractor;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;

/**
 * A utility class for the integration and change propagation of Java code into Vitruvius.
 * 
 * @author Martin Armbruster
 */
public final class ComponentModuleDetector {
	private Set<ComponentDetectionStrategy> strategies = new HashSet<>();
	
	public void addComponentDetectionStrategy(ComponentDetectionStrategy strategy) {
		strategies.add(strategy);
	}
	
	public void detectComponentsAndCreateModules(ResourceSet resourceSet, Path dir, Path configPath) {
		ModuleConfiguration config = new ModuleConfiguration(configPath);
		ModuleCandidates candidate = new ModuleCandidates();
		for (Resource resource : resourceSet.getResources()) {
			if (resource.getContents().isEmpty()) {
				continue;
			}
			EObject root = resource.getContents().get(0);
			if (root instanceof org.emftext.language.java.containers.Module) {
				resource.getContents().clear();
			} else if (root instanceof org.emftext.language.java.containers.Package) {
				((org.emftext.language.java.containers.Package) root).setModule(null);
			} else if (root instanceof CompilationUnit) {
				if (resource.getURI().isFile()) {
					Path file = Paths.get(resource.getURI().toFileString()).toAbsolutePath();
					strategies.forEach(s -> s.detectComponent(resource, file, dir, candidate));
				}
			}
		}
		InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();
		var modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.COMPONENT_CANDIDATE));
		modCandidates.forEach((k, v) -> {
			if (config.getModuleClassification().containsKey(k)) {
				candidate.updateState(ModuleState.COMPONENT_CANDIDATE,
						config.getModuleClassification().get(k), k);
			}
		});
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.PART_OF_COMPONENT));
		modCandidates.forEach((k, v) -> {
			if (config.getSubModuleMapping().containsKey(k)) {
				candidate.removeModule(ModuleState.PART_OF_COMPONENT, k);
				String otherMod = config.getSubModuleMapping().get(k);
				candidate.getModulesInState(candidate.getStateOfModule(otherMod)).get(otherMod).addAll(v);
			}
		});
		config.clear();
		updateConfig(config, candidate, ModuleState.MICROSERVICE_COMPONENT);
		updateConfig(config, candidate, ModuleState.REGULAR_COMPONENT);
		updateConfig(config, candidate, ModuleState.NO_COMPONENT);
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.COMPONENT_CANDIDATE));
		modCandidates.forEach((k, v) -> {
			int r = userInteractor.getSingleSelectionDialogBuilder().message("Detected the potential component / module"
				+ k + ". Which type of a component is it?").choices(List.of("Microservice component",
				"Regular component", "Part of another component", "No component")).startInteraction();
			ModuleState newState;
			if (r == 0) {
				newState = ModuleState.MICROSERVICE_COMPONENT;
			} else if (r == 1) {
				newState = ModuleState.REGULAR_COMPONENT;
			} else if (r == 2) {
				newState = ModuleState.PART_OF_COMPONENT;
			} else {
				newState = ModuleState.NO_COMPONENT;
			}
			candidate.updateState(ModuleState.COMPONENT_CANDIDATE, newState, k);
			config.getModuleClassification().put(k, newState);
		});
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.PART_OF_COMPONENT));
		modCandidates.forEach((k, v) -> {
			ArrayList<String> allPossibleModules = new ArrayList<>();
			allPossibleModules.addAll(candidate.getModulesInState(
					ModuleState.MICROSERVICE_COMPONENT).keySet());
			int mscSize = allPossibleModules.size();
			allPossibleModules.addAll(candidate.getModulesInState(
					ModuleState.REGULAR_COMPONENT).keySet());
			int r = userInteractor.getSingleSelectionDialogBuilder().message("The component / module candidate " + k
					+ " is part of which component / module?").choices(allPossibleModules).startInteraction();
			String newMod = allPossibleModules.get(r);
			candidate.removeModule(ModuleState.PART_OF_COMPONENT, k);
			ModuleState stateToUse;
			if (r < mscSize) {
				stateToUse = ModuleState.MICROSERVICE_COMPONENT;
			} else {
				stateToUse = ModuleState.REGULAR_COMPONENT;
			}
			candidate.getModulesInState(stateToUse).get(newMod).addAll(v);
			config.getSubModuleMapping().put(k, newMod);
		});
		config.save();
		createModules(candidate.getModulesInState(ModuleState.MICROSERVICE_COMPONENT),
				resourceSet, Origin.FILE);
		createModules(candidate.getModulesInState(ModuleState.REGULAR_COMPONENT),
				resourceSet, Origin.ARCHIVE);
	}
	
	private void updateConfig(ModuleConfiguration config, ModuleCandidates candidates, ModuleState state) {
		var candidateMap = new HashMap<>(candidates.getModulesInState(state));
		candidateMap.forEach((k, v) -> config.getModuleClassification().put(k, state));
	}
	
	private void createModules(Map<String, Set<Resource>> map, ResourceSet resourceSet,
			Origin moduleOrigin) {
		map.forEach((k, v) -> {
			URI uri = LogicalJavaURIGenerator.getModuleURI(k);
			Resource targetResource = resourceSet.getResource(uri, false);
			if (targetResource == null) {
				targetResource = resourceSet.createResource(uri);
			}
			org.emftext.language.java.containers.Module mod =
					org.emftext.language.java.containers.ContainersFactory.eINSTANCE.createModule();
			mod.setName(k);
			mod.setOrigin(moduleOrigin);
			targetResource.getContents().add(mod);
			v.stream().map(resource -> resource.getContents().get(0))
				.map(obj -> (CompilationUnit) obj).map(cu -> cu.getChildrenByType(ConcreteClassifier.class))
				.flatMap(cc -> cc.stream()).map(cc -> cc.getPackage()).filter(p -> p != null)
				.forEach(p -> p.setModule(mod));
		});
	}
}
