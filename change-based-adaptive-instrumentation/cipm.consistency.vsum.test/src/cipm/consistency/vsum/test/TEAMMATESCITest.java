package cipm.consistency.vsum.test;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.JavaParserAndPropagatorUtils;
import cipm.consistency.commitintegration.detection.TEAMMATESComponentDetectionStrategy;
import cipm.consistency.cpr.javapcm.teammates.TeammatesJavaPCMChangePropagationSpecification;
import tools.vitruv.framework.propagation.ChangePropagationSpecification;

public class TEAMMATESCITest extends AbstractCITest {
	private static final String COMMIT_TAG_V_8_10_0 = "fdfdcf1ebcabab50f11b01effa036d4c33ad6783";

	@BeforeAll
	public static void setUpForAll() {
		JavaParserAndPropagatorUtils.setConfiguration(new JavaParserAndPropagatorUtils.Configuration(false,
				new TEAMMATESComponentDetectionStrategy()));
	}
	
	@Override
	protected String getTestPath() {
		return "target" + File.separator + "TeammatesTest";
	}

	@Override
	protected String getRepositoryPath() {
		return "https://github.com/TEAMMATES/teammates";
	}

	@Override
	protected String getSettingsPath() {
		return "teammates-exec-files" + File.separator + "settings.properties";
	}
	
	@Override
	protected ChangePropagationSpecification getJavaPCMSpecification() {
		return new TeammatesJavaPCMChangePropagationSpecification();
	}

//	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeammatesIntegration() throws Exception {
		// Integrates TEAMMATES version 8.10.0.
		executePropagationAndEvaluation(null, COMMIT_TAG_V_8_10_0, 0);
//		performIndependentEvaluation();
	}
}
