package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.vsum.VsumFacade;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.nio.file.Paths;
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
import org.xtext.lua.lua.ChunkSet;
import org.xtext.lua.lua.Expression_Functioncall;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.LuaFactory;
import tools.vitruv.change.composite.description.PropagatedChange;

public class LuaCommitChangePropagator extends CommitChangePropagator {

    @Inject
    Provider<XtextResourceSet> resourceSetProvider;

    public LuaCommitChangePropagator(VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper,
            LanguageFileSystemLayout fileLayout) {
        super(vsumFacade, repoWrapper, fileLayout);

        // set the platform path
//        var platformPath = fileLayout.getModelFileContainer().toString();
//        new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri(platformPath);

        // TODO Is this the correct way of doing the injecting? I heard that standalone was not
        // ideal, when within eclipse
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

    /**
     * Merges all chunks of the resource set into one SuperChunk and puts it in a separate resource
     * for propagation
     * 
     * @param rs
     * @param targetUri
     * @return
     */
    private Resource mergeResourceSetToChunkSet(ResourceSet rs, URI targetUri) {
        var mergeRs = resourceSetProvider.get();
        var merge = mergeRs.createResource(targetUri);

        // we use this custom class as a container for all the chunks
//        var superChunk = new SuperChunkImpl();
        ChunkSet chunkSet = LuaFactory.eINSTANCE.createChunkSet();
        merge.getContents()
            .add(chunkSet);

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

                // merge chunks
                chunkSet.getChunks()
                    .add((Chunk) eObj);
            });
        return merge;
    }

    private Resource mergeResourceSetToResource(ResourceSet rs, URI targetUri) {
        var mergeRs = resourceSetProvider.get();
        var merge = mergeRs.createResource(targetUri);

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

                // merge chunks
                merge.getContents()
                    .add(eObj);
            });
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

        if (res.getErrors()
            .size() > 0) {
            LOGGER.error(String.format("Code model contains %d errors:", res.getErrors()
                .size()));
            var i = 0;
            for (var error : res.getErrors()) {
                LOGGER.error(String.format("%d: %s", i, error.getMessage()));
                i++;
            }
            return false;
        }

        return true;
    }

    private List<PropagatedChange> propagateResource(Resource resource) {
        if (!checkPropagationPreconditions(resource))
            return null;

        return vsumFacade.propagateResource(resource);
    }

    @Deprecated
    private List<PropagatedChange> propagateResourceSet(ResourceSet rs, URI targetUri) {
        LOGGER.debug("Merging resource set into one resource for propagation");
        var merge = mergeResourceSetToResource(rs, targetUri);

        if (!checkPropagationPreconditions(merge))
            return null;

        var chunks = merge.getContents()
            .stream()
            .map(r -> r.eContents()
                .get(0))
            .collect(Collectors.toList());

        return vsumFacade.propagateEObjects(chunks);
    }

    private List<PropagatedChange> propagateResourceSetUsingChunkSet(ResourceSet resourceSet, URI targetUri) {
        LOGGER.debug("Merging ResourceSet into a ChunkSet for propagation");
        var chunkSetResource = mergeResourceSetToChunkSet(resourceSet, targetUri);

        return propagateResource(chunkSetResource);
    }

    @Override
    public List<PropagatedChange> propagateCurrentCheckout() {
        LOGGER.info("Propagating the current worktree");
        // parse all lua files into one resource set
        var workTreeResourceSet = parseWorkTreeToResourceSet();

        var targetUri = URI.createFileURI(getFileSystemLayout().getModelFile()
            .toAbsolutePath()
            .toString());
        
        return propagateResourceSetUsingChunkSet(workTreeResourceSet, targetUri);
    }
}
