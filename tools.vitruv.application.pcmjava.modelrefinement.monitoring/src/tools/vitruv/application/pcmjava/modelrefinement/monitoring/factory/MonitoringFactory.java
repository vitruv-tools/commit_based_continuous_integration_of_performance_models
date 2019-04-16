package tools.vitruv.application.pcmjava.modelrefinement.monitoring.factory;


import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.BranchRecord;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.InternalActionRecord;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.LoopRecord;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.ServiceRecord;


public class MonitoringFactory {

	public static IMonitoringController getMonitoringController() {
		final IMonitoringController MONITORING_CONTROLLER = MonitoringController.getInstance();
	    return 	MONITORING_CONTROLLER;
	}
	
	
	public static BranchRecord getBranchRecord(String serviceExecutionId, String branchId,
			String executedBranchId) {
		return new BranchRecord(
				getSessionID(),
				serviceExecutionId,
				branchId,
				executedBranchId);
	}
	
	
	public static LoopRecord getLooRecord(String serviceExecutionId, String loopId, long loopIterationCount) {
		return new LoopRecord(
				getSessionID(), 
				serviceExecutionId,
				loopId,
				loopIterationCount);
	}
	
	
	public static InternalActionRecord getInternalActionRecord(String serviceExecutionId,String internalActionId,
			long startTime, long stopTime) {
		return new InternalActionRecord(
				getSessionID(),
				serviceExecutionId,
				internalActionId,
				InternalActionRecord.RESOURCE_ID, 
				startTime,
				stopTime);
	}
	
	
	public static ServiceRecord getServiceRecord(String serviceExecutionId, String serviceId, String parameters,
			String callerServiceExecutionId, String callerId, long startTime, long stopTime) {
		return new ServiceRecord(
				getSessionID(), 
				serviceExecutionId, 
				serviceId, 
				parameters, 
				callerServiceExecutionId,
				callerId,
				startTime,
				stopTime);
	}
	
	
	private static long getThreadID() {
		long threadId = Thread.currentThread().getId();
		return threadId;
	}
	
	
	private static String getSessionID() {
		return ServiceRecord.SERVICE_ID;
	}
}
