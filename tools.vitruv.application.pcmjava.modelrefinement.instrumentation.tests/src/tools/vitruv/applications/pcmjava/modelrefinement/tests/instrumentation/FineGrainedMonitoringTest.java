package tools.vitruv.applications.pcmjava.modelrefinement.tests.instrumentation;

import org.junit.Test;

public class FineGrainedMonitoringTest extends FineGrainedMonitoringTestBase{

	@Test
	public void completeInstrumentation() throws Throwable {
		
		// add the internal method
		this.addInternalMethodToGUI(FineGrainedMonitoringSourceCode.interanlMethodName,
				FineGrainedMonitoringSourceCode.interanlMethodHeader,
				FineGrainedMonitoringSourceCode.internalMethodContent);
		
		//
		this.editSequentialSearchMethod(FineGrainedMonitoringSourceCode.sequentialSearchCode);
		this.editBinarySearchMethod(FineGrainedMonitoringSourceCode.binarySearchCode);
		
		//
		this.editGuiBinarySearchMethod(FineGrainedMonitoringSourceCode.guiBinarySearchCode);
		this.editGuiSequentialSearchMethod(FineGrainedMonitoringSourceCode.guiSquentialSearchCode);
		
		
		// instrumentation
		this.instrumentSourceCode("complete");
	}
	
	
	//@Test
	public void iterativeInstrumentation() throws Throwable {
		// add the internal method
		this.addInternalMethodToGUI(FineGrainedMonitoringSourceCode.interanlMethodName,
				FineGrainedMonitoringSourceCode.interanlMethodHeader,
				FineGrainedMonitoringSourceCode.internalMethodContent);
		
		/*   Iteration-0 */
		this.editSequentialSearchMethod(FineGrainedMonitoringSourceCode.sequentialSearchCode);
		this.editGuiSequentialSearchMethod(FineGrainedMonitoringSourceCode.guiSquentialSearchCode);
		
		// instrumentation Iteration-0
		this.instrumentSourceCode("iteration-0");
		
		// delete all probes from instrumentation model
		this.deleteProbesFromInstrumentationModel();
		
		
		/*  Iteration-1 */
		this.editBinarySearchMethod(FineGrainedMonitoringSourceCode.binarySearchCode);
		this.editGuiBinarySearchMethod(FineGrainedMonitoringSourceCode.guiBinarySearchCode);
		
		// instrumentation Iteration-1
		this.instrumentSourceCode("iteration-1");
	}
	
	
}
