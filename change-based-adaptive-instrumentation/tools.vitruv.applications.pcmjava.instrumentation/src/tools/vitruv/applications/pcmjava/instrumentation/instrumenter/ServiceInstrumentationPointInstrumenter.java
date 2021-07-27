package tools.vitruv.applications.pcmjava.instrumentation.instrumenter;

import java.util.EnumMap;

import org.emftext.language.java.members.Method;
import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.MethodCall;
import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.variables.VariablesFactory;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationType;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterNamespace;
import tools.vitruv.applications.pcmjava.instrumentation.ActionStatementMapping;

/**
 * An instrumenter for ServiceInstrumentationPoints.
 * 
 * @author Martin Armbruster
 */
public class ServiceInstrumentationPointInstrumenter extends AbstractInstrumenter {
	private ServiceInstrumenter serviceIns;
	private EnumMap<InstrumentationType, AbstractInstrumenter> aipTypeToInstrumenter;
	
	public ServiceInstrumentationPointInstrumenter(MinimalMonitoringEnvironmentModelGenerator gen) {
		super(gen);
		serviceIns = new ServiceInstrumenter(this.environmentGen);
		aipTypeToInstrumenter = new EnumMap<>(InstrumentationType.class);
		aipTypeToInstrumenter.put(InstrumentationType.BRANCH,
				new BranchActionInstrumenter(this.environmentGen));
		aipTypeToInstrumenter.put(InstrumentationType.EXTERNAL_CALL,
				new ExternalCallActionInstrumenter(this.environmentGen));
		aipTypeToInstrumenter.put(InstrumentationType.INTERNAL,
				new InternalActionInstrumenter(this.environmentGen));
		aipTypeToInstrumenter.put(InstrumentationType.LOOP,
				new LoopActionInstrumenter(this.environmentGen));
	}

	/**
	 * Instruments a ServiceInstrumentationPoint.
	 * 
	 * @param m the method to instrument.
	 * @param sip the ServiceInstrumentationPoint.
	 * @param statementMapping a mapping to retrieve statements corresponding to
	 *                         AbstractActions within the ServiceInstrumentationPoint.
	 * @param adaptive true if only active instrumentation points shall be instrumented. false otherwise.
	 */
	public void instrument(Method m, ServiceInstrumentationPoint sip, ActionStatementMapping statementMapping,
			boolean adaptive) {
		prepareMethodBeforeInstrumentation(m);
		
		serviceIns.setLocalThreadMonitoringVariable(this.threadMonitoringVariable);
		serviceIns.setService(m, sip.getService().getId());
		serviceIns.instrument(null, null);
		
		for (ActionInstrumentationPoint aip : sip.getActionInstrumentationPoints()) {
			if (aip.isActive() || !adaptive) {
				instrument(aip, statementMapping);
			}
		}
		
		prepareMethodAfterInstrumentation(m);
	}
	
	private void prepareMethodBeforeInstrumentation(Method m) {
		IdentifierReference init = ReferencesFactory.eINSTANCE.createIdentifierReference();
		init.setTarget(environmentGen.threadMonitoringControllerClassifier);
		MethodCall initCall = ReferencesFactory.eINSTANCE.createMethodCall();
		initCall.setTarget(environmentGen.getInstanceMethod);
		init.setNext(initCall);
		
		threadMonitoringVariable = VariablesFactory.eINSTANCE.createLocalVariable();
		threadMonitoringVariable.setTypeReference(this.createTypeReference(environmentGen.threadMonitoringControllerClassifier));
		threadMonitoringVariable.setName(ApplicationProjectInstrumenterNamespace.THREAD_MONITORING_CONTROLLER_VARIABLE);
		threadMonitoringVariable.setInitialValue(init);
	}
	
	private void prepareMethodAfterInstrumentation(Method m) {
		LocalVariableStatement threadVarStat = StatementsFactory.eINSTANCE.createLocalVariableStatement();
		threadVarStat.setVariable(threadMonitoringVariable);
		m.getStatements().add(0, threadVarStat);
	}
	
	@Override
	protected void instrument(ActionInstrumentationPoint aip, ActionStatementMapping statementMapping) {
		AbstractInstrumenter actionInstrumenter = aipTypeToInstrumenter.get(aip.getType());
		if (actionInstrumenter != null) {
			actionInstrumenter.setLocalThreadMonitoringVariable(this.threadMonitoringVariable);
			actionInstrumenter.instrument(aip, statementMapping);
		}
	}
}
