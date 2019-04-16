package tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator;

import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.util.CodeInstrumentationUtil;

public class InstrumentationStatements {
	
	public static MonitoringStatementInternalAction getInternalActionInstrumentationCode(String internalActionID,
			String resourceId) {
		MonitoringStatementInternalAction monitoringStatementInternalAction =  new MonitoringStatementInternalAction();
		StringBuilder beforeExecution = new StringBuilder();
		StringBuilder afterExecution = new StringBuilder();
		// Start Time variable
		String tin = "__tin_" + CodeInstrumentationUtil.getUUID();

		
		beforeExecution.append(System.lineSeparator());
		beforeExecution.append("\n final long " + tin + " = ThreadMonitoringController.getInstance().getTime();");

        afterExecution.append("\n ThreadMonitoringController.getInstance().logResponseTime(\"" + internalActionID + "\", "
        		+ " \"" + resourceId + "\"," + tin + ");");
        
        //
        monitoringStatementInternalAction.setBeforeExecution(beforeExecution.toString());
        monitoringStatementInternalAction.setAfterExecution(afterExecution.toString());
		
		return monitoringStatementInternalAction;
	}
	
	public static MonitoringStatementOperation getOperationInstrumentationCode(String serviceId, 
			String[] serviceParametersNames) {	
		
		MonitoringStatementOperation monitoringStatementOperation =  new MonitoringStatementOperation();
		StringBuilder beforeExecution = new StringBuilder();
		StringBuilder afterExecution = new StringBuilder();
		
        // service parameters
        String serviceParameters = "__serviceParameters" + CodeInstrumentationUtil.getUUID();
		
		beforeExecution.append(System.lineSeparator());
		
		// service parameters
		String serviceParametersToString = serviceParamsNamesToString(serviceParametersNames, serviceParameters);
		beforeExecution.append(" \n ServiceParametersFactory serviceParametersFactory = new ServiceParametersFactoryImp();");
		beforeExecution.append(" \n " + serviceParametersToString + "; \n");
		beforeExecution.append("\n ThreadMonitoringController.getInstance().enterService(\"" + serviceId + "\", " + serviceParameters + ");");
		
		afterExecution.append("\n ThreadMonitoringController.getInstance().exitService();");
        //
        monitoringStatementOperation.setBeforeExecution(beforeExecution.toString());
        monitoringStatementOperation.setAfterExecution(afterExecution.toString());
		
		return monitoringStatementOperation;	
	}
	
	
	public static MonitoringStatementLoop getLoopActionInstrumentationCode(String serviceExecutionId, String loopActionId ) {
		MonitoringStatementLoop monitoringStatementLoop =  new MonitoringStatementLoop();
		String counter = "__counter_" + CodeInstrumentationUtil.getUUID();
		
		String beforeExecution = "\n long " + counter + " = 0;";
		String inBlock = "\n " + counter + " ++;";
		String afterExecution = "ThreadMonitoringController.getInstance().logLoopIterationCount(\"" + serviceExecutionId + "\", "
				+ " " + counter + ");";
		
		monitoringStatementLoop.setBeforeExecution(beforeExecution);
		monitoringStatementLoop.setInBlock(inBlock);
		monitoringStatementLoop.setAfterExecution(afterExecution);
		
		return monitoringStatementLoop;
	}
	
	public static MonitoringStatementBranch getBranchActionInstrumentationCode(String branchId, String executedBranchId) {
		MonitoringStatementBranch monitoringStatementBranch =  new MonitoringStatementBranch();
		String executedBranch = "__executedBranch_" + CodeInstrumentationUtil.getUUID();
		
		String beforeExecution = "\n String " + executedBranch + " = null;";
		String inBlock = "\n " + executedBranch + " =\"" + executedBranchId + "\";";
		String afterExecution = "ThreadMonitoringController.getInstance().logBranchExecution(\"" + branchId + "\", " + executedBranch + ");";
		
		
		monitoringStatementBranch.setBeforeExecution(beforeExecution);
		monitoringStatementBranch.setInBlock(inBlock);
		monitoringStatementBranch.setAfterExecution(afterExecution);
		
		return monitoringStatementBranch;
	}
	
	
	public static String getSetCurrentCallerIdStatement(String currentCallerID) {
		String code = "\n ThreadMonitoringController.getInstance().setCurrentCallerId(\"" + currentCallerID +"\");";
		return code;
	}
	
	
	private static String serviceParamsNamesToString(String[] serviceParametersNames, String variableName) {
		if(serviceParametersNames ==  null || serviceParametersNames.length == 0) {
			return "\n ServiceParameters serviceParamtersFactory ="
					+ " serviceParametersFactory.getServiceParameters(new Object[]{}, new String[]{})";
		}
		
    	String serviceParamsNamesToString = "{\"" + String.join("\",\"", serviceParametersNames) + "\"}";
    	String serviceParametersNamesJoined = "{" + String.join(",", serviceParametersNames) + "}";
		
		String primitiveValueExtracter = "\n ServiceParameters " + variableName + " ="
				+ " serviceParametersFactory.getServiceParameters(new Object[]" + serviceParametersNamesJoined + 
				", new String[]" + serviceParamsNamesToString + ")";
		
		return primitiveValueExtracter;
    }
	
	
}
