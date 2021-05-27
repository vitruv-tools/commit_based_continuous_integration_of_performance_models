package cipm.consistency.base.vitruv;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import cipm.consistency.base.core.AbstractCoreTest;
import cipm.consistency.base.core.health.HealthStateObservedComponent;
import cipm.consistency.base.core.mocks.HealthStateMessageSender;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage;
import cipm.consistency.base.models.runtimeenvironment.REModel.REModelPackage;
import cipm.consistency.base.shared.pcm.util.PCMUtils;
import cipm.consistency.base.vsum.domains.ExtendedPcmDomainProvider;
import cipm.consistency.base.vsum.domains.InstrumentationModelDomainProvider;
import cipm.consistency.base.vsum.domains.RuntimeEnvironmentDomainProvider;
import cipm.consistency.base.vsum.facade.CentralVsumFacade;
import cipm.consistency.base.vsum.manager.VsumManager;
import cipm.consistency.base.vsum.mapping.VsumMappingController;
import cipm.consistency.base.vsum.mapping.VsumMappingPersistence;
import mir.reactions.pcmToREM.PcmToREMChangePropagationSpecification;
import mir.reactions.remToPCM.RemToPCMChangePropagationSpecification;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

@RunWith(SpringRunner.class)
public abstract class VsumManagerTestBase extends AbstractCoreTest {

	@BeforeClass
	public static void initBefore() {
		PCMUtils.loadPCMModels();
		InstrumentationModelPackage.eINSTANCE.eClass();
		REModelPackage.eINSTANCE.eClass();
	}

	@TestConfiguration
	public static class VsumManagerTestConfiguration extends AbstractCoreTest.CoreContextConfiguration {
		@Bean
		public VsumManager vsumManager() {
			return new VsumManager();
		}

		@Bean
		public CentralVsumFacade vsumFacade() {
			return new CentralVsumFacade();
		}

		@Bean
		public VsumMappingPersistence vsumPersistence() {
			return new VsumMappingPersistence();
		}

		@Bean
		public VsumMappingController vsumMappingController() {
			return new VsumMappingController();
		}

		// DOMAINS
		@Bean
		public VitruvDomainProvider<? extends VitruvDomain> pcmDomain() {
			return new ExtendedPcmDomainProvider();
		}

		@Bean
		public VitruvDomainProvider<? extends VitruvDomain> remDomain() {
			return new RuntimeEnvironmentDomainProvider();
		}

		@Bean
		public VitruvDomainProvider<? extends VitruvDomain> immDomain() {
			return new InstrumentationModelDomainProvider();
		}

		// PROPAGATION SPECS
		@Bean
		public AbstractReactionsChangePropagationSpecification remToResEnv() {
			return new PcmToREMChangePropagationSpecification();
		}

		@Bean
		public AbstractReactionsChangePropagationSpecification resEnvToREM() {
			return new RemToPCMChangePropagationSpecification();
		}
	}

	@Autowired
	private HealthStateMessageSender healthStateMsgSender;

	protected void reloadVsum() {
		healthStateMsgSender.sendMessage(HealthStateObservedComponent.MODEL_MANAGER,
				HealthStateObservedComponent.VSUM_MANAGER); // => this triggers the vsum startup
	}

	@Before
	public void startup() {
		setSpecific(null, null, null); // empty models 2
		setPcm(null, null, null, null, null); // empty models

		this.reloadVsum();
	}

}
