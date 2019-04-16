package tools.vitruv.applications.pcmjava.modelrefinement.tests.instrumentation;

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

public class FineGrainedSourceCodeInstrumentationTest extends SourceCodeInstrumentationTestBase{

	@Test
	public void addInternalActionTest() throws Throwable {
		final String code = "final int i = 5;";
        final ResourceDemandingSEFF seff = this.editWebGUIDownloadMethod(code);
	
        //instrumentation
		this.instrumentSourceCode();
		
		// compare code
		final IMethod webGUIMethodFromIstrumentedCode = this.getWebGuiDowloadMethodFromIstumentedProject();
		String expectedInternalActionInstrumentation = AssertSourceCodeHelper.getExpectedInternalActionInstrumentation(seff, code);
		String internalActionInstrumentation = 
				AssertSourceCodeHelper.renameRandomVariableFromInstrumentedCode(webGUIMethodFromIstrumentedCode.getSource());
		assertEquals(expectedInternalActionInstrumentation, internalActionInstrumentation);
	}
	
	@Test
	public void addBranchActionTest() throws JavaModelException, Throwable {
		final String code = "\nif(i<10){\n" + this.getExternalCallCode() + "\n}\n";
   	    final ResourceDemandingSEFF seff = this.editWebGUIDownloadMethod(code);
   	    
   	   
   	    // instrumentation
   	    this.instrumentSourceCode();
   	    
   		// compare code
   		final IMethod webGUIMethodFromIstrumentedCode = this.getWebGuiDowloadMethodFromIstumentedProject();
   		String expectedBranchActionInstrumentation = AssertSourceCodeHelper.getExpectedBranchActionInstrumentation(seff);
   		String branchActionInstrumentation = 
   				AssertSourceCodeHelper.renameRandomVariableFromInstrumentedCode(webGUIMethodFromIstrumentedCode.getSource());   		
   		assertEquals(expectedBranchActionInstrumentation, branchActionInstrumentation);
	}
	
	@Test
	public void addLoopActionTest() throws JavaModelException, Throwable {
		final String code = "\nfor(int i=0;i<10;i++){\n" + this.getExternalCallCode() + "\n}\n";
   	    final ResourceDemandingSEFF seff = this.editWebGUIDownloadMethod(code);
   	    
		// instrumentation
		this.instrumentSourceCode();
		
		// compare code
   		final IMethod webGUIMethodFromIstrumentedCode = this.getWebGuiDowloadMethodFromIstumentedProject();
   		String expectedLoopActionInstrumentation = AssertSourceCodeHelper.getExpectedLoopActionInstrumentation(seff);
   		String loopActionInstrumentation = 
   				AssertSourceCodeHelper.renameRandomVariableFromInstrumentedCode(webGUIMethodFromIstrumentedCode.getSource());
   		assertEquals(expectedLoopActionInstrumentation, loopActionInstrumentation);	
	}

}
