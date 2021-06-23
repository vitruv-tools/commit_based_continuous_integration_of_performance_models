package tools.vitruv.applications.pcmjava.instrumentation.instrumenter;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;

/**
 * An instrumenter for InternalActions.
 * 
 * @author Martin Armbruster
 */
public class InternalActionInstrumenter extends AbstractInstrumenter {
	/**
	 * Instruments an InternalAction.
	 * 
	 * @param start the starting statement of the InternalAction.
	 * @param end the ending statement of the InternalAction.
	 * @param correspondingInternalActionId
	 */
	public void instrumentInternalAction(Statement start, Statement end, String correspondingInternalActionId) {
		// Entry.
		IdentifierReference objRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
		objRef.setTarget(this.threadMonitoringVariable);
		MethodCall entryCall = ReferencesFactory.eINSTANCE.createMethodCall();
		objRef.setNext(entryCall);
		createArguments(entryCall, correspondingInternalActionId);
		entryCall.setTarget(environmentGen.enterInternalActionMethod);
		
		ExpressionStatement entryStatement = StatementsFactory.eINSTANCE.createExpressionStatement();
		entryStatement.setExpression(objRef);
		start.addBeforeContainingStatement(entryStatement);
		
		// Exit.
		objRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
		objRef.setTarget(this.threadMonitoringVariable);
		MethodCall exitCall = ReferencesFactory.eINSTANCE.createMethodCall();
		objRef.setNext(exitCall);
		createArguments(exitCall, correspondingInternalActionId);
		exitCall.setTarget(environmentGen.exitInternalActionMethod);
		
		ExpressionStatement exitStatement = StatementsFactory.eINSTANCE.createExpressionStatement();
		exitStatement.setExpression(objRef);
		end.addAfterContainingStatement(exitStatement);
	}
	
	private void createArguments(MethodCall call, String correspondingInternalActionId) {
		createAndAddStringArgument(call, correspondingInternalActionId);
		createAndAddStringArgument(call, ApplicationProjectInstrumenterNamespace.RESOURCE_ID_CPU);
	}
}
