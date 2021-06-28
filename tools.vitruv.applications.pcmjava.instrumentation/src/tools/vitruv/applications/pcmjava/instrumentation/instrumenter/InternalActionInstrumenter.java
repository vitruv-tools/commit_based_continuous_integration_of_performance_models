package tools.vitruv.applications.pcmjava.instrumentation.instrumenter;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;
import tools.vitruv.applications.pcmjava.instrumentation.ActionStatementMapping;

/**
 * An instrumenter for InternalActions.
 * 
 * @author Martin Armbruster
 */
public class InternalActionInstrumenter extends AbstractInstrumenter {
	protected InternalActionInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
		super(gen);
	}

	@Override
	protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMap) {
		Statement start = statementMap.get(aip.getAction());
		Statement end = statementMap.getAbstractActionToLastStatementMapping().get(aip.getAction());
		String correspondingInternalActionId = aip.getAction().getId();
		
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
