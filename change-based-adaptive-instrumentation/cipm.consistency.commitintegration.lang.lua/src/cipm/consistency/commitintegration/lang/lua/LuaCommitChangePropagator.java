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
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.xtext.lua.LuaStandaloneSetup;
import org.xtext.lua.lua.Expression_Functioncall;
import org.xtext.lua.lua.impl.Expression_VariableNameImpl;

public class LuaCommitChangePropagator extends CommitChangePropagator {

//    @Inject
//    ParseHelper<Chunk> chunkParseHelper;

    @Inject
    Provider<XtextResourceSet> resourceSetProvider;

    public LuaCommitChangePropagator(VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper,
            LanguageFileSystemLayout fileLayout) {
        super(vsumFacade, repoWrapper, fileLayout);

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
                // proxies can only be variable- and function-names
                if (eObj instanceof Expression_VariableNameImpl) {
                    var exp = (Expression_VariableNameImpl) eObj;
                    var ref = exp.getRef();
                    if (ref.eIsProxy()) {
                        proxies.add(ref);
                    }
                } else if (eObj instanceof Expression_Functioncall) {
                    var exp = (Expression_Functioncall) eObj;
                    var ref = exp.getCalledFunction();
                    if (ref.eIsProxy()) {
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

    private Resource mergeResourceSet(ResourceSetImpl rs, URI targetUri) {
        ResourceSet next = new ResourceSetImpl();
        Resource merge = next.createResource(targetUri);
        rs.getResources()
            .forEach(resource -> {
//                var uri = resource.getURI();
                merge.getContents()
                    .addAll(resource.getContents());
            });
        return merge;
    }

    @Override
    public boolean propagateCurrentCheckout() {
        LOGGER.info("Propagating the current worktree");
        var workTreeResourceSet = parseWorkTreeToResourceSet();

        // store the complete resource set in one resource and
        LOGGER.debug("Merging resource set into one resource");
        var allModelsUri = URI.createFileURI(getFileSystemLayout().getModelFile()
            .toAbsolutePath()
            .toString());
        var allModels = mergeResourceSet(workTreeResourceSet, allModelsUri);

        // The models may contain proxies -> Currently we only log them
        var proxies = findProxiesInResource(allModels);
        LOGGER.debug(String.format("Code model contains %d proxies: %s", proxies.size(), proxies.stream()
            .map(p -> p.toString())
            .collect(Collectors.joining(", "))));

        // Save the resource to disk
        // This is not needed for the propagation, but we do it anyway for debugging purposes
//        try {
//            allModels.save(null);

        // and propagate into the vsum
        var propagatedChanges = vsumFacade.propagateResource(allModels, null);
        if (propagatedChanges != null) {
            LOGGER.info(String.format("Propagated %d changes", propagatedChanges.size()));
            return propagatedChanges.size() > 0;
        }
        LOGGER.error("No propagated changes");
        return false;
    }
}
