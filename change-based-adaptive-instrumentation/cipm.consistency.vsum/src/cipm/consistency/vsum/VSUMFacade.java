package cipm.consistency.vsum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.base.shared.FileBackedModelUtil;
import cipm.consistency.base.shared.pcm.InMemoryPCM;
import cipm.consistency.base.shared.pcm.LocalFilesystemPCM;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.cpr.javapcm.CommitIntegrationJavaPCMChangePropagationSpecification;
import cipm.consistency.domains.im.InstrumentationModelDomainProvider;
import cipm.consistency.domains.java.AdjustedJavaDomainProvider;
import cipm.consistency.domains.pcm.ExtendedPcmDomain;
import cipm.consistency.domains.pcm.ExtendedPcmDomainProvider;
import mir.reactions.imUpdate.ImUpdateChangePropagationSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.helper.ReactionsCorrespondenceHelper;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.vsum.VirtualModelBuilder;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

public class VSUMFacade {
	private FileLayout files;
	private InternalVirtualModel vsum;
	private LocalFilesystemPCM filePCM;
	private InMemoryPCM pcm;
	private InstrumentationModel imm;

	public VSUMFacade(Path rootDir) {
		files = new FileLayout(rootDir);
		setUp();
	}
	
	private void setUp() {
		boolean isVSUMExistent = Files.exists(files.getVsumPath());
		ExtendedPcmDomain pcmDomain = new ExtendedPcmDomainProvider().getDomain();
		pcmDomain.enableTransitiveChangePropagation();
		var vsumBuilder = new VirtualModelBuilder().withDomain(new AdjustedJavaDomainProvider().getDomain())
				.withDomain(pcmDomain)
				.withDomain(new InstrumentationModelDomainProvider().getDomain())
				.withStorageFolder(files.getVsumPath())
				.withUserInteractor(UserInteractionFactory.instance.createDialogUserInteractor())
				.withChangePropagationSpecification(new CommitIntegrationJavaPCMChangePropagationSpecification());
		if (CommitIntegrationSettingsContainer.getSettingsContainer().getPropertyAsBoolean(SettingKeys.PERFORM_FINE_GRAINED_SEFF_RECONSTRUCTION)
				|| CommitIntegrationSettingsContainer.getSettingsContainer().getPropertyAsBoolean(SettingKeys.USE_PCM_IM_CPRS)) {
			vsumBuilder = vsumBuilder.withChangePropagationSpecification(new ImUpdateChangePropagationSpecification());
		}
		vsum = vsumBuilder.buildAndInitialize();
		filePCM = new LocalFilesystemPCM();
		filePCM.setRepositoryFile(files.getPcmRepositoryPath().toFile());
		filePCM.setAllocationModelFile(files.getPcmAllocationPath().toFile());
		filePCM.setSystemFile(files.getPcmSystemPath().toFile());
		filePCM.setResourceEnvironmentFile(files.getPcmResourceEnvironmentPath().toFile());
		filePCM.setUsageModelFile(files.getPcmUsageModelPath().toFile());
		pcm = new InMemoryPCM();
		if (isVSUMExistent) {
			Resource resource = vsum.getModelInstance(files.getPcmRepositoryURI()).getResource();
			pcm.setRepository((Repository) resource.getContents().get(0));
			resource = vsum.getModelInstance(files.getPcmAllocationURI()).getResource();
			pcm.setAllocationModel((Allocation) resource.getContents().get(0));
			resource = vsum.getModelInstance(files.getPcmSystemURI()).getResource();
			pcm.setSystem((org.palladiosimulator.pcm.system.System) resource.getContents().get(0));
			resource = vsum.getModelInstance(files.getPcmResourceEnvironmentURI()).getResource();
			pcm.setResourceEnvironmentModel((ResourceEnvironment) resource.getContents().get(0));
			resource = vsum.getModelInstance(files.getPcmUsageModelURI()).getResource();
			pcm.setUsageModel((UsageModel) resource.getContents().get(0));
			resource = vsum.getModelInstance(files.getImURI()).getResource();
			imm = (InstrumentationModel) resource.getContents().get(0);
		} else {
			pcm.setRepository(RepositoryFactory.eINSTANCE.createRepository());
			pcm.setSystem(SystemFactory.eINSTANCE.createSystem());
			pcm.setResourceEnvironmentModel(ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment());
			pcm.setAllocationModel(AllocationFactory.eINSTANCE.createAllocation());
			pcm.getAllocationModel().setSystem_Allocation(pcm.getSystem());
			pcm.getAllocationModel().setTargetResourceEnvironment_Allocation(pcm.getResourceEnvironmentModel());
			pcm.setUsageModel(UsagemodelFactory.eINSTANCE.createUsageModel());
			pcm.syncWithFilesystem(filePCM);
			imm = InstrumentationModelFactory.eINSTANCE.createInstrumentationModel();
			FileBackedModelUtil.synchronize(imm, files.getImPath().toFile(), InstrumentationModel.class);
			vsum.propagateChangedState(imm.eResource());
			vsum.propagateChangedState(pcm.getRepository().eResource());
			vsum.propagateChangedState(pcm.getResourceEnvironmentModel().eResource());
			vsum.propagateChangedState(pcm.getSystem().eResource());
			vsum.propagateChangedState(pcm.getAllocationModel().eResource());
			vsum.propagateChangedState(pcm.getUsageModel().eResource());
			ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(), pcm.getRepository(),
					RepositoryPackage.Literals.REPOSITORY, null);
			var correspondence = ReactionsCorrespondenceHelper.addCorrespondence(vsum.getCorrespondenceModel(), imm,
					InstrumentationModelPackage.Literals.INSTRUMENTATION_MODEL, null);
			try {
				correspondence.eResource().save(null);
				vsum.dispose();
			} catch (IOException e) {
			}
			setUp();
		}
	}
	
	public InternalVirtualModel getVSUM() {
		return vsum;
	}
	
	public FileLayout getFileLayout() {
		return files;
	}
	
	public InstrumentationModel getInstrumentationModel() {
		return imm;
	}
	
	public InMemoryPCM getPCMWrapper() {
		return pcm;
	}
}
