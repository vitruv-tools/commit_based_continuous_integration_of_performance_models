package tools.vitruv.applications.pcmjava.instrumentation.instrumenter;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.references.StringReference;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.TypesFactory;
import org.emftext.language.java.variables.LocalVariable;

/**
 * An abstract instrumenter.
 * 
 * @author Martin Armbruster
 */
public abstract class AbstractInstrumenter {
	protected MinimalMonitoringEnvironmentModelGenerator environmentGen;
	protected LocalVariable threadMonitoringVariable;
	
	protected void createAndAddStringArgument(MethodCall call, String s) {
		StringReference ref = ReferencesFactory.eINSTANCE.createStringReference();
		ref.setValue(s);
		call.getArguments().add(ref);
	}
	
	protected NamespaceClassifierReference createTypeReference(ConcreteClassifier classifier) {
		NamespaceClassifierReference result = TypesFactory.eINSTANCE.createNamespaceClassifierReference();
		result.getNamespaces().addAll(classifier.getContainingCompilationUnit().getNamespaces());
		ClassifierReference actualRef = TypesFactory.eINSTANCE.createClassifierReference();
		actualRef.setTarget(classifier);
		result.getClassifierReferences().add(actualRef);
		return result;
	}
}
