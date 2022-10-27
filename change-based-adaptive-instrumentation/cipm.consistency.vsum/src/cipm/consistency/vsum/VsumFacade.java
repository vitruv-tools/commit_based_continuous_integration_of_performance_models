package cipm.consistency.vsum;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.base.shared.FileBackedModelUtil;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import mir.reactions.imUpdate.ImUpdateChangePropagationSpecification;
import mir.reactions.luaPcm.LuaPcmChangePropagationSpecification;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationFactory;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.palladiosimulator.pcm.system.SystemFactory;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;
import tools.vitruv.change.composite.description.PropagatedChange;
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
 */
@SuppressWarnings("restriction")
public class VsumFacade {
    private static final Logger LOGGER = Logger.getLogger("cipm.VSUMFacade");
    private Path rootPath;
    private FileSystemLayout fileLayout;

    private InternalVirtualModel vsum;
    private InMemoryPCM pcm;
    private InstrumentationModel imm;

    // initialized is used as a breakpoint conditional
    @SuppressWarnings("unused")
    private boolean initialized = false;

    public VsumFacade(Path rootPath) {
        this.rootPath = rootPath;
    }

    public void initialize() throws IOException {
        fileLayout = new FileSystemLayout(rootPath);
        loadOrCreateVsum();
    }

    private CommittableView getView(InternalVirtualModel theVsum) {
        // TODO i think "myView" should be a magic string
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

    private List<ChangePropagationSpecification> getChangePropagationSpecs() {
        List<ChangePropagationSpecification> changePropagationSpecs = new ArrayList<>();

        // the lua->pcm spec is always added
        changePropagationSpecs.add(new LuaPcmChangePropagationSpecification());

        boolean useImUpdateChangeSpec = CommitIntegrationSettingsContainer.getSettingsContainer()
            .getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)
                || CommitIntegrationSettingsContainer.getSettingsContainer()
                    .getPropertyAsBoolean(SettingKeys.USE_PCM_IM_CPRS);

        if (useImUpdateChangeSpec)
            changePropagationSpecs.add(new ImUpdateChangePropagationSpecification());

        return changePropagationSpecs;
    }

    private VirtualModelBuilder getVsumBuilder() {
//		ExtendedPcmDomain pcmDomain = new ExtendedPcmDomainProvider().getDomain();
//		pcmDomain.enableTransitiveChangePropagation();

        return new VirtualModelBuilder().withStorageFolder(fileLayout.getVsumPath())
            .withUserInteractor(UserInteractionFactory.instance.createDialogUserInteractor())
            .withChangePropagationSpecifications(getChangePropagationSpecs());
    }

    private InMemoryPCM createPCM() throws IOException {
        // Create models for the PCM
        var systemModel = SystemFactory.eINSTANCE.createSystem();
        var repoModel = RepositoryFactory.eINSTANCE.createRepository();
        var resourceEnvModel = ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment();
        var usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
        var allocationModel = AllocationFactory.eINSTANCE.createAllocation();

        pcm = new InMemoryPCM(repoModel, systemModel, usageModel, allocationModel, resourceEnvModel);

        // Create files and resources before binding the allocation
        pcm.saveToFilesystem(fileLayout.getFilePCM());

        // Bind the allocation
        // This needs to occur after pcm.saveToFilej
        allocationModel.setSystem_Allocation(systemModel);
        allocationModel.setTargetResourceEnvironment_Allocation(resourceEnvModel);
        allocationModel.eResource()
            .save(null);

        return pcm;
    }

    private boolean checkPropagationPreconditions(Resource res) {
        if (res.getContents().size() == 0) {
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

        return true;
    }

//    /**
//     * TODO this does not work at all!
//     * @param eObjects
//     * @return
//     */
//    @Deprecated
//    public List<PropagatedChange> propagateEObjects(List<EObject> eObjects) {
//        var view = getView(vsum);
//
//        for (var eObj : eObjects) {
//            var persistAtUri = eObj.eResource().getURI();
//            view.registerRoot(eObj, persistAtUri);
//        }
//
//        var propagatedChanges = view.commitChangesAndUpdate();
//        if (propagatedChanges.size() == 0) {
//            LOGGER.error("  -> No Propagated changes");
//        } else {
//            LOGGER.debug(String.format("  -> %d change(s)", propagatedChanges.size()));
//        }
//        return propagatedChanges;
//    }

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @return The propagated changes
     */
    public List<PropagatedChange> propagateResource(Resource resource) {
        return propagateResource(resource, null);
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
    private List<PropagatedChange> propagateResource(Resource resource, InternalVirtualModel vsum) {
        if (vsum == null) {
            vsum = this.vsum;
        }
        
        if (!checkPropagationPreconditions(resource)) {
            LOGGER.error(String.format("Not propagating resource because of missing preconditions: %s", resource.getURI()));
            return null;
        }
        
        // add resources by registering its root object in the change deriving view
        LOGGER.debug(String.format("Propagating resource: %s", resource.getURI()
            .lastSegment()));

        if (resource.getContents()
            .size() == 0) {
            LOGGER.debug(String.format("Not propagating empty resource: %s", resource.getURI()));
            return List.of();
        }

        var view = getView(vsum);
        var rootEObject = resource.getContents()
            .get(0);

        // this also extracts the rootEObject from the resource
        view.registerRoot(rootEObject, resource.getURI());

        var propagatedChanges = view.commitChangesAndUpdate();
        if (propagatedChanges.size() == 0) {
            LOGGER.error("  -> No Propagated changes");
        } else {
            LOGGER.debug(String.format("  -> %d change(s)", propagatedChanges.size()));
        }
        return propagatedChanges;
    }

    /**
     * Propagate multiple independent resources into the underlying vsum
     * 
     * @param resources
     *            The resources which are to be propagated
     * @param vsum
     *            Optional, may be used to override the vsum to which the change is propagated
     * @return The propagated changes
     */
    private List<PropagatedChange> propagateResources(List<Resource> resources, InternalVirtualModel vsum) {
        if (vsum == null) {
            vsum = this.vsum;
        }

        final List<PropagatedChange> propagatedChanges = new ArrayList<PropagatedChange>();
        for (Resource resource : resources) {
            propagatedChanges.addAll(propagateResource(resource, vsum));
        }

        LOGGER.debug(String.format("Propagated %d changes into the VSUM", propagatedChanges.size()));
        return propagatedChanges;
    }

    private void createVsum(VirtualModelBuilder vsumBuilder) throws IOException {
        // temporary vsum for the initialization
        LOGGER.info("Creating temporary VSUM");
        final var tempVsum = vsumBuilder.buildAndInitialize();

        createPCM();

        // build IMM
        imm = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
        FileBackedModelUtil.synchronize(imm, fileLayout.getImPath()
            .toFile(), InstrumentationModel.class);

        // propagate all resources into the new vsum
        propagateResources(List.of(imm.eResource(), pcm.getRepository()
            .eResource(),
                pcm.getResourceEnvironmentModel()
                    .eResource(),
                pcm.getSystem()
                    .eResource(),
                pcm.getUsageModel()
                    .eResource(),
                pcm.getAllocationModel()
                    .eResource()),
                tempVsum);

        // add correspondences between the models
        var correspondenceModel = tempVsum.getCorrespondenceModel();
        correspondenceModel.addCorrespondenceBetween(pcm.getRepository(), RepositoryPackage.Literals.REPOSITORY, null);
        correspondenceModel.addCorrespondenceBetween(imm, InstrumentationModelPackage.Literals.INSTRUMENTATION_MODEL,
                null);

        LOGGER.debug("Disposing temporary VSUM");
        tempVsum.dispose();
    }

    private void loadOrCreateVsum() throws IOException {
        var vsumBuilder = getVsumBuilder();
        boolean overwrite = true;
        boolean filesExistent = Files.exists(fileLayout.getRootPath());
        if (overwrite && filesExistent) {
            LOGGER.info("Deleting existing models (VSUM, PCM, IMM, ..)");
            fileLayout.delete();
        }
        if (overwrite || !filesExistent) {
            LOGGER.info("Creating new models (VSUM, PCM, IMM, ..)");
            createVsum(vsumBuilder);
        }

        LOGGER.info("Loading VSUM");
        vsum = vsumBuilder.buildAndInitialize();
        getView(vsum);

        LOGGER.info("Loading pcm from disk");
//        pcm = InMemoryPCM.createFromFilesystem(fileLayout.getFilePCM());
        pcm = new InMemoryPCM();
        LOGGER.info("Binding PCM models from VSUM");
        Resource resource = vsum.getModelInstance(fileLayout.getPcmRepositoryURI())
            .getResource();
        pcm.setRepository((Repository) resource.getContents()
            .get(0));

        resource = vsum.getModelInstance(fileLayout.getPcmSystemURI())
            .getResource();
        pcm.setSystem((org.palladiosimulator.pcm.system.System) resource.getContents()
            .get(0));

        resource = vsum.getModelInstance(fileLayout.getPcmResourceEnvironmentURI())
            .getResource();
        pcm.setResourceEnvironmentModel((ResourceEnvironment) resource.getContents()
            .get(0));

        resource = vsum.getModelInstance(fileLayout.getPcmUsageModelURI())
            .getResource();
        pcm.setUsageModel((UsageModel) resource.getContents()
            .get(0));

        resource = vsum.getModelInstance(fileLayout.getPcmAllocationURI())
            .getResource();
        pcm.setAllocationModel((Allocation) resource.getContents()
            .get(0));

        LOGGER.info("Binding IMM");
        resource = vsum.getModelInstance(fileLayout.getImURI())
            .getResource();
        imm = (InstrumentationModel) resource.getContents()
            .get(0);

        initialized = true;
    }

    public InternalVirtualModel getVsum() {
        return vsum;
    }

    public FileSystemLayout getFileSystemLayout() {
        return fileLayout;
    }

    public InstrumentationModel getInstrumentationModel() {
        return imm;
    }

    /**
     * The In memory pcm
     * 
     * @return The pcm or null if it the vsum is not yet loaded
     */
    public InMemoryPCM getPCMWrapper() {
        return pcm;
    }
}
