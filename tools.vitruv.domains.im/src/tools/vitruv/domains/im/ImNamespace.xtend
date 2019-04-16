package tools.vitruv.domains.im

import org.eclipse.emf.ecore.EPackage
import tools.vitruv.models.im.ImPackage

class ImNamespace {
	
	private new() {
	}
	
	// file extensions
	public static final String FILE_EXTENSION = "im";
	
	// MM Namespaces
	public static final EPackage ROOT_PACKAGE = ImPackage.eINSTANCE;
	public static final String METAMODEL_NAMESPACE = ImPackage.eNS_URI;
}