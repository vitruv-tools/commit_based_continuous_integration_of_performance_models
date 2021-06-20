package tools.vitruv.applications.pcmjava.instrumentation.instrumenter;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsFactory;

/**
 * An instrumenter for ExternalCallActions.
 * 
 * @author Martin Armbruster
 */
public class ExternalCallActionInstrumenter extends AbstractInstrumenter {
	/**
	 * Instruments an ExternalCallAction.
	 * 
	 * @param callStatement statement of the external call.
	 * @param externalCallId id of the external call.
	 */
	public void instrumentExternalCallAction(Statement callStatement, String externalCallId) {
		ExpressionStatement enterSt = StatementsFactory.eINSTANCE.createExpressionStatement();
		IdentifierReference objRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
		objRef.setTarget(this.threadMonitoringVariable);
		MethodCall enterCall = ReferencesFactory.eINSTANCE.createMethodCall();
		enterCall.setTarget(setExternalCallIdMethod);
		createAndAddStringArgument(enterCall, externalCallId);
		
		objRef.setNext(enterCall);
		enterSt.setExpression(objRef);
		callStatement.addBeforeContainingStatement(enterSt);
	}
}
