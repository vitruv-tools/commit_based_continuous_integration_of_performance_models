package tools.vitruv.applications.pcmjava.seffstatements.tests.ejbtransformations

import org.emftext.language.java.classifiers.ConcreteClassifier
import org.palladiosimulator.pcm.repository.BasicComponent
import org.palladiosimulator.pcm.repository.OperationInterface
import org.palladiosimulator.pcm.repository.OperationRequiredRole
import org.palladiosimulator.pcm.repository.Repository
import tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm.EjbClassMappingTest
import tools.vitruv.applications.pcmjava.tests.ejbtransformations.java2pcm.EjbJava2PcmTransformationTest
import org.junit.Test
import org.junit.Ignore
import tools.vitruv.applications.pcmjava.tests.util.Pcm2JavaTestUtils
import tools.vitruv.applications.pcmjava.seffstatements.ejbtransformations.Java2PcmEjbWithSeffstatmentsChangePropagationSpecification
import tools.vitruv.applications.pcmjava.seffstatements.tests.pojotransformations.IncrementalSeffReconstructionTest

class EjbIncrementalSeffReconstructionTest extends IncrementalSeffReconstructionTest {

	override protected createChangePropagationSpecifications() {
		return #[new Java2PcmEjbWithSeffstatmentsChangePropagationSpecification()];
	}

	override public void beforeTest() {
		super.beforeTest()
		super.setWebGUIPackageName = Pcm2JavaTestUtils.REPOSITORY_NAME
	}

	@Ignore
	@Test
    public override void testComponentInternalCallInSameComponent() throws Throwable {
    }
    
    @Ignore
    @Test
    public override void testComponentInternalCallWithExternalCallInSameComponent() throws Throwable {
    }

	override protected createMediaStoreViaCode() {
		// create main package
		val Repository repo = super.addRepoContractsAndDatatypesPackage();

		// create packages and component implementing classes
		val ConcreteClassifier classifier = super.createClassInPackage(this.mainPackage,
			IncrementalSeffReconstructionTest.MEDIA_STORE_CLASSNAME) as ConcreteClassifier
		this.addAnnotationToClassifier(classifier, EjbClassMappingTest.STATELESS_ANNOTATION_NAME, BasicComponent,
			IncrementalSeffReconstructionTest.MEDIA_STORE_CLASSNAME)

		val ConcreteClassifier webGUIclassifier = super.createClassInPackage(this.mainPackage,
			IncrementalSeffReconstructionTest.WEBGUI_CLASSNAME) as ConcreteClassifier
		this.addAnnotationToClassifier(webGUIclassifier, EjbClassMappingTest.STATELESS_ANNOTATION_NAME, BasicComponent,
			IncrementalSeffReconstructionTest.WEBGUI_CLASSNAME)

		val String webGuiInterfaceName = "I" + IncrementalSeffReconstructionTest.WEBGUI;
		val String mediaStoreInterfaceName = "I" + IncrementalSeffReconstructionTest.MEDIA_STORE;

		// create interfaces
		val webGuiIf = super.
			createInterfaceInPackageBasedOnJaMoPPPackageWithoutCorrespondence(Pcm2JavaTestUtils.REPOSITORY_NAME,
				webGuiInterfaceName)
		this.addAnnotationToClassifier(webGuiIf, EjbJava2PcmTransformationTest.REMOTE_ANNOTATION_NAME,
			OperationInterface, webGuiInterfaceName)

		val mediaStoreIf = super.
			createInterfaceInPackageBasedOnJaMoPPPackageWithoutCorrespondence(Pcm2JavaTestUtils.REPOSITORY_NAME,
				mediaStoreInterfaceName)
		this.addAnnotationToClassifier(mediaStoreIf, EjbJava2PcmTransformationTest.REMOTE_ANNOTATION_NAME,
			OperationInterface, mediaStoreInterfaceName)

		val String uploadMethodName = "upload";
		val String downloadMethodName = "download";

		val String httpDownloadMethodName = "httpDownload";
		val String httpUploadMethodName = "httpUpload";

		// create interface methods
		this.httpDownloadOpSig = super.addMethodToInterfaceWithCorrespondence(webGuiInterfaceName,
			httpDownloadMethodName);
		this.httpUploadOpSig = super.addMethodToInterfaceWithCorrespondence(webGuiInterfaceName, httpUploadMethodName);
		this.uploadOpSig = super.addMethodToInterfaceWithCorrespondence(mediaStoreInterfaceName, uploadMethodName);
		this.downloadOpSig = super.addMethodToInterfaceWithCorrespondence(mediaStoreInterfaceName, downloadMethodName);

		// create implements
		super.addImplementsCorrespondingToOperationProvidedRoleToClass(IncrementalSeffReconstructionTest.WEBGUI_CLASSNAME, webGuiInterfaceName);
		super.addImplementsCorrespondingToOperationProvidedRoleToClass(IncrementalSeffReconstructionTest.MEDIA_STORE_CLASSNAME, mediaStoreInterfaceName);

		// create class methods in component implementing classes in order to create SEFF
		// correspondences
		this.addClassMethodToClassThatOverridesInterfaceMethod(IncrementalSeffReconstructionTest.WEBGUI_CLASSNAME, httpUploadMethodName);
		this.addClassMethodToClassThatOverridesInterfaceMethod(IncrementalSeffReconstructionTest.WEBGUI_CLASSNAME, httpDownloadMethodName);
		this.addClassMethodToClassThatOverridesInterfaceMethod(IncrementalSeffReconstructionTest.MEDIA_STORE_CLASSNAME, uploadMethodName);
		this.addClassMethodToClassThatOverridesInterfaceMethod(IncrementalSeffReconstructionTest.MEDIA_STORE_CLASSNAME, downloadMethodName);

		// create requiredRole from webgui to IMediaStore
		this.addFieldToClassWithName(IncrementalSeffReconstructionTest.WEBGUI_CLASSNAME, mediaStoreInterfaceName, "i" + IncrementalSeffReconstructionTest.MEDIA_STORE, null);
		this.webGUIRequiresIMediaStoreRole = super.addAnnotationToField("i" + IncrementalSeffReconstructionTest.MEDIA_STORE,
			EjbClassMappingTest.EJB_FIELD_ANNOTATION_NAME, OperationRequiredRole, IncrementalSeffReconstructionTest.WEBGUI_CLASSNAME)
		return repo;
	}

}
