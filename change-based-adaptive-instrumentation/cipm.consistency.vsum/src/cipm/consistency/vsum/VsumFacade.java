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
import java.util.Collection;
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

        // TODO From the docs: you receive a selector that allows you to select the elements you
        // want to have in your view.
//        var viewSelector = theVsum.createSelector(viewType);
        LOGGER.debug(String.format("View sees: %s", viewSelector.getSelectableElements()));

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

        return new VirtualModelBuilder()
//				.withDomain(new AdjustedJavaDomainProvider().getDomain())
//				.withDomain(pcmDomain)
//				.withDomain(new InstrumentationModelDomainProvider().getDomain())
            .withStorageFolder(fileLayout.getVsumPath())
            .withUserInteractor(UserInteractionFactory.instance.createDialogUserInteractor())
            .withChangePropagationSpecifications(getChangePropagationSpecs());
    }

    private InMemoryPCM createPCM() {
        // Create models for the PCM
        var systemModel = SystemFactory.eINSTANCE.createSystem();
        var repoModel = RepositoryFactory.eINSTANCE.createRepository();
        var resourceEnvModel = ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment();
        var usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
        var allocationModel = AllocationFactory.eINSTANCE.createAllocation();
        // TODO if we set these two models here, the allocation model cannot be propagated into the vsum :/
//        allocationModel.setSystem_Allocation(systemModel);
//        allocationModel.setTargetResourceEnvironment_Allocation(resourceEnvModel);


        // build PCM
        final var filePCM = fileLayout.getFilePCM();
        pcm = new InMemoryPCM(repoModel, systemModel, usageModel, allocationModel, resourceEnvModel);
        // resync before allocation
        pcm.syncWithFilesystem(filePCM);

        pcm.getAllocationModel().setSystem_Allocation(pcm.getSystem());
        pcm.getAllocationModel().setTargetResourceEnvironment_Allocation(pcm.getResourceEnvironmentModel());

        LOGGER.debug(String.format("SystemModel: %s", pcm.getSystem().eResource()
            .getURI()));
        LOGGER.debug(String.format("ResourceEnvModel: %s", pcm.getResourceEnvironmentModel().eResource()
            .getURI()));

        // and save to disk
//         TODO for this the pcm.save... method could be used. Whats the difference?
        pcm.syncWithFilesystem(filePCM);

        return pcm;
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
    public List<PropagatedChange> propagateResource(Resource resource, InternalVirtualModel vsum) {
        if (vsum == null) {
            vsum = this.vsum;
        }
        var view = getView(vsum);
        var propagatedChanges = new ArrayList<PropagatedChange>();

        // TODO using the root is too fragile
        // add resources by registering its root object in the change deriving view
        LOGGER.debug(String.format("Propagating resource: %s", resource.toString()));
        var rootEobject = resource.getAllContents()
            .next();
        try {
            if (rootEobject != null) {
                view.registerRoot(rootEobject, resource.getURI());

                // immediately commit the change to prevent issues, when commiting multiple
                // resources
                var changes = view.commitChangesAndUpdate();
                propagatedChanges.addAll(changes);

                if (changes.size() == 0) {
                    LOGGER.error("  -> No Propagated changes");
                } else {
                    LOGGER.debug(String.format("  -> %d change(s)", changes.size()));
                }
            } else {
                LOGGER.debug(String.format("Resource does not contain an EObject: %s", resource.toString()));
            }
        } catch (IllegalStateException e) {
            LOGGER.error(String.format("Unable to register root object of resource %s", resource.toString()), e);
        }
        return propagatedChanges;
    }

    public List<PropagatedChange> propagateResources(Collection<Resource> resources, InternalVirtualModel vsum) {
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

    private void createVsum(VirtualModelBuilder vsumBuilder) {
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

        // TODO we cannot access the correspondence model view resource
//        try {
//            correspondenceModel.eResource()
//                .save(null);
//        } catch (IOException e) {
//        }

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

        pcm = new InMemoryPCM();

        LOGGER.info("Loading VSUM");
        vsum = vsumBuilder.buildAndInitialize();
        // TODO im trying to add the resources below via this view
        getView(vsum);

        // load the in-memory PCM into the VSUM
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

        try {
            resource = vsum.getModelInstance(fileLayout.getPcmAllocationURI())
                .getResource();
            pcm.setAllocationModel((Allocation) resource.getContents()
                .get(0));
        } catch (NullPointerException e) {
            // TODO why is the allocation model sometimes missing?
        }

        LOGGER.info("Binding IMM");
        resource = vsum.getModelInstance(fileLayout.getImURI())
            .getResource();
        imm = (InstrumentationModel) resource.getContents()
            .get(0);
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

    public InMemoryPCM getPCMWrapper() {
        return pcm;
    }
}
