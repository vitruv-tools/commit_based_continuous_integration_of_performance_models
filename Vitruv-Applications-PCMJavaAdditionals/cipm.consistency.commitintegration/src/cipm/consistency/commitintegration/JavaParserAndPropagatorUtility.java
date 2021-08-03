package cipm.consistency.commitintegration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.LogicalJavaURIGenerator;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Origin;

import jamopp.options.ParserOptions;
import jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser;
import tools.vitruv.framework.userinteraction.InternalUserInteractor;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.vsum.VirtualModel;

/**
 * A utility class for the integration and change propagation of Java code into Vitruvius.
 * 
 * @author Martin Armbruster
 */
public final class JavaParserAndPropagatorUtility {
	private static final Logger logger = Logger.getLogger(JavaParserAndPropagatorUtility.class.getSimpleName());
	private static final String MAVEN_POM_FILE_NAME = "pom.xml";
	private static final String GRADLE_BUILD_FILE_NAME = "build.gradle";
	private static final String DOCKERFILE_FILE_NAME = "Dockerfile";
	
	private JavaParserAndPropagatorUtility() {
	}
	
	/**
	 * Parses all Java code and creates one Resource with all models.
	 * 
	 * @param dir directory in which the Java code resides.
	 * @param target target file of the Resource with all models.
	 * @param modConfig file which contains the stored module configuration.
	 * @return the Resource with all models.
	 */
	public static Resource parseJavaCodeIntoOneModel(Path dir, Path target, Path modConfig) {
		// 1. Parse the code.
		ParserOptions.CREATE_LAYOUT_INFORMATION.setValue(Boolean.FALSE);
		ParserOptions.RESOLVE_EVERYTHING.setValue(Boolean.TRUE);
		ParserOptions.REGISTER_LOCAL.setValue(Boolean.TRUE);
		JaMoPPJDTSingleFileParser parser = new JaMoPPJDTSingleFileParser();
		parser.setResourceSet(new ResourceSetImpl());
		parser.setExclusionPatterns(".*?/src/test/java/.*?");
		logger.debug("Parsing " + dir.toString());
		ResourceSet resourceSet = parser.parseDirectory(dir);
		logger.debug("Parsed " + resourceSet.getResources().size() + " files.");
		
		// 2. Filter the resources and create modules for components.
		filterResourcesForComponents(resourceSet, dir.toAbsolutePath(), modConfig);
		
		// 3. Create one resource with all Java models.
		logger.debug("Creating one resource with all Java models.");
		ResourceSet next = new ResourceSetImpl();
		Resource all = next.createResource(URI.createFileURI(target.toAbsolutePath().toString()));
		for (Resource r : new ArrayList<>(resourceSet.getResources())) {
			all.getContents().addAll(r.getContents());
		}
		return all;
	}
	
	private static void filterResourcesForComponents(ResourceSet resourceSet, Path dir, Path configPath) {
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
					detectModule(resource, file, dir, candidate);
				}
			}
		}
		InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();
		var modCandidates = new HashMap<>(candidate.getModulesInState(ModuleCandidates.ModuleState.COMPONENT_CANDIDATE));
		modCandidates.forEach((k, v) -> {
			if (config.getModuleClassification().containsKey(k)) {
				candidate.updateState(ModuleCandidates.ModuleState.COMPONENT_CANDIDATE,
						config.getModuleClassification().get(k), k);
			}
		});
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleCandidates.ModuleState.PART_OF_COMPONENT));
		modCandidates.forEach((k, v) -> {
			if (config.getSubModuleMapping().containsKey(k)) {
				candidate.removeModule(ModuleCandidates.ModuleState.PART_OF_COMPONENT, k);
				String otherMod = config.getSubModuleMapping().get(k);
				candidate.getModulesInState(candidate.getStateOfModule(otherMod)).get(otherMod).addAll(v);
			}
		});
		config.clear();
		updateConfig(config, candidate, ModuleCandidates.ModuleState.MICROSERVICE_COMPONENT);
		updateConfig(config, candidate, ModuleCandidates.ModuleState.REGULAR_COMPONENT);
		updateConfig(config, candidate, ModuleCandidates.ModuleState.NO_COMPONENT);
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleCandidates.ModuleState.COMPONENT_CANDIDATE));
		modCandidates.forEach((k, v) -> {
			int r = userInteractor.getSingleSelectionDialogBuilder().message("Detected the potential component / module"
				+ k + ". Which type of a component is it?").choices(List.of("Microservice component",
				"Regular component", "Part of another component", "No component")).startInteraction();
			ModuleCandidates.ModuleState newState;
			if (r == 0) {
				newState = ModuleCandidates.ModuleState.MICROSERVICE_COMPONENT;
			} else if (r == 1) {
				newState = ModuleCandidates.ModuleState.REGULAR_COMPONENT;
			} else if (r == 2) {
				newState = ModuleCandidates.ModuleState.PART_OF_COMPONENT;
			} else {
				newState = ModuleCandidates.ModuleState.NO_COMPONENT;
			}
			candidate.updateState(ModuleCandidates.ModuleState.COMPONENT_CANDIDATE, newState, k);
			config.getModuleClassification().put(k, newState);
		});
		modCandidates = new HashMap<>(candidate.getModulesInState(ModuleCandidates.ModuleState.PART_OF_COMPONENT));
		modCandidates.forEach((k, v) -> {
			ArrayList<String> allPossibleModules = new ArrayList<>();
			allPossibleModules.addAll(candidate.getModulesInState(
					ModuleCandidates.ModuleState.MICROSERVICE_COMPONENT).keySet());
			int mscSize = allPossibleModules.size();
			allPossibleModules.addAll(candidate.getModulesInState(
					ModuleCandidates.ModuleState.REGULAR_COMPONENT).keySet());
			int r = userInteractor.getSingleSelectionDialogBuilder().message("The component / module candidate " + k
					+ " is part of which component / module?").choices(allPossibleModules).startInteraction();
			String newMod = allPossibleModules.get(r);
			candidate.removeModule(ModuleCandidates.ModuleState.PART_OF_COMPONENT, k);
			ModuleCandidates.ModuleState stateToUse;
			if (r < mscSize) {
				stateToUse = ModuleCandidates.ModuleState.MICROSERVICE_COMPONENT;
			} else {
				stateToUse = ModuleCandidates.ModuleState.REGULAR_COMPONENT;
			}
			candidate.getModulesInState(stateToUse).get(newMod).addAll(v);
			config.getSubModuleMapping().put(k, newMod);
		});
		config.save();
		createModules(candidate.getModulesInState(ModuleCandidates.ModuleState.MICROSERVICE_COMPONENT),
				resourceSet, Origin.FILE);
		createModules(candidate.getModulesInState(ModuleCandidates.ModuleState.REGULAR_COMPONENT),
				resourceSet, Origin.ARCHIVE);
	}
	
	private static void updateConfig(ModuleConfiguration config, ModuleCandidates candidates, ModuleCandidates.ModuleState state) {
		var candidateMap = new HashMap<>(candidates.getModulesInState(state));
		candidateMap.forEach((k, v) -> config.getModuleClassification().put(k, state));
	}
	
	private static void createModules(Map<String, Set<Resource>> map, ResourceSet resourceSet,
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
	
	private static void detectModule(Resource res, Path file, Path container, ModuleCandidates candidate) {
		Path parent = file.getParent();
		while (container.compareTo(parent) != 0) {
			boolean buildFileExistence = checkSiblingExistence(parent, MAVEN_POM_FILE_NAME)
					|| checkSiblingExistence(parent, GRADLE_BUILD_FILE_NAME);
			boolean dockerFileExistence = checkSiblingExistence(parent, DOCKERFILE_FILE_NAME);
			if (buildFileExistence) {
				String modName = parent.getParent().getFileName().toString();
				if (dockerFileExistence) {
					candidate.addModuleClassifier(ModuleCandidates.ModuleState.MICROSERVICE_COMPONENT, 
							modName, res);
				} else {
					candidate.addModuleClassifier(ModuleCandidates.ModuleState.COMPONENT_CANDIDATE,
							modName, res);
				}
				return;
			}
			parent = parent.getParent();
		}
	}
	
	private static boolean checkSiblingExistence(Path file, String siblingName) {
		Path sibling = file.resolveSibling(siblingName);
		return Files.exists(sibling);
	}
	
	/**
	 * Performs an integration or change propagation of Java code into Vitruvius.
	 * 
	 * @param dir the directory with the Java code.
	 * @param target destination in which the complete Java model will be stored.
	 * @param vsum the VSUM.
	 * @param configPath file path to the module configuration.
	 */
	public static void parseAndPropagateJavaCode(Path dir, Path target, VirtualModel vsum,
			Path configPath) {
		// 1. Parse the Java code and create one Resource with all models.
		Resource all = parseJavaCodeIntoOneModel(dir, target, configPath);
		all.getContents().forEach(content ->
			JavaClasspath.get().registerJavaRoot((JavaRoot) content, all.getURI()));
		
		// 2. Propagate the Java models.
		logger.debug("Propagating the Java models.");
		vsum.propagateChangedState(all);
		JavaClasspath.get().getURIMap().entrySet().stream()
			.filter(entry -> entry.getValue() == all.getURI())
			.map(Map.Entry::getKey).collect(Collectors.toList())
			.forEach(u -> JavaClasspath.get().getURIMap().remove(u));
		all.unload();
		JavaClasspath.remove(all);
	}
}
