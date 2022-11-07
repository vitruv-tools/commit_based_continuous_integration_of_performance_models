package cipm.consistency.commitintegration.lang.detection.java;

import cipm.consistency.commitintegration.lang.detection.ComponentDetectorImpl;
import cipm.consistency.commitintegration.lang.detection.ModuleCandidates;
import cipm.consistency.commitintegration.lang.detection.ModuleState;
import java.nio.file.Path;
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

/**
 * A utility class for the detection of components which are converted to modules.
 * 
 * @author Martin Armbruster
 */
public final class JavaComponentModuleDetector extends ComponentDetectorImpl {

    @Override
    public ModuleCandidates detectModuleCandidates(ResourceSet resourceSet, Path dir) {
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
        ModuleCandidates candidates = detectModuleCandidates(resourceSet, projectRoot);

        // resolve the module candidates using config and user interaction
        resolveModuleCandidates(candidates, configPath);

        // At last, create the modules.
        createModules(candidates.getModulesInState(ModuleState.MICROSERVICE_COMPONENT), resourceSet, Origin.FILE);
        createModules(candidates.getModulesInState(ModuleState.REGULAR_COMPONENT), resourceSet, Origin.ARCHIVE);
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
}
