package tools.vitruv.applications.pcmjava.modelrefinement.tests.instrumentation;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;

import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.util.CodeInstrumentationUtil;

public class AssertSourceCodeHelper {
	private static String expectedInternalActionInstrumentation(String internalActionID, String internalActionResourceId,
			String serviceId, String internalActionBody) {
		String code ="public void httpDownload() {\r\n" + 
				"		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();\r\n" + 
				"		ServiceParameters __serviceParameters_0 = serviceParamtersFactory\r\n" + 
				"				.getServiceParameters(new Object[] {});\r\n" + 
				"		ThreadMonitoringController.getInstance().enterService(\"" + serviceId + "\",\r\n" + 
				"				__serviceParameters_0);\r\n" + 
				"		final long __tin_0 = ThreadMonitoringController.getInstance().getTime();\r\n" + 
				"		" + internalActionBody + "\r\n" + 
				"		ThreadMonitoringController.getInstance().logResponseTime(\"" + internalActionID + "\", "
						+ "\"" + internalActionResourceId + "\",\r\n" + 
				"				__tin_0);\r\n" + 
				"		ThreadMonitoringController.getInstance().exitService();\r\n" + 
				"	}";
		
		return code.replaceAll("\\s+","");
	}
	
	
	private static String expectedBranchActionInstrumentation(String branchId, String executedBranchId,
			String externalCallId, String serviceId) {
		
		String code = "public void httpDownload() {\r\n" + 
				"		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();\r\n" + 
				"		ServiceParameters __serviceParameters_0 = serviceParamtersFactory\r\n" + 
				"				.getServiceParameters(new Object[] {});\r\n" + 
				"		ThreadMonitoringController.getInstance().enterService(\"" + serviceId + "\",\r\n" + 
				"				__serviceParameters_0);\r\n" + 
				"		String __executedBranch_0 = null;\r\n" + 
				"		if (i < 10) {\r\n" + 
				"			__executedBranch_0 = \"" + executedBranchId + "\";\r\n" + 
				"			ThreadMonitoringController.setCurrentCallerId(\"" + externalCallId + "\");\r\n" + 
				"			iMediaStore.download();\r\n" + 
				"		}\r\n" + 
				"		ThreadMonitoringController.getInstance().logBranchExecution(\"" + branchId + "\",\r\n" + 
				"				__executedBranch_0);\r\n" + 
				"		ThreadMonitoringController.getInstance().exitService();\r\n" + 
				"	}";
		
		return code.replaceAll("\\s+","");
	}
	
	
	private static String expectedLoopActionInstrumentation(String loopActionId, String externalCallId, String serviceId) {
		String code = "public void httpDownload() {\r\n" + 
				"		ServiceParamtersFactory serviceParamtersFactory = new ServiceParametersFactoryImp();\r\n" + 
				"		ServiceParameters __serviceParameters_0 = serviceParamtersFactory\r\n" + 
				"				.getServiceParameters(new Object[] {});\r\n" + 
				"		ThreadMonitoringController.getInstance().enterService(\"" + serviceId + "\",\r\n" + 
				"				__serviceParameters_0);\r\n" + 
				"		long __counter_0 = 0;\r\n" + 
				"		for (int i = 0; i < 10; i++) {\r\n" + 
				"			__counter_0 ++;\r\n" + 
				"			ThreadMonitoringController.setCurrentCallerId(\"" + externalCallId + "\");\r\n" + 
				"			iMediaStore.download();\r\n" + 
				"		}\r\n" + 
				"		ThreadMonitoringController.getInstance().logLoopIterationCount(\"" + loopActionId + "\",\r\n" + 
				"				__counter_0);\r\n" + 
				"		ThreadMonitoringController.getInstance().exitService();\r\n" + 
				"	}";
		
		return code.replaceAll("\\s+","");
	}
	
	
	public static String getExpectedLoopActionInstrumentation(ResourceDemandingBehaviour seff) {
		String expectedBranchActionInstrumentation = "";
		List<AbstractAction> listAbstractActions = getListAbstractActionWithoutStartStopAction(seff);
		if(listAbstractActions.size() == 0) {
			fail("No SEFF was fount");
		}
		else if(listAbstractActions.size() != 1){
			fail("Only a Loo action can be test");
		}
		else {
			LoopAction loopAction = (LoopAction) listAbstractActions.get(0);
			ExternalCallAction externalCallAction = getExtenalCallFromLoopAtion(loopAction);
			
			String serviceId = seff.getId();
			String loopActionId = loopAction.getId();
			String externalCallId = externalCallAction.getId();
			
			expectedBranchActionInstrumentation = 
					expectedLoopActionInstrumentation(loopActionId, externalCallId, serviceId);
			
		}
		
		return expectedBranchActionInstrumentation;
	}
	
	
	public static String getExpectedBranchActionInstrumentation(ResourceDemandingBehaviour seff) throws JavaModelException {
		String expectedBranchActionInstrumentation = "";
		List<AbstractAction> listAbstractActions = getListAbstractActionWithoutStartStopAction(seff);
		if(listAbstractActions.size() == 0) {
			fail("No SEFF was fount");
		}
		else if(listAbstractActions.size() != 1){
			fail("Only a branch action can be test");
		}
		else {
			BranchAction branchAction = (BranchAction) listAbstractActions.get(0);
			ExternalCallAction branchExternalCall = getExternalCallFromBranchAction(branchAction);
			
			String serviceId = seff.getId();
			String branchId = branchAction.getId();
			String externalCallId = branchExternalCall.getId();
			String executedBranchId =  CodeInstrumentationUtil.getExecutedBranchId(branchAction);
			
			expectedBranchActionInstrumentation = expectedBranchActionInstrumentation(branchId,
					executedBranchId, externalCallId, serviceId);	
		}
		
		return expectedBranchActionInstrumentation;
	}
	
	
	public static String getExpectedInternalActionInstrumentation(ResourceDemandingBehaviour seff, String internalActionBody) throws JavaModelException {
		String expectedInternalActionInstrumetation = "";

		List<AbstractAction> listAbstractActions = getListAbstractActionWithoutStartStopAction(seff);
		if(listAbstractActions.size() == 0) {
			fail("No SEFF was fount");
		}
		else if(listAbstractActions.size() != 1){
			fail("Only an internal action is expected to instrumented");
		}
		else if(!(listAbstractActions.get(0) instanceof InternalAction)){
			fail("No Internal Action was found");
		}
		else {
			InternalAction internalAction = (InternalAction) listAbstractActions.get(0);
			
			String internalActionId =  internalAction.getId();
			String internalActionResouceId = CodeInstrumentationUtil.getInternalActionResourceId(internalAction);
			String serviceId = seff.getId();
			
			expectedInternalActionInstrumetation = expectedInternalActionInstrumentation(internalActionId,
					internalActionResouceId, serviceId, internalActionBody);
			
			
		}
		
		return expectedInternalActionInstrumetation;
	}
	
	
	public static String renameRandomVariableFromInstrumentedCode(String instrumentedCode) {
		// start time variable
		instrumentedCode = renameRandomVariable("__tin", "__tin_0", instrumentedCode);
		
		// serviceParameter variable
		instrumentedCode = renameRandomVariable("__serviceParameters", "__serviceParameters_0", instrumentedCode);
		
		// executed branch id
		instrumentedCode = renameRandomVariable("__executedBranch", "__executedBranch_0", instrumentedCode);
		
		// loop execution count
		instrumentedCode = renameRandomVariable("__counter", "__counter_0", instrumentedCode);
		
		return instrumentedCode.replaceAll("\\s+","");
	}
	
	
	private static String renameRandomVariable(String randomVariableNameStart, String newName, String instrumentedCode) {
		for(String codeWord: instrumentedCode.split(" ")) {
			if(codeWord.startsWith(randomVariableNameStart)) {
				if(codeWord.endsWith("++")) {
					instrumentedCode = instrumentedCode.replaceAll(codeWord, newName + "++");
				}
				else {
					instrumentedCode = instrumentedCode.replaceAll(codeWord, newName);
				}
				
			}
		}
		return instrumentedCode;
	}
	
	
	private static List<AbstractAction> getListAbstractActionWithoutStartStopAction(ResourceDemandingBehaviour seff){
		List<AbstractAction> listWithoutStartStopActions = new ArrayList<AbstractAction>();
		List<AbstractAction> listAbstractActions = seff.getSteps_Behaviour();
		
		for(AbstractAction abstractAction: listAbstractActions) {
			if(!(abstractAction instanceof StartAction || abstractAction instanceof StopAction)) {
				listWithoutStartStopActions.add(abstractAction);
			}
		}
		
		return listWithoutStartStopActions;
	}
	
	
	private static ExternalCallAction getExtenalCallFromLoopAtion(LoopAction loopAction) {
		ExternalCallAction externalCallAction = null;
		for(AbstractAction abstractAction: loopAction.getBodyBehaviour_Loop().getSteps_Behaviour()) {
			if(abstractAction instanceof ExternalCallAction) {
				externalCallAction = (ExternalCallAction) abstractAction;
				break;
			}
		}
		return externalCallAction;
	}
	
	
	private static ExternalCallAction getExternalCallFromBranchAction(BranchAction branchAction) {
		ExternalCallAction externalCallAction = null;
		if(branchAction.getBranches_Branch().size() != 0) {
			AbstractBranchTransition abstractBranchTransition = branchAction.getBranches_Branch().get(0);
			for(AbstractAction abstractAction: abstractBranchTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour()) {
				if(abstractAction instanceof ExternalCallAction) {
					externalCallAction = (ExternalCallAction) abstractAction;
					break;
				}
			}
		}
		return externalCallAction;
	}
	
	
}
