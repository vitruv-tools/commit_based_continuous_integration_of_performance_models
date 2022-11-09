package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.lang.detection.ModuleState;
import cipm.consistency.vsum.VsumFacade;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
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
import tools.vitruv.change.composite.description.PropagatedChange;

public class LuaCommitChangePropagator extends CommitChangePropagator {

    @Inject
    Provider<XtextResourceSet> resourceSetProvider;

    public LuaCommitChangePropagator(VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper,
            LuaLanguageFileSystemLayout fileLayout, ComponentDetector componentDetector) {
        super(vsumFacade, repoWrapper, fileLayout, componentDetector);

        // set the platform path
//        var platformPath = fileLayout.getModelFileContainer().toString();
//        new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri(platformPath);

        Injector injector = new LuaStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);
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

    private String printUri(URI uri) {
        var anchorPath = repoWrapper.getWorkTree()
            .toPath();
        var uriPath = Paths.get(uri.path());
        return anchorPath.relativize(uriPath)
            .toString();
    }

    private XtextResourceSet parseWorkTreeToResourceSet() {
        // get a resource from the provider
        var resourceSet = resourceSetProvider.get();
        resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);

        var iterator = FileUtils.iterateFiles(repoWrapper.getWorkTree(), null, true);
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
                LOGGER.error(String.format("Unable to load resource: %s", printUri(uri)));
            } else {
                LOGGER.debug(String.format("Loaded resource: %s", printUri(uri)));
            }
        }

        return resourceSet;
    }

    private Resource getCleanResource(URI uri) {
        var rs = resourceSetProvider.get();

        var path = Path.of(uri.toFileString());
        if (path.toFile().exists()) {
            LOGGER.debug(String.format("Deleting backed up resource: %s", uri));
            var backupPath = Path.of(uri.toFileString()+".bak");
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
    private Resource resolveResourceSetToComponents(ResourceSet rs, URI targetUri) {
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

//                // merge chunks
//                componentSet.getComponents()
//                    .add((Chunk) eObj);
            });

        // detect components in the resources
        var modulesCandidates = componentDetector.detectModules(rs, repoWrapper.getWorkTree()
            .toPath(), fileLayout.getModuleConfigurationPath());
        if (modulesCandidates != null) {
            var actualModules = modulesCandidates.getModulesInState(ModuleState.REGULAR_COMPONENT);
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
        return merge;
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

    /**
     * 
     * @param resource The resource which is to be propagated
     * @param uri The uri which us used to persist the resource during propagation
     * @return
     */
    private List<PropagatedChange> propagateResource(Resource resource, URI uri) {
        if (!checkPropagationPreconditions(resource))
            return null;

        return vsumFacade.propagateResource(resource, uri);
    }

//    private List<PropagatedChange> propagateResourceSetUsingChunkSet(ResourceSet resourceSet, URI targetUri) {
//        var chunkSetResource = resolveResourceSetToComponents(resourceSet, targetUri);
//        return propagateResource(chunkSetResource, targetUri);
//    }

    @Override
    public List<PropagatedChange> propagateCurrentCheckout() {
        LOGGER.info("Propagating the current worktree");
        // parse all lua files into one resource set
        var workTreeResourceSet = parseWorkTreeToResourceSet();
        
        // where the processed resource is stored prior to propagation
        var storeUri = getFileSystemLayout().getParsedFileUri();
        var processedResource = resolveResourceSetToComponents(workTreeResourceSet, storeUri);
        
        // the uri which is used during the propagation
        var propagationUri = getFileSystemLayout().getModelFileUri();
        return propagateResource(processedResource, propagationUri);
    }
}
