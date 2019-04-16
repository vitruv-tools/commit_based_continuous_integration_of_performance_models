package tools.vitruv.application.pcmjava.modelrefinement.monitoring.test;

import tools.vitruv.application.pcmjava.modelrefinement.monitoring.factory.MonitoringFactory;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.BranchRecord;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.InternalActionRecord;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.LoopRecord;
import tools.vitruv.application.pcmjava.modelrefinement.monitoring.record.ServiceRecord;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i = 0; i<20; i++)
         summe();
	}
	
	
	
	public static void summe() {
		
		// internal action
		long tin = MonitoringFactory.getMonitoringController().getTimeSource().getTime();
		int i = 0;
		int j = 3;
		long tout = MonitoringFactory.getMonitoringController().getTimeSource().getTime();
		final InternalActionRecord internalActionRecord = MonitoringFactory.getInternalActionRecord("__dfzuezr", "__sfdghghfgd", tin, tout);
		MonitoringFactory.getMonitoringController().newMonitoringRecord(internalActionRecord);
		
		//-------------------------------------
		
		long __loopCount = 0;
		// loop action
		for(int k = 0; k<10; k++) {
			__loopCount ++;
			Calculator.summe(k, j);
		}
		final LoopRecord loopRecord = MonitoringFactory.getLooRecord("__fhjhfd", "__asfj", __loopCount);
		MonitoringFactory.getMonitoringController().newMonitoringRecord(loopRecord);
		
		//--------------------------------------------
		
		
		// branch action
		boolean __isBranchExecuted = false;
		if(i == 0) {
			__isBranchExecuted = true;
			Calculator.summe(i, j);
		}
		
		if(__isBranchExecuted) {
			final BranchRecord branchRecord = MonitoringFactory.getBranchRecord("__sshsaf", "__sfkjasfd", "__sfkjasfd");
			MonitoringFactory.getMonitoringController().newMonitoringRecord(branchRecord);
		}
		
	
		//--------------------------------------------
				
	}

}
