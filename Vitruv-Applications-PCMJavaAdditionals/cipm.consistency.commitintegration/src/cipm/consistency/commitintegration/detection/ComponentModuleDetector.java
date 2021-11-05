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
 * A utility class for the detection of components which are converted to
 * modules.
 * 
 * @author Martin Armbruster
 */
public final class ComponentModuleDetector {
	private Set<ComponentDetectionStrategy> strategies = new HashSet<>();

	public void addComponentDetectionStrategy(ComponentDetectionStrategy strategy) {
		strategies.add(strategy);
	}

	/**
	 * Detects the components and creates a module for every component.
	 * 
	 * @param resourceSet the ResourceSet which includes all Java models.
	 * @param dir         path to the repository which contains the complete project
	 *                    and source code.
	 * @param configPath  path to the module configuration.
	 */
	public void detectComponentsAndCreateModules(ResourceSet resourceSet, Path dir, Path configPath) {
		ModuleConfiguration config = new ModuleConfiguration(configPath);
		ModuleCandidates candidate = new ModuleCandidates();
		for (Resource resource : resourceSet.getResources()) {
			if (resource.getContents().isEmpty()) {
				continue;
			}
			EObject root = resource.getContents().get(0);
			if (root instanceof org.emftext.language.java.containers.Module) {
				// Existing modules are removed because all modules will represent a component.
				resource.getContents().clear();
			} else if (root instanceof org.emftext.language.java.containers.Package) {
				// The module for package models are newly set at a later point in time.
				((org.emftext.language.java.containers.Package) root).setModule(null);
			} else if (root instanceof CompilationUnit) {
				if (resource.getURI().isFile()) {
					// Find the Java file for the compilation unit.
					Path file = Paths.get(resource.getURI().toFileString()).toAbsolutePath();
					// Detect the component for the Java file / model.
					strategies.forEach(s -> s.detectComponent(resource, file, dir, candidate));
				}
			}
		}
		InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();
		// Apply the stored configuration on the found modules.
		var modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.COMPONENT_CANDIDATE));
		// Decide the state for component candidates.
		modCandidates.forEach((k, v) -> {
			if (config.getModuleClassification().containsKey(k)) {
				candidate.updateState(ModuleState.COMPONENT_CANDIDATE,
						config.getModuleClassification().get(k), k);
			}
		});
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.PART_OF_COMPONENT));
		// Merge modules which are part of other modules.
		modCandidates.forEach((k, v) -> {
			if (config.getSubModuleMapping().containsKey(k)) {
				candidate.removeModule(ModuleState.PART_OF_COMPONENT, k);
				String otherMod = config.getSubModuleMapping().get(k);
				candidate.getModulesInState(candidate.getStateOfModule(otherMod)).get(otherMod).addAll(v);
			}
		});
		// The configuration is reset to the current state to exclude removed modules.
		config.clear();
		updateConfig(config, candidate, ModuleState.MICROSERVICE_COMPONENT);
		updateConfig(config, candidate, ModuleState.REGULAR_COMPONENT);
		updateConfig(config, candidate, ModuleState.NO_COMPONENT);
		// Ask the developer to decide the type of the remaining component candidates.
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.COMPONENT_CANDIDATE));
		modCandidates.forEach((k, v) -> {
			int r = userInteractor.getSingleSelectionDialogBuilder()
					.message("Detected the potential component / module"
							+ k + ". Which type of a component is it?")
					.choices(List.of("Microservice component", "Regular component",
							"Part of another component", "No component"))
					.startInteraction();
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
		// Ask the developer to decide which module is part of which other module.
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleState.PART_OF_COMPONENT));
		modCandidates.forEach((k, v) -> {
			ArrayList<String> allPossibleModules = new ArrayList<>();
			allPossibleModules.addAll(candidate.getModulesInState(ModuleState.MICROSERVICE_COMPONENT).keySet());
			int mscSize = allPossibleModules.size();
			allPossibleModules.addAll(candidate.getModulesInState(ModuleState.REGULAR_COMPONENT).keySet());
			int r = userInteractor.getSingleSelectionDialogBuilder()
					.message("The component / module candidate "
							+ k + " is part of which component / module?")
					.choices(allPossibleModules).startInteraction();
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
		// At last, create the modules.
		createModules(candidate.getModulesInState(ModuleState.MICROSERVICE_COMPONENT), resourceSet, Origin.FILE);
		createModules(candidate.getModulesInState(ModuleState.REGULAR_COMPONENT), resourceSet, Origin.ARCHIVE);
	}

	private void updateConfig(ModuleConfiguration config, ModuleCandidates candidates, ModuleState state) {
		var candidateMap = new HashMap<>(candidates.getModulesInState(state));
		candidateMap.forEach((k, v) -> config.getModuleClassification().put(k, state));
	}

	/**
	 * Creates modules for a component.
	 * 
	 * @param map          a map of the modules to its Resources within the module.
	 * @param resourceSet  the ResourceSet which contains all Java models.
	 * @param moduleOrigin the origin for the modules.
	 */
	private void createModules(Map<String, Set<Resource>> map, ResourceSet resourceSet, Origin moduleOrigin) {
		map.forEach((k, v) -> {
			URI uri = LogicalJavaURIGenerator.getModuleURI(k);
			Resource targetResource = resourceSet.getResource(uri, false);
			if (targetResource == null) {
				targetResource = resourceSet.createResource(uri);
			}
			org.emftext.language.java.containers.Module mod =
					org.emftext.language.java.containers.ContainersFactory.eINSTANCE
					.createModule();
			mod.setName(k);
			mod.setOrigin(moduleOrigin);
			targetResource.getContents().add(mod);
			// For every compilation unit in the module, the module of its package is set to
			// the newly created module.
			v.stream().map(resource -> resource.getContents().get(0)).map(obj -> (CompilationUnit) obj)
					.map(cu -> cu.getChildrenByType(ConcreteClassifier.class)).flatMap(cc -> cc.stream())
					.map(cc -> cc.getPackage()).filter(p -> p != null).forEach(p -> p.setModule(mod));
		});
	}
}
