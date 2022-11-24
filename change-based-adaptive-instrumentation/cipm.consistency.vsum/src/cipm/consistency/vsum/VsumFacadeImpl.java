package cipm.consistency.vsum;

import cipm.consistency.models.ModelFacade;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractionFactory;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.ViewTypeFactory;
import tools.vitruv.framework.views.changederivation.DefaultStateBasedChangeResolutionStrategy;
import tools.vitruv.framework.vsum.VirtualModelBuilder;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * Facade to the V-SUM.
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
@SuppressWarnings("restriction")
public class VsumFacadeImpl implements VsumFacade {
    private static final Logger LOGGER = Logger.getLogger(VsumFacadeImpl.class.getName());

    private VsumDirLayout dirLayout;
    private List<ChangePropagationSpecification> changeSpecs;
    private InternalVirtualModel vsum;

    // initialized is used as a breakpoint conditional
    @SuppressWarnings("unused")
    private boolean initialized = false;

    public VsumFacadeImpl() {
        dirLayout = new VsumDirLayout();
    }

    public void initialize(Path rootPath, List<ModelFacade> models, List<ChangePropagationSpecification> changeSpecs) {
        dirLayout.initialize(rootPath);
        this.changeSpecs = changeSpecs;
        loadOrCreateVsum();
        loadModels(models);
    }

    private void loadModelResource(Resource res) {
        var loaded = vsum.getModelInstance(res.getURI());
        if (loaded == null) {
            this.propagateResource(res);
        }
    }

    @Override
    public List<PropagatedChange> loadModels(List<ModelFacade> models) {
        for (var model : models) {
            // multiple resources
            var resources = model.getResources();
            if (resources != null) {
                for (var resource : resources) {
                    loadModelResource(resource);
                }
            }

            // single resource
            var resource = model.getResource();
            if (resource != null) {
                loadModelResource(resource);
            }
        }
        return null;
    }

    private void loadOrCreateVsum() {
        var vsumBuilder = getVsumBuilder();

        LOGGER.info("Loading VSUM");
        vsum = vsumBuilder.buildAndInitialize();
        getView(vsum);
        initialized = true;
    }

    private CommittableView getView(InternalVirtualModel theVsum) {
        var viewType = ViewTypeFactory.createIdentityMappingViewType("myView");
        var viewSelector = viewType.createSelector(theVsum);

        // Selecting all elements here
        viewSelector.getSelectableElements()
            .forEach(ele -> viewSelector.setSelected(ele, true));

        var resolutionStrategy = new DefaultStateBasedChangeResolutionStrategy();
        var view = viewSelector.createView()
            .withChangeDerivingTrait(resolutionStrategy);
        return view;
    }

//    private List<ChangePropagationSpecification> getChangePropagationSpecs() {
//        List<ChangePropagationSpecification> changePropagationSpecs = new ArrayList<>();
//
//        // the lua->pcm spec is always added
//        changePropagationSpecs.add(new LuaPcmChangePropagationSpecification());
//        changePropagationSpecs.add(new PcmInitChangePropagationSpecification());
//
//        boolean useImUpdateChangeSpec = CommitIntegrationSettingsContainer.getSettingsContainer()
//            .getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)
//                || CommitIntegrationSettingsContainer.getSettingsContainer()
//                    .getPropertyAsBoolean(SettingKeys.USE_PCM_IM_CPRS);
//
//        if (useImUpdateChangeSpec)
//            changePropagationSpecs.add(new ImUpdateChangePropagationSpecification());
//
//        return changePropagationSpecs;
//    }

    private VirtualModelBuilder getVsumBuilder() {
//		ExtendedPcmDomain pcmDomain = new ExtendedPcmDomainProvider().getDomain();
//		pcmDomain.enableTransitiveChangePropagation();

        return new VirtualModelBuilder().withStorageFolder(dirLayout.getRootDirPath())
            .withUserInteractor(UserInteractionFactory.instance.createDialogUserInteractor())
            .withChangePropagationSpecifications(changeSpecs);
    }

    private void checkResourceForProxies(Resource res) {
        // try to resolve all proxies before checking for unresolved ones
        EcoreUtil.resolveAll(res);
        

        var rootEObject = res.getContents()
            .get(0);
        var potentialProxies = EcoreUtil.ProxyCrossReferencer.find(rootEObject);
        if (!potentialProxies.isEmpty()) {
            var proxies = potentialProxies.keySet();
            var proxyNames = proxies.stream()
                .map(p -> p.toString())
                .collect(Collectors.joining(", "));

            var errorMsg = String.format("Resource contains %d proxies: %s", proxies.size(), proxyNames);
            LOGGER.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
    }

    private boolean checkPropagationPreconditions(Resource res) {
        if (res.getContents()
            .size() == 0) {
            LOGGER.error(String.format("Resource has no contents: %s", res.getURI()));
            return false;
        }

        if (res.getErrors()
            .size() > 0) {
            LOGGER.error(String.format("Resource contains %d errors:", res.getErrors()
                .size()));
            var i = 0;
            for (var error : res.getErrors()) {
                LOGGER.error(String.format("%d: %s", i, error.getMessage()));
                i++;
            }
            return false;
        }

        // warnings are only logged, they don't prevent propagation
        if (res.getWarnings()
            .size() > 0) {
            LOGGER.debug(String.format("Resource contains %d warnings:", res.getWarnings()
                .size()));
            var i = 0;
            for (var warning : res.getWarnings()) {
                LOGGER.debug(String.format("%d: %s", i, warning.getMessage()));
                i++;
            }
        }
        checkResourceForProxies(res);
        return true;
    }

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @return The propagated changes
     */
    @Override
    public List<PropagatedChange> propagateResource(Resource resource) {
        return propagateResource(resource, null, null);
    }

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @param targetUri
     *            The uri where vitruv persists the propagated resource
     * @return The propagated changes
     */
    @Override
    public List<PropagatedChange> propagateResource(Resource resource, URI targetUri) {
        return propagateResource(resource, targetUri, null);
    }

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @param vsum
     *            Optional, may be used to override the vsum to which the change is propagated
     * @return The propagated changes
     */
    private List<PropagatedChange> propagateResource(Resource resource, URI _targetUri, InternalVirtualModel vsum) {
        if (vsum == null) {
            vsum = this.vsum;
        }
        if (_targetUri == null) {
            _targetUri = resource.getURI();
        }
        final URI targetUri = _targetUri;

        // try to resolve all proxies in the resource
        EcoreUtil.resolveAll(resource);

        if (!checkPropagationPreconditions(resource)) {
            LOGGER.error(
                    String.format("Not propagating resource because of missing preconditions: %s", resource.getURI()));
            return null;
        }

        LOGGER.trace(String.format("Propagating resource: %s", resource.getURI()
            .toString()));

        if (resource.getContents()
            .size() == 0) {
            LOGGER.debug(String.format("Not propagating empty resource: %s", resource.getURI()));
            return List.of();
        }

        var view = getView(vsum);
        var newRootEobject = resource.getContents()
            .get(0);

        var roots = view.getRootObjects();
        var possiblyExistingRoot = roots.stream()
            .filter(root -> root.eResource()
                .getURI() == targetUri)
            .findAny();
        if (possiblyExistingRoot.isPresent()) {
            LOGGER.trace(String.format("Replacing old root object (%s) at %s", newRootEobject.getClass(), targetUri));
            // replace the existing root with the new one
            var existingContents = possiblyExistingRoot.get()
                .eResource()
                .getContents();
            existingContents.remove(0);
            existingContents.add(newRootEobject);
        } else {
            LOGGER.trace(String.format("Registering new root object (%s) at %s", newRootEobject.getClass(), targetUri));
            // or register the new root at the view
            view.registerRoot(newRootEobject, targetUri);
        }

        var propagatedChanges = view.commitChangesAndUpdate();
        if (propagatedChanges.size() == 0) {
            LOGGER.info("-> No Propagated changes");
        } else {
            LOGGER.info(String.format("-> %d change(s)", propagatedChanges.size()));
        }
        return propagatedChanges;
    }

    @Override
    public InternalVirtualModel getVsum() {
        return vsum;
    }

    @Override
    public VsumDirLayout getDirLayout() {
        return dirLayout;
    }

    @Override
    public EditableCorrespondenceModelView<Correspondence> getCorrespondenceView() {
        if (vsum != null) {
            return vsum.getCorrespondenceModel();
        }
        return null;
    }
}
