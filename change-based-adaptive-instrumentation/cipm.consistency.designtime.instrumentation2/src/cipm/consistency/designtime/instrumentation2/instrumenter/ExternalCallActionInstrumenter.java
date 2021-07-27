package cipm.consistency.designtime.instrumentation2.instrumenter;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation2.ActionStatementMapping;

/**
 * An instrumenter for ExternalCallActions.
 * 
 * @author Martin Armbruster
 */
public class ExternalCallActionInstrumenter extends AbstractInstrumenter {
	protected ExternalCallActionInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
		super(gen);
	}

	@Override
	protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMap) {
		Statement callStatement = statementMap.get(aip.getAction());
		String externalCallId = aip.getAction().getId();
		ExpressionStatement enterSt = StatementsFactory.eINSTANCE.createExpressionStatement();
		IdentifierReference objRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
		objRef.setTarget(this.threadMonitoringVariable);
		MethodCall enterCall = ReferencesFactory.eINSTANCE.createMethodCall();
		enterCall.setTarget(environmentGen.setExternalCallIdMethod);
		createAndAddStringArgument(enterCall, externalCallId);
		
		objRef.setNext(enterCall);
		enterSt.setExpression(objRef);
		callStatement.addBeforeContainingStatement(enterSt);
	}
}
