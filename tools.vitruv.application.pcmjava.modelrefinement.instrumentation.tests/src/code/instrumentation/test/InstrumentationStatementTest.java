package code.instrumentation.test;

import org.junit.Test;

import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator.InstrumentationStatements;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator.MonitoringStatementBranch;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator.MonitoringStatementInternalAction;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator.MonitoringStatementLoop;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.instrumentationcodegenerator.MonitoringStatementOperation;

public class InstrumentationStatementTest {

	@Test
	public void instrumentationStatementTest() {
			
		MonitoringStatementInternalAction internalActionProbe = InstrumentationStatements.getInternalActionInstrumentationCode("23adasf", null);
		MonitoringStatementOperation operationProbe = InstrumentationStatements.getOperationInstrumentationCode("23wedasf", null);
		MonitoringStatementLoop loopActionProbe = InstrumentationStatements.getLoopActionInstrumentationCode("23wedasf", null);
		MonitoringStatementBranch branchActionProbe = InstrumentationStatements.getBranchActionInstrumentationCode("23wedasf", null);
		
		String instrumentedMethod = "public void execute(int[] counter, boolean bool) {\n";
		instrumentedMethod += operationProbe.getBeforeExecution() + "\n\n";
		
		// internal action
		instrumentedMethod += internalActionProbe.getBeforeExecution() + "\n";
		instrumentedMethod += "// execution internal action\n";
		instrumentedMethod += internalActionProbe.getAfterExecution() + "\n\n";
		
		// loop action
		instrumentedMethod += loopActionProbe.getBeforeExecution() + "\n";
		instrumentedMethod +="for(int i:counter){\n"+
				loopActionProbe.getInBlock() + "\n"
				+ " // execute external action \n }\n";
		instrumentedMethod += loopActionProbe.getAfterExecution() + "\n\n";
		
		//branch action
		instrumentedMethod += branchActionProbe.getBeforeExecution() + "\n";
		instrumentedMethod += "if(bool){\n" + branchActionProbe.getInBlock() + "\n // execute external action \n } \n";
		instrumentedMethod += branchActionProbe.getAfterExecution() + "\n\n";
		
		
		
		
		
		instrumentedMethod += operationProbe.getAfterExecution();	
		instrumentedMethod += "\n}\n";
		
		System.out.println("\n\n" + instrumentedMethod + "\n\n");


		System.out.println("DONE");
	}
}
