package tools.vitruv.applications.pcmjava.modelrefinement.tests.instrumentation;

import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

public class AdaptiveMonitoringTest extends SourceCodeInstrumentationTestBase{

	@Test
	public void updateWebGuiMethod() throws JavaModelException, Throwable {
		// change 1
		final String code = "final int i = 5;";
        final ResourceDemandingSEFF seff = this.editWebGUIDownloadMethod(code);
        
        System.out.println("\n\n----------------------- waiting to for the next change.........");
        Thread.sleep(110000);
        System.out.println("\n\n+++++++++++++++++ the next change is started!!!!!!!!!!!!!");
        // change 2
        final String code2 = "\nif(i<10){\n" + this.getExternalCallCode() + "\n}\n";
        final ResourceDemandingSEFF seff2 = this.editWebGUIDownloadMethod(code2);
	}
}
