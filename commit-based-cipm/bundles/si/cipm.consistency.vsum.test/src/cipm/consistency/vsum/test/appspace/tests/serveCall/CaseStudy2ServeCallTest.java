package cipm.consistency.vsum.test.appspace.tests.serveCall;

import org.junit.jupiter.api.BeforeEach;

import cipm.consistency.cpr.luapcm.Config;
import cipm.consistency.vsum.test.appspace.tests.CaseStudy2Test;

/**
 * A test class for the AppSpace Case Study
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class CaseStudy2ServeCallTest extends CaseStudy2Test {
    @BeforeEach
    public void serveCallSetup() {
        Config.setIsTrackServeCallsEnabled(true);
    }
}
