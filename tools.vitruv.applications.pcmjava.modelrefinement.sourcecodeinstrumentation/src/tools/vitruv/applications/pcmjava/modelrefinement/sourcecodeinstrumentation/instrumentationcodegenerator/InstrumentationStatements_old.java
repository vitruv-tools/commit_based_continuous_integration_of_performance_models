package tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator;

import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.util.CodeInstrumentationUtil;

public class InstrumentationStatements_old {

	public static MonitoringStatementInternalAction getInternalActionInstrumentationCode(String internalActionID,
			String serviceExecutionId) {
		MonitoringStatementInternalAction monitoringStatementInternalAction =  new MonitoringStatementInternalAction();
		StringBuilder beforeExecution = new StringBuilder();
		StringBuilder afterExecution = new StringBuilder();
		// Start Time variable
		String tin = "__tin_" + CodeInstrumentationUtil.getUUID();
		
		// End Time Variable
		String tout = "__tout_" + CodeInstrumentationUtil.getUUID();
		// Internal Action record variable
		String internalActionRecord = "__internalActionRecord_" + CodeInstrumentationUtil.getUUID();
		
		beforeExecution.append(System.lineSeparator());
		beforeExecution.append("\n final long " + tin + " = MonitoringFactory.getMonitoringController().getTimeSource().getTime();");

        afterExecution.append("\n final long " + tout + " = MonitoringFactory.getMonitoringController().getTimeSource().getTime();");
        afterExecution.append(System.lineSeparator());
        afterExecution.append("\n final InternalActionRecord " + internalActionRecord+ " = MonitoringFactory.getInternalActionRecord(\"" + serviceExecutionId +
        		"\",\"" + internalActionID + "\"," + tin + "," + tout + ");");
        afterExecution.append(System.lineSeparator());
        afterExecution.append("\n MonitoringFactory.getMonitoringController().newMonitoringRecord(" + internalActionRecord + ");");
        
        //
        monitoringStatementInternalAction.setBeforeExecution(beforeExecution.toString());
        monitoringStatementInternalAction.setAfterExecution(afterExecution.toString());
		
		return monitoringStatementInternalAction;
	}
	
	
	public static MonitoringStatementOperation getOperationInstrumentationCode(String serviceId, String serviceExecutionId, 
			String[] serviceParametersNames, String callerServiceExecutionId, String callerId) {	
		
		MonitoringStatementOperation monitoringStatementOperation =  new MonitoringStatementOperation();
		StringBuilder beforeExecution = new StringBuilder();
		StringBuilder afterExecution = new StringBuilder();
		
        // service parameters
        String serviceParameters = "__serviceParameters" + CodeInstrumentationUtil.getUUID();
        
		// Start Time variable
		String tin = "__tin_" + CodeInstrumentationUtil.getUUID();
		
		// End Time Variable
		String tout = "__tout_" + CodeInstrumentationUtil.getUUID();
		
		// Internal Action record variable
		String operationRecord = "__operationRecord_" + CodeInstrumentationUtil.getUUID();
		
		beforeExecution.append(System.lineSeparator());
		
		if(serviceParametersNames == null) {
			beforeExecution.append("\n String " + serviceParameters + " = \"{}\";");
		}
		else {
			if(serviceParametersNames.length == 0) {
				beforeExecution.append("\n String " + serviceParameters + " = \"{}\";");
			}
			else {
				String serviceParametersToString = serviceParamsNamesToString(serviceParametersNames, serviceParameters);
				//String serviceParametersValues = String.join(",", serviceParametersNames);
				//beforeExecution.append(" \n " + serviceParametersToString + "; \n");
				//beforeExecution.append("\n final String " + serviceParameters + " = MonitoringFactory.getServiceParameters(__serviceParametersNames, " + serviceParametersValues + ");\n");
			}
		}

		beforeExecution.append("\n final long " + tin + " = MonitoringFactory.getMonitoringController().getTimeSource().getTime();");
		
		afterExecution.append("\n final long " + tout + " = MonitoringFactory.getMonitoringController().getTimeSource().getTime();");
        afterExecution.append(System.lineSeparator());
        afterExecution.append("\n final ServiceRecord " + operationRecord + " = MonitoringFactory.getServiceRecord(\"" + serviceExecutionId  + "\",\"" +
        serviceId + "\"," + serviceParameters +",\"" +  callerServiceExecutionId + "\",\"" + callerId + "\"," + tin + "," +  tout + ");");
        afterExecution.append(System.lineSeparator());
        afterExecution.append("\n MonitoringFactory.getMonitoringController().newMonitoringRecord(" + operationRecord + ");");        
        
        //
        monitoringStatementOperation.setBeforeExecution(beforeExecution.toString());
        monitoringStatementOperation.setAfterExecution(afterExecution.toString());
		
		return monitoringStatementOperation;	
	}
	
	
	public static MonitoringStatementLoop getLoopActionInstrumentationCode(String serviceExecutionId, String loopActionId ) {
		MonitoringStatementLoop monitoringStatementLoop =  new MonitoringStatementLoop();
		String counter = "__counter_" + CodeInstrumentationUtil.getUUID();
		String loopActionRecord = "__loopActionRecord_" + CodeInstrumentationUtil.getUUID();
		
		String beforeExecution = "\n long " + counter + " = 0;";
		String inBlock = "\n " + counter + "++;";
		String afterExecution = "\n final LoopRecord " + loopActionRecord + " = MonitoringFactory.getLooRecord(\"" + serviceExecutionId + "\", \"" +
		loopActionId + "\"," + counter + ");\n\n" + 
				" MonitoringFactory.getMonitoringController().newMonitoringRecord(" + loopActionRecord + ");";
		
		
		
		monitoringStatementLoop.setBeforeExecution(beforeExecution);
		monitoringStatementLoop.setInBlock(inBlock);
		monitoringStatementLoop.setAfterExecution(afterExecution);
		
		return monitoringStatementLoop;
	}
	
	
	public static MonitoringStatementBranch getBranchActionInstrumentationCode(String serviceExecutionId, String branchId, String executedBranchId) {
		MonitoringStatementBranch monitoringStatementBranch =  new MonitoringStatementBranch();
		String isBranchExecuted = "__isBranchExecuted_" + CodeInstrumentationUtil.getUUID();
		String branchActionRecord = "__branchActionRecord_" + CodeInstrumentationUtil.getUUID();
		
		String beforeExecution = "\n long " + isBranchExecuted + " = 0;";
		String inBlock = "\n " + isBranchExecuted + " = 1;";
		String afterExecution = "\n final BranchRecord " + branchActionRecord + " = MonitoringFactory.getBranchRecord(\"" + serviceExecutionId + "\",\"" +
		branchId + "\",\"" + executedBranchId +"\"); \n\n" + 
				" MonitoringFactory.getMonitoringController().newMonitoringRecord(" + branchActionRecord + ");";
		
		
		monitoringStatementBranch.setBeforeExecution(beforeExecution);
		monitoringStatementBranch.setInBlock(inBlock);
		monitoringStatementBranch.setAfterExecution(afterExecution);
		
		return monitoringStatementBranch;
	}
	
	
	private static String serviceParamsNamesToString(String[] serviceParametersNames, String variableName) {
    	String serviceParams = "{";
		for(int i = 0; i< serviceParametersNames.length; i++) {
			if(i == serviceParametersNames.length) {
				serviceParams += serviceParametersNames[i];
			}
			else {
				serviceParams += serviceParametersNames[i] + ",";
			}
		}	
		serviceParams += "}";
		
		String primitiveValueExtracter = "String[] " + variableName + " ="
				+ " PrimitiveAttributeExtracter.getPrimitiveAttributeVlues(new Object[]" + serviceParams +")";
		
		return primitiveValueExtracter;
    }
		
}
