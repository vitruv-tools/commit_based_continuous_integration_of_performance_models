package cipm.consistency.commitintegration.lang.detection.java;

import cipm.consistency.commitintegration.lang.detection.ComponentDetectorImpl;
import cipm.consistency.commitintegration.lang.detection.ModuleCandidates;
import cipm.consistency.commitintegration.lang.detection.ModuleConfiguration;
import cipm.consistency.commitintegration.lang.detection.ModuleState;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
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
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.UserInteractionFactory;

/**
 * A utility class for the detection of components which are converted to modules.
 * 
 * @author Martin Armbruster
 */
public final class JavaComponentModuleDetector extends ComponentDetectorImpl {

    @Override
    public ModuleCandidates detectComponents(ResourceSet resourceSet, Path dir) {
        ModuleCandidates candidates = new ModuleCandidates();
        for (Resource resource : resourceSet.getResources()) {
            if (resource.getContents()
                .isEmpty()) {
                continue;
            }
            EObject root = resource.getContents()
                .get(0);
            if (root instanceof org.emftext.language.java.containers.Module) {
                // Existing modules are removed because all modules will represent a component.
                resource.getContents()
                    .clear();
            } else if (root instanceof org.emftext.language.java.containers.Package) {
                // The module for package models are newly set at a later point in time.
                ((org.emftext.language.java.containers.Package) root).setModule(null);
            } else if (root instanceof CompilationUnit) {
                if (resource.getURI()
                    .isFile()) {
                    // Detect the component for the Java file / model.
                    strategies.forEach(s -> s.detectComponent(resource, dir, candidates));
                }
            }
        }

        return candidates;
    }

    /**
     * Detects the components and creates a module for every component.
     * 
     * @param resourceSet
     *            the ResourceSet which includes all Java models.
     * @param projectRoot
     *            path to the repository which contains the complete project and source code.
     * @param configPath
     *            path to the module configuration.
     */
    public void detectComponentsAndCreateModules(ResourceSet resourceSet, Path projectRoot, Path configPath) {
        ModuleCandidates candidates = detectComponents(resourceSet, projectRoot);

        // resolve the module candidates using config and user interaction
        resolveModuleCandidates(candidates, configPath);

        // At last, create the modules.
        createModules(candidates.getModulesInState(ModuleState.MICROSERVICE_COMPONENT), resourceSet, Origin.FILE);
        createModules(candidates.getModulesInState(ModuleState.REGULAR_COMPONENT), resourceSet, Origin.ARCHIVE);
    }

    private void resolveModuleCandidates(ModuleCandidates candidates, Path configPath) {
        InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();

        // Apply the stored configuration on the found modules.
        var modCandidates = new HashMap<>(candidates.getModulesInState(ModuleState.COMPONENT_CANDIDATE));

        ModuleConfiguration config = new ModuleConfiguration(configPath);
        // Decide the state for component candidates.
        modCandidates.forEach((k, v) -> {
            if (config.getModuleClassification()
                .containsKey(k)) {
                candidates.updateState(ModuleState.COMPONENT_CANDIDATE, config.getModuleClassification()
                    .get(k), k);
            }
        });
        modCandidates = new HashMap<>(candidates.getModulesInState(ModuleState.PART_OF_COMPONENT));
        // Merge modules which are part of other modules.
        modCandidates.forEach((k, v) -> {
            if (config.getSubModuleMapping()
                .containsKey(k)) {
                candidates.removeModule(ModuleState.PART_OF_COMPONENT, k);
                String otherMod = config.getSubModuleMapping()
                    .get(k);
                candidates.getModulesInState(candidates.getStateOfModule(otherMod))
                    .get(otherMod)
                    .addAll(v);
            }
        });
        // The configuration is reset to the current state to exclude removed modules.
        config.clear();
        updateConfig(config, candidates, ModuleState.MICROSERVICE_COMPONENT);
        updateConfig(config, candidates, ModuleState.REGULAR_COMPONENT);
        updateConfig(config, candidates, ModuleState.NO_COMPONENT);
        // Ask the developer to decide the type of the remaining component candidates.
        modCandidates = new HashMap<>(candidates.getModulesInState(ModuleState.COMPONENT_CANDIDATE));
        modCandidates.forEach((k, v) -> {
            int r = userInteractor.getSingleSelectionDialogBuilder()
                .message("Detected the potential component / module" + k + ". Which type of a component is it?")
                .choices(List.of("Microservice component", "Regular component", "Part of another component",
                        "No component"))
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
            candidates.updateState(ModuleState.COMPONENT_CANDIDATE, newState, k);
            config.getModuleClassification()
                .put(k, newState);
        });

        // Ask the developer to decide which module is part of which other module.
        modCandidates = new HashMap<>(candidates.getModulesInState(ModuleState.PART_OF_COMPONENT));
        modCandidates.forEach((k, v) -> {
            ArrayList<String> allPossibleModules = new ArrayList<>();
            allPossibleModules.addAll(candidates.getModulesInState(ModuleState.MICROSERVICE_COMPONENT)
                .keySet());
            int mscSize = allPossibleModules.size();
            allPossibleModules.addAll(candidates.getModulesInState(ModuleState.REGULAR_COMPONENT)
                .keySet());
            int r = userInteractor.getSingleSelectionDialogBuilder()
                .message("The component / module candidate " + k + " is part of which component / module?")
                .choices(allPossibleModules)
                .startInteraction();
            String newMod = allPossibleModules.get(r);
            candidates.removeModule(ModuleState.PART_OF_COMPONENT, k);
            ModuleState stateToUse;
            if (r < mscSize) {
                stateToUse = ModuleState.MICROSERVICE_COMPONENT;
            } else {
                stateToUse = ModuleState.REGULAR_COMPONENT;
            }
            candidates.getModulesInState(stateToUse)
                .get(newMod)
                .addAll(v);
            config.getSubModuleMapping()
                .put(k, newMod);
        });
        config.save();
    }

    /**
     * Creates modules for a component.
     * 
     * @param map
     *            a map of the modules to its Resources within the module.
     * @param resourceSet
     *            the ResourceSet which contains all Java models.
     * @param moduleOrigin
     *            the origin for the modules.
     */
    private void createModules(Map<String, Set<Resource>> map, ResourceSet resourceSet, Origin moduleOrigin) {
        map.forEach((k, v) -> {
            URI uri = LogicalJavaURIGenerator.getModuleURI(k);
            Resource targetResource = resourceSet.getResource(uri, false);
            if (targetResource == null) {
                targetResource = resourceSet.createResource(uri);
            }
            org.emftext.language.java.containers.Module mod = org.emftext.language.java.containers.ContainersFactory.eINSTANCE
                .createModule();
            mod.setName(k);
            mod.setOrigin(moduleOrigin);
            targetResource.getContents()
                .add(mod);
            // For every compilation unit in the module, the module of its package is set to
            // the newly created module.
            v.stream()
                .map(resource -> resource.getContents()
                    .get(0))
                .map(obj -> (CompilationUnit) obj)
                .map(cu -> cu.getChildrenByType(ConcreteClassifier.class))
                .flatMap(cc -> cc.stream())
                .map(cc -> cc.getPackage())
                .filter(p -> p != null)
                .forEach(p -> p.setModule(mod));
        });
    }

    private void updateConfig(ModuleConfiguration config, ModuleCandidates candidates, ModuleState state) {
        var candidateMap = new HashMap<>(candidates.getModulesInState(state));
        candidateMap.forEach((k, v) -> config.getModuleClassification()
            .put(k, state));
    }
}
