package cipm.consistency.base.vitruv.reactions;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.io.Files;

import cipm.consistency.base.models.runtimeenvironment.REModel.REModelFactory;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import cipm.consistency.base.shared.ModelUtil;
import cipm.consistency.base.vitruv.VsumManagerTestBase;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.helper.ReactionsCorrespondenceHelper;
import tools.vitruv.framework.change.description.VitruviusChangeFactory;
import tools.vitruv.framework.change.echange.TypeInferringAtomicEChangeFactory;
import tools.vitruv.framework.change.echange.TypeInferringUnresolvingAtomicEChangeFactory;
import tools.vitruv.framework.change.echange.eobject.CreateEObject;
import tools.vitruv.framework.change.echange.eobject.DeleteEObject;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.util.bridges.EMFBridge;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModelConfiguration;
import tools.vitruv.framework.vsum.VirtualModelImpl;

@RunWith(SpringRunner.class)
@Import(VsumManagerTestBase.VsumManagerTestConfiguration.class)
public class VsumReactionsTests extends VsumManagerTestBase {
	@Autowired
	private List<VitruvDomainProvider<? extends VitruvDomain>> domainProviders;

	@Autowired
	private List<AbstractReactionsChangePropagationSpecification> reactions;

	private InternalVirtualModel vsum;
	private TypeInferringAtomicEChangeFactory atomicFactory;

	@Before
	public void refreshVsum() {
		VirtualModelConfiguration vsumConfig = new VirtualModelConfiguration();

		for (VitruvDomainProvider<? extends VitruvDomain> provider : domainProviders) {
			vsumConfig.addMetamodel(provider.getDomain());
		}

		for (AbstractReactionsChangePropagationSpecification reaction : reactions) {
			vsumConfig.addChangePropagationSpecification(reaction);
		}

		File vsumFolder = Files.createTempDir();
		this.vsum = new VirtualModelImpl(vsumFolder, UserInteractionFactory.instance.createDummyUserInteractor(),
				vsumConfig);

		this.atomicFactory = new TypeInferringUnresolvingAtomicEChangeFactory(vsum.getUuidGeneratorAndResolver());
	}

	@Test
	public void pcmToREMTests() throws IOException {
		Pair<RuntimeEnvironmentModel, ResourceEnvironment> result = initREMAndResEnv();

		vsum.executeCommand(() -> {
			removePCMElementsTests(result.getRight(), result.getLeft());

			return null;
		});
	}

	@Test
	public void containerLinkTests() throws IOException {
		Pair<RuntimeEnvironmentModel, ResourceEnvironment> result = initREMAndResEnv();

		vsum.executeCommand(() -> {
			createLinkMatching(result.getRight(), result.getLeft());

			return null;
		});
	}

	@Test
	public void resourceContainerTests() throws IOException {
		Pair<RuntimeEnvironmentModel, ResourceEnvironment> result = initREMAndResEnv();

		vsum.executeCommand(() -> {
			createContainerTest(result.getRight(), result.getLeft());
			deleteContainerTest(result.getLeft());
			createContainerTestNonMatching(result.getRight(), result.getLeft());

			return null;
		});
	}

	private void removePCMElementsTests(ResourceEnvironment env, RuntimeEnvironmentModel rem) {
		RuntimeResourceContainer nContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer.setHostname("Virtual2");
		rem.getContainers().add(nContainer);

		CreateEObject<RuntimeResourceContainer> change = atomicFactory.createCreateEObjectChange(nContainer);
		change.setAffectedEObject(nContainer);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change));

		RuntimeResourceContainer nContainer2 = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer2.setHostname("Virtual");
		rem.getContainers().add(nContainer2);

		CreateEObject<RuntimeResourceContainer> change2 = atomicFactory.createCreateEObjectChange(nContainer2);
		change2.setAffectedEObject(nContainer2);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change2));

		assertEquals(2, rem.getContainers().size());
		assertEquals(2, env.getResourceContainer_ResourceEnvironment().size());

		RuntimeResourceContainerConnection link = REModelFactory.eINSTANCE.createRuntimeResourceContainerConnection();
		link.setContainerFrom(nContainer);
		link.setContainerTo(nContainer2);
		rem.getConnections().add(link);

		CreateEObject<RuntimeResourceContainerConnection> change3 = atomicFactory.createCreateEObjectChange(link);
		change3.setAffectedEObject(link);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change3));

		assertEquals(4, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(1, env.getLinkingResources__ResourceEnvironment().size());
		assertEquals(1, rem.getConnections().size());

		// first delete the connection
		DeleteEObject<LinkingResource> change4 = atomicFactory
				.createDeleteEObjectChange(env.getLinkingResources__ResourceEnvironment().get(0));
		change4.setAffectedEObject(env.getLinkingResources__ResourceEnvironment().get(0));

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change4));

		env.getLinkingResources__ResourceEnvironment().clear();

		assertEquals(3, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(0, env.getLinkingResources__ResourceEnvironment().size());
		assertEquals(0, rem.getConnections().size());

		// second delete the container
		DeleteEObject<ResourceContainer> change5 = atomicFactory
				.createDeleteEObjectChange(env.getResourceContainer_ResourceEnvironment().get(0));
		change5.setAffectedEObject(env.getResourceContainer_ResourceEnvironment().get(0));

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change5));

		assertEquals(2, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(0, rem.getConnections().size());
		assertEquals(1, rem.getContainers().size());
	}

	private void createLinkMatching(ResourceEnvironment env, RuntimeEnvironmentModel rem) {
		RuntimeResourceContainer nContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer.setHostname("Virtual2");
		rem.getContainers().add(nContainer);

		CreateEObject<RuntimeResourceContainer> change = atomicFactory.createCreateEObjectChange(nContainer);
		change.setAffectedEObject(nContainer);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change));

		RuntimeResourceContainer nContainer2 = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer2.setHostname("Virtual");
		rem.getContainers().add(nContainer2);

		CreateEObject<RuntimeResourceContainer> change2 = atomicFactory.createCreateEObjectChange(nContainer2);
		change2.setAffectedEObject(nContainer2);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change2));

		assertEquals(2, rem.getContainers().size());
		assertEquals(2, env.getResourceContainer_ResourceEnvironment().size());
		assertEquals(3, vsum.getCorrespondenceModel().getAllCorrespondences().size());

		LinkingResource linkres = ResourceenvironmentFactory.eINSTANCE.createLinkingResource();
		linkres.getConnectedResourceContainers_LinkingResource()
				.add(env.getResourceContainer_ResourceEnvironment().get(0));
		linkres.getConnectedResourceContainers_LinkingResource()
				.add(env.getResourceContainer_ResourceEnvironment().get(1));
		env.getLinkingResources__ResourceEnvironment().add(linkres);

		RuntimeResourceContainerConnection link = REModelFactory.eINSTANCE.createRuntimeResourceContainerConnection();
		link.setContainerFrom(nContainer);
		link.setContainerTo(nContainer2);
		rem.getConnections().add(link);

		CreateEObject<RuntimeResourceContainerConnection> change3 = atomicFactory.createCreateEObjectChange(link);
		change3.setAffectedEObject(link);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change3));

		assertEquals(4, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(1, env.getLinkingResources__ResourceEnvironment().size());
		assertEquals(1, rem.getConnections().size());

		DeleteEObject<RuntimeResourceContainerConnection> change4 = atomicFactory
				.createDeleteEObjectChange(rem.getConnections().get(0));
		change4.setAffectedEObject(rem.getConnections().get(0));

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change4));

		rem.getConnections().clear();
		env.getLinkingResources__ResourceEnvironment().clear();

		assertEquals(3, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(0, env.getLinkingResources__ResourceEnvironment().size());
		assertEquals(0, rem.getConnections().size());

		rem.getConnections().add(link);
		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change3));

		assertEquals(4, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(1, env.getLinkingResources__ResourceEnvironment().size());
		assertEquals(1, rem.getConnections().size());
	}

	private void createContainerTestNonMatching(ResourceEnvironment env, RuntimeEnvironmentModel rem) {
		assertEquals(1, vsum.getCorrespondenceModel().getAllCorrespondences().size());

		RuntimeResourceContainer nContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer.setHostname("Virtual2");
		rem.getContainers().add(nContainer);

		CreateEObject<RuntimeResourceContainer> change = atomicFactory.createCreateEObjectChange(nContainer);
		change.setAffectedEObject(nContainer);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change));

		assertEquals(1,
				ReactionsCorrespondenceHelper.getCorrespondingModelElements(nContainer,
						org.palladiosimulator.pcm.resourceenvironment.ResourceContainer.class, null, (e) -> true,
						vsum.getCorrespondenceModel()).size());

		assertEquals(2, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(2, env.getResourceContainer_ResourceEnvironment().size());
		assertEquals(1, rem.getContainers().size());
	}

	private void deleteContainerTest(RuntimeEnvironmentModel rem) {
		DeleteEObject<RuntimeResourceContainer> change = atomicFactory
				.createDeleteEObjectChange(rem.getContainers().get(0));
		change.setAffectedEObject(rem.getContainers().get(0));

		rem.getContainers().clear();

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChangeWithVuri(change,
				VURI.getInstance(rem.eResource())));

		assertEquals(1, vsum.getCorrespondenceModel().getAllCorrespondences().size());
	}

	private void createContainerTest(ResourceEnvironment env, RuntimeEnvironmentModel rem) {
		RuntimeResourceContainer nContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		nContainer.setHostname("Virtual");
		rem.getContainers().add(nContainer);

		CreateEObject<RuntimeResourceContainer> change = atomicFactory.createCreateEObjectChange(nContainer);
		change.setAffectedEObject(nContainer);

		vsum.propagateChange(VitruviusChangeFactory.getInstance().createConcreteChange(change));

		assertEquals(1,
				ReactionsCorrespondenceHelper.getCorrespondingModelElements(nContainer,
						org.palladiosimulator.pcm.resourceenvironment.ResourceContainer.class, null, (e) -> true,
						vsum.getCorrespondenceModel()).size());

		assertEquals(2, vsum.getCorrespondenceModel().getAllCorrespondences().size());
		assertEquals(1, env.getResourceContainer_ResourceEnvironment().size());
		assertEquals(1, rem.getContainers().size());
	}

	private Pair<RuntimeEnvironmentModel, ResourceEnvironment> initREMAndResEnv() throws IOException {
		RuntimeEnvironmentModel rem = REModelFactory.eINSTANCE.createRuntimeEnvironmentModel();
		ResourceEnvironment resEnv = ResourceenvironmentFactory.eINSTANCE.createResourceEnvironment();
		org.palladiosimulator.pcm.resourceenvironment.ResourceContainer virtual = ResourceenvironmentFactory.eINSTANCE
				.createResourceContainer();
		virtual.setEntityName("Virtual");
		resEnv.getResourceContainer_ResourceEnvironment().add(virtual);

		File tempFile1 = File.createTempFile("model", ".resourceenvironment");
		File tempFile2 = File.createTempFile("model", ".rem");
		tempFile1.deleteOnExit();
		tempFile2.deleteOnExit();

		ModelUtil.saveToFile(resEnv, tempFile1);
		ModelUtil.saveToFile(rem, tempFile2);

		vsum.persistRootElement(VURI.getInstance(EMFBridge.getEmfFileUriForFile(tempFile1)), resEnv);
		vsum.persistRootElement(VURI.getInstance(EMFBridge.getEmfFileUriForFile(tempFile2)), rem);

		CorrespondenceModel cpm = vsum.getCorrespondenceModel();

		vsum.executeCommand(() -> {
			ReactionsCorrespondenceHelper.addCorrespondence(cpm, resEnv, rem, null);
			return null;
		});

		vsum.save();

		assertEquals(1, cpm.getAllCorrespondences().size());

		return Pair.of(rem, resEnv);
	}

}
