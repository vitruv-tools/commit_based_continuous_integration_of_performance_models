package cipm.consistency.base.vitruv.persistence;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import cipm.consistency.base.core.IPcmModelProvider;
import cipm.consistency.base.core.ISpecificModelProvider;
import cipm.consistency.base.models.runtimeenvironment.REModel.REModelFactory;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.vitruv.VsumManagerTestBase;
import cipm.consistency.base.vsum.facade.IVsumFacade;
import cipm.consistency.base.vsum.manager.VsumManager;
import cipm.consistency.base.vsum.manager.VsumManager.VsumChangeSource;
import cipm.consistency.base.vsum.mapping.VsumMappingPersistence;
import tools.vitruv.framework.correspondence.Correspondences;

@RunWith(SpringRunner.class)
@Import(VsumManagerTestBase.VsumManagerTestConfiguration.class)
public class CorrespondencePersistenceTest extends VsumManagerTestBase {

	@Autowired
	private VsumManager vsumManager;

	@Autowired
	private IVsumFacade vsumFacade;

	@Autowired
	private ISpecificModelProvider specificModelProvider;

	@Autowired
	private IPcmModelProvider pcmModelProvider;

	@Autowired
	private VsumMappingPersistence persistence;

	@Before
	public void startup() {
		super.startup();
	}

	@Test
	public void createRemContainerTest() {
		// the transaction functionality is covered by the reaction tests
		// the target here is to check whether the facades work
		RuntimeResourceContainer newContainer = REModelFactory.eINSTANCE.createRuntimeResourceContainer();
		newContainer.setHostID("test");
		newContainer.setHostname("test@abc");

		vsumManager.executeTransaction(() -> {
			specificModelProvider.getRuntimeEnvironment().getContainers().add(newContainer);
			vsumFacade.createdObject(newContainer, VsumChangeSource.RUNTIME_ENVIRONMENT);
			return null;
		});

		assertEquals(1, pcmModelProvider.getResourceEnvironment().getResourceContainer_ResourceEnvironment().size());
		assertEquals(1, specificModelProvider.getRuntimeEnvironment().getContainers().size());

		Correspondences test = persistence.buildStorableCorrespondeces(vsumManager.getCorrespondenceModel(),
				vsumManager.getJavaCorrespondences());

		persistence.recoverCorresondences(vsumManager.getCorrespondenceModel(), test,
				vsumManager.getJavaCorrespondences());
	}

}
