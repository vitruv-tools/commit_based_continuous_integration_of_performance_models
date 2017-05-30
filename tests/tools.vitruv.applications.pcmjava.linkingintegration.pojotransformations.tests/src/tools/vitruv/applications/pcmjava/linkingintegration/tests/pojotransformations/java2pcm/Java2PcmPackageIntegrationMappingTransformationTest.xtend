package tools.vitruv.applications.pcmjava.linkingintegration.tests.pojotransformations.java2pcm

import org.eclipse.core.resources.ResourcesPlugin
import java.util.Iterator
import java.util.Collection
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import org.palladiosimulator.pcm.repository.RepositoryComponent
import java.util.Set
import org.palladiosimulator.pcm.repository.Repository
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil
import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.CodeIntegrationUtils
import tools.vitruv.applications.pcmjava.linkingintegration.tests.CodeIntegrationTestCBSNamespace
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.Java2PcmIntegrationChangePropagationSpecification
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.Pcm2JavaIntegrationChangePropagationSpecification
import tools.vitruv.applications.pcmjava.tests.pojotransformations.java2pcm.Java2PcmPackageMappingTransformationTest
import org.eclipse.core.resources.IProject

class Java2PcmPackageIntegrationMappingTransformationTest extends Java2PcmPackageMappingTransformationTest {

	val public static String NAME_OF_NOT_INTEGRATED_PACKAGE = "nonIntegratedPackage"
	val public static String NAME_OF_INTEGRATED_CLASS = "IntegratedClass"
	val public static String NAME_OF_INTEGRATED_PACKAGE = "packageInIntegratedArea"
	val public static String INTEGRATED_METHOD_NAME = "integratedMethodName"
	val public static String NON_INTEGRATED_METHOD_NAME = "nonIntegradedMethodName"

	override protected createChangePropagationSpecifications() {
		return #[new Java2PcmIntegrationChangePropagationSpecification(), new Pcm2JavaIntegrationChangePropagationSpecification()];
	}

	new() {
		testProjectCreator = [name | initializeTestProject(name).location.toFile];
	}
	
	override public void beforeTest() {
		super.beforeTest()
	}

	def static private IProject initializeTestProject(String name) {
		val workspace = ResourcesPlugin.getWorkspace()
		val testProjectName = CodeIntegrationTestCBSNamespace.TEST_PROJECT_NAME
		CodeIntegrationUtils.importTestProjectFromBundleData(workspace, testProjectName,
			CodeIntegrationTestCBSNamespace.TEST_BUNDLE_NAME, CodeIntegrationTestCBSNamespace.SOURCE_CODE_PATH)

		val testProject = workspace.getRoot().getProject(testProjectName)
		CodeIntegrationUtils.integratProject(testProject)
		return testProject;
	}

	def protected void assertMessage(int expectedSize, String... expectedMessages) {
		val Collection<String> messageLog = super.getUserInteractor().getMessageLog();
		assertEquals("Size of message log is wrong", expectedSize, messageLog.size());

		val Iterator<String> iterator = messageLog.iterator();
		var int i = 0;
		while (iterator.hasNext()) {
			val String nextMessage = iterator.next();
			val String expectedNexMessage = expectedMessages.get(i++);
			assertTrue(
				"The message '" + nextMessage + "' does not contain the expected message '" + expectedNexMessage + "'.",
				nextMessage.contains(expectedNexMessage));
		}
	}

	def protected void assertNoUserInteractingMessage() {
		assertMessage(0)
	}

	def protected void assertNoComponentWithName(String nameOfComponent) throws Throwable {
		val Set<RepositoryComponent> repoComponents = CorrespondenceModelUtil.
			getAllEObjectsOfTypeInCorrespondences(getCorrespondenceModel(), RepositoryComponent);
		assertNoBasicComponentWithName(nameOfComponent, repoComponents);
		val Set<Repository> repos = CorrespondenceModelUtil.
			getAllEObjectsOfTypeInCorrespondences(getCorrespondenceModel(), Repository);
		repos.forEach[assertNoBasicComponentWithName(nameOfComponent, it.getComponents__Repository())];
	}

	def private void assertNoBasicComponentWithName(String nameOfComponent,
		Iterable<RepositoryComponent> repoComponents) {
		for (RepositoryComponent repoComponent : repoComponents) {
			assertTrue("basic component with name " + nameOfComponent + " found: " + repoComponent,
				!repoComponent.getEntityName().contains(nameOfComponent))
		}
	}

}
