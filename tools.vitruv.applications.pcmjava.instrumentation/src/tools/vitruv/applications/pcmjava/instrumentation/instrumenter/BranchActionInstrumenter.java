package tools.vitruv.applications.pcmjava.instrumentation.instrumenter;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.Block;
import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementsFactory;

/**
 * An instrumenter for BranchActions.
 * 
 * @author Martin Armbruster
 */
public class BranchActionInstrumenter extends AbstractInstrumenter {
	/**
	 * Instruments a BranchAction.
	 * 
	 * @param branchSt the branch statement.
	 * @param transitionId id for the transition.
	 */
	public void instrumentBranchAction(Statement branchSt, String transitionId) {
		if (branchSt instanceof StatementContainer
				&& ((StatementContainer) branchSt).getStatement() instanceof Block) {
			ExpressionStatement enterSt = StatementsFactory.eINSTANCE.createExpressionStatement();
			IdentifierReference idRef = ReferencesFactory.eINSTANCE.createIdentifierReference();
			idRef.setTarget(this.threadMonitoringVariable);
			MethodCall enterCall = ReferencesFactory.eINSTANCE.createMethodCall();
			enterCall.setTarget(enterBranchMethod);
			this.createAndAddStringArgument(enterCall, transitionId);
			
			idRef.setNext(enterCall);
			enterSt.setExpression(idRef);
			((Block) ((StatementContainer) branchSt).getStatement()).getStatements().add(0, enterSt);
		}
	}
}
