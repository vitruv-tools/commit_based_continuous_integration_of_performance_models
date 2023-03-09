package cipm.consistency.vsum.test.appspace.tests.serveCall;

import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.cpr.luapcm.Config;
import cipm.consistency.vsum.test.appspace.tests.CaseStudy1Test;

/**
 * Test case study 1 with serve call tracking
 * 
 * @author Lukas Burgey
 */
public class CaseStudy1ServeCallTest extends CaseStudy1Test {
    @BeforeEach
    public void serveCallSetup() {
        Config.setIsTrackServeCallsEnabled(true);
    }
}
