package tools.vitruv.applications.pcmjava.modelrefinement.tests.instrumentation;

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.IMethod;
import org.junit.Test;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

public class FineGrainedInstrumentationLevel2Test extends SourceCodeInstrumentationTestBase{
	
	@Test
	public void addSourceCodeTest() throws Throwable {
		final String code_0 = "final int i = 5; final int j = 0;";
		final String code_1 = "\nif(i<10){\n" + this.getExternalCallCode() + "\n}\n";
		final String code_2 = "\nfor(int i=0;i<10;i++){\n" + this.getExternalCallCode() + "\n}\n";
		final String code_3 = "\nfor(int k=0;k<10;k++){\n" + this.getExternalCallCode() + "\n}\n"; 
		
		// iteration 0
        final ResourceDemandingSEFF seff_0 = this.editWebGUIDownloadMethod(code_0);
        
        // iteration 1
        final ResourceDemandingSEFF seff_1 = this.editWebGUIDownloadMethod(code_1);
        
        // iteration 2
        final ResourceDemandingSEFF seff_2 = this.editWebGUIDownloadMethod(code_2 + code_3);
        

        
        AbstractAction internalAction_iteration_0 = this.getInternalActionId(seff_0);
        AbstractAction branchAction_iteration_1 = this.getBranchActionId(seff_1);
        
        AbstractAction internalAction_iteration_2 = this.getInternalActionId(seff_2);
        AbstractAction branchAction_iteration_2 = this.getBranchActionId(seff_2);
        
        // compare the ids from iteration 0, iteration 1 and iteration 2
        
        assertEquals(internalAction_iteration_0.getId(), internalAction_iteration_2.getId());
        assertEquals(branchAction_iteration_1.getId(), branchAction_iteration_2.getId());

        
        // iteration 3: modify the first internal action
        final ResourceDemandingSEFF seff_3 = modifyWebGuiDownloadMethod("final int m = 0;", 0);
        
        AbstractAction internalAction_iteration_3 = this.getInternalActionId(seff_3);
        assertEquals(internalAction_iteration_3.getId(), internalAction_iteration_0.getId());
        
        //assertEquals(seff_2.getSteps_Behaviour().size(), 6);
        
	}
	
	private AbstractAction getInternalActionId(ResourceDemandingSEFF seff) {
		for(AbstractAction aa: seff.getSteps_Behaviour()) {
			if(aa instanceof InternalAction) {
				return aa;
			}
		}
		return null;
	}
	
	private AbstractAction getBranchActionId(ResourceDemandingSEFF seff) {
		for(AbstractAction aa: seff.getSteps_Behaviour()) {
			if(aa instanceof BranchAction) {
				return aa;
			}
		}
		return null;
	}
	
	private AbstractAction getLoopActionId(ResourceDemandingSEFF seff) {
		for(AbstractAction aa: seff.getSteps_Behaviour()) {
			if(aa instanceof LoopAction) {
				return aa;
			}
		}
		return null;
	}
	
}
