package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectorImpl;
import cipm.consistency.commitintegration.lang.detection.ComponentState;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.models.CodeModelFacade;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.xtext.lua.LuaStandaloneSetup;
import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_Functioncall;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.LuaFactory;

public class LuaModelFacade implements CodeModelFacade {
    private static final Logger LOGGER = Logger.getLogger(LuaModelFacade.class.getName());
    private LuaDirLayout dirLayout;
    private ComponentDetector componentDetector;
    
    // TODO tracking the last component set is a bit ugly here
    private ComponentSet currentComponentSet;
    private Resource currentResource;
    
    @Inject
    Provider<XtextResourceSet> resourceSetProvider;


    public LuaModelFacade() {
        Injector injector = new LuaStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);

        this.componentDetector = new ComponentDetectorImpl();
        this.dirLayout = new LuaDirLayout();
    }


    @Override
    public void initialize(Path dirPath) {
        // todo find a way to initialize the work tree
        // this.workTree = workTree;
        this.dirLayout.initialize(dirPath);
    }

    public void setComponentDetectionStrategies(List<ComponentDetectionStrategy> strategies) {
        for (var strat : strategies) {
            this.componentDetector.addComponentDetectionStrategy(strat);
        }
    }

    private List<EObject> findProxiesInResource(Resource resource) {
        // The models may contain proxies
        var proxies = new ArrayList<EObject>();
        resource.getAllContents()
            .forEachRemaining(eObj -> {
                // TODO this is a crude way of finding ref attributes
                // proxies can only be variable- and function-names
                if (eObj instanceof Expression_VariableName) {
                    var exp = (Expression_VariableName) eObj;
                    var ref = exp.getRef();
                    if (ref.eIsProxy()) {
                        proxies.add(ref);
                    }
                } else if (eObj instanceof Expression_Functioncall) {
                    var exp = (Expression_Functioncall) eObj;
                    var ref = exp.getCalledFunction();
                    if (ref != null && ref.eIsProxy()) {
                        proxies.add(ref);
                    }
                }
            });
        return proxies;
    }

    private XtextResourceSet parseDirToResourceSet(Path sourceCodeDirPath) {
        LOGGER.info("Parsing source code directory");
        // get a resource from the provider
        var resourceSet = resourceSetProvider.get();
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

        var iterator = FileUtils.iterateFiles(sourceCodeDirPath.toFile(), null, true);
        while (iterator.hasNext()) {
            var file = iterator.next();
            var path = file.toPath();

            if (!path.toString()
                .endsWith(".lua"))
                continue;

            var uri = URI.createFileURI(path.toAbsolutePath()
                .toString());

            Resource res = resourceSet.getResource(uri, true);
            if (res == null) {
                LOGGER.error(String.format("Unable to load resource: %s", uri));
            } else {
                LOGGER.debug(String.format("Loaded resource: %s", uri));
            }
        }

        return resourceSet;
    }

    private Resource getCleanResource(URI uri) {
        var rs = resourceSetProvider.get();

        var path = Path.of(uri.toFileString());
        if (path.toFile()
            .exists()) {
            LOGGER.debug(String.format("Deleting backed up resource: %s", uri));
            var backupPath = Path.of(uri.toFileString() + ".bak");
            try {
                Files.move(path, backupPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
            }
        }

        var res = rs.createResource(uri);
        return res;
    }

    /**
     * Merges all chunks of the resource set into one SuperChunk and puts it in a separate resource
     * for propagation
     * 
     * @param rs
     * @param targetUri
     * @return
     * @throws IOException
     */
    private ComponentSet resolveResourceSetToComponents(Path workTree, ResourceSet rs, URI targetUri) {
        LOGGER.debug("Resolving ResourceSet into a ComponentSet");
        var merge = getCleanResource(targetUri);

        // we use this custom class as a container for all the components
        ComponentSet componentSet = LuaFactory.eINSTANCE.createComponentSet();
        merge.getContents()
            .add(componentSet);

        rs.getResources()
            .forEach(resource -> {
                // also merge errors and warnings into the new resource
                merge.getErrors()
                    .addAll(resource.getErrors());
                merge.getWarnings()
                    .addAll(resource.getWarnings());

                if (resource.getContents()
                    .size() == 0) {
                    LOGGER.error(String.format("Resource has no contents: %s", resource.getURI()));
                    return;
                }
                var eObj = resource.getContents()
                    .get(0);
                if (!(eObj instanceof Chunk)) {
                    LOGGER.error(String.format("Resource does not contain a chunk: %s", resource.getURI()));
                    return;
                }
            });

        // detect components in the resources
        var modulesCandidates = componentDetector.detectModules(rs, workTree, dirLayout.getModuleConfigurationPath());
        if (modulesCandidates != null) {
            var actualModules = modulesCandidates.getModulesInState(ComponentState.REGULAR_COMPONENT);
            LOGGER.debug(String.format("Detected %d components", actualModules.size()));

            actualModules.forEach((componentName, resources) -> {

                // Create a component and add in to the component set
                var component = LuaFactory.eINSTANCE.createComponent();
                component.setName(componentName);

                var chunksOfThisComponent = resources.stream()
                    .map(r -> r.getContents())
                    .filter(cl -> cl.size() > 0)
                    .map(cl -> cl.get(0))
                    .filter(eo -> (eo instanceof Chunk))
                    .map(eo -> (Chunk) eo)
                    .collect(Collectors.toList());

                component.getChunks()
                    .addAll(chunksOfThisComponent);
                componentSet.getComponents()
                    .add(component);
            });

        }

        try {
            merge.save(null);
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot write new resource: %s", targetUri));
        }
        return componentSet;
    }

    private boolean checkPropagationPreconditions(Resource res) {
        // The models may contain proxies
        var proxies = findProxiesInResource(res);
        if (proxies.size() > 0) {
            LOGGER.error(String.format("Code model contains %d proxies: %s", proxies.size(), proxies.stream()
                .map(p -> p.toString())
                .collect(Collectors.joining(", "))));
            return false;
        }

        return true;
    }

//    /**
//     * 
//     * @param resource The resource which is to be propagated
//     * @param uri The uri which us used to persist the resource during propagation
//     * @return
//     */
//    private List<PropagatedChange> propagateResource(Resource resource, URI uri) {
//        if (!checkPropagationPreconditions(resource))
//            return null;
//
//        return vsumFacade.propagateResource(resource, uri);
//    }
//
//    public List<PropagatedChange> propagateCurrentCheckout() {
//        LOGGER.info("Propagating the current worktree");
//        // parse all lua files into one resource set
//        var workTreeResourceSet = parseWorkTreeToResourceSet();
//        
//        // where the processed resource is stored prior to propagation
//        var storeUri = fileLayout.getParsedFileUri();
//        var processedResource = resolveResourceSetToComponents(workTreeResourceSet, storeUri);
//        
//        // the uri which is used during the propagation
//        var propagationUri = fileLayout.getModelFileUri();
//        return propagateResource(processedResource, propagationUri);
//    }

    @Override
    public Resource parseSourceCodeDir(Path sourceCodeDir) {
        LOGGER.info("Propagating the current worktree");
        // parse all lua files into one resource set
        var workTreeResourceSet = parseDirToResourceSet(sourceCodeDir);

        // where the processed resource is stored prior to propagation
        var storeUri = dirLayout.getParsedFileUri();
        currentComponentSet = resolveResourceSetToComponents(sourceCodeDir, workTreeResourceSet, storeUri);

        if (!checkPropagationPreconditions(currentComponentSet.eResource())) {
            throw new IllegalStateException();
        }
        
        currentResource = currentComponentSet.eResource();
        return currentResource;
    }

    public boolean existsOnDisk() {
        return dirLayout.getModelFilePath()
            .toFile()
            .exists();
    }

    @Override
    public LuaDirLayout getDirLayout() {
        return dirLayout;
    }

    public ComponentSet getCurrentComponentSet() {
        return currentComponentSet;
    }


    @Override
    public List<Resource> getResources() {
        return null;
    }
    
    @Override
    public Resource getResource() {
        return currentResource;
    }
}
