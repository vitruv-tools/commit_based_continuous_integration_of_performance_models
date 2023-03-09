package cipm.consistency.vsum.test.appspace.tests;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.vsum.test.appspace.AppSpaceCITestController;

/**
 * A test class for the AppSpace Case Study
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class CaseStudy1Test extends AppSpaceCITestController {
    private static final String CASESTUDY_SUBMODULE = "commit-based-cipm/bundles/si/cipm.consistency.vsum.test/ciTestRepos/caseStudy1";
    
    // primary history:
    private static final String COMMIT_TAG_0_1_0 = "fce1b9f12c0719451141078cdc7785e866fdb12f";
    private static final String COMMIT_TAG_1_0_0 = "c771106f9e81ec996c982afb8689c43240471fc4";
    private static final String COMMIT_TAG_1_0_1 = "37c7c418103595d5d33519c6332e43a45157a500";
    private static final String COMMIT_TAG_1_0_2 = "1da687c2cca1694ee49a6704b3b94fe20fb5eb50";
    
    // this commit removes the database api again
    private static final String COMMIT_TAG_UNSERVE_DB = "95d4f37";
    private static final String COMMIT_TAG_REMOVE_DB = "225be44";

    private static final String[] COMMITS = { null, COMMIT_TAG_0_1_0, COMMIT_TAG_1_0_0, COMMIT_TAG_1_0_1,
            COMMIT_TAG_1_0_2, COMMIT_TAG_UNSERVE_DB, COMMIT_TAG_REMOVE_DB };
    

    // alternative history:
    private static final String COMMIT_TAG_1 = "fce1b9f12c0719451141078cdc7785e866fdb12f";
    private static final String COMMIT_TAG_2 = "95f0868b5f61bb1110862da75f21cfabc0530737";
    private static final String COMMIT_TAG_3 = "a9455a2ccca6e3b1c33700f8828cbb708f4c1a95";
    private static final String COMMIT_TAG_4 = "d2aa8a9f35251bd275a80a6a781eb50de374d04b";
    private static final String COMMIT_TAG_5 = "202bef3f7824a4ca6f3e2ff8d3c8a5d32ff9ad21";
    private static final String COMMIT_TAG_6 = "5be3edca1d98e5b628e4ebfc5e25dcd43d9c4f65";
    private static final String COMMIT_TAG_7 = "327144968094a6659d5973602b1afe397016349f";
    private static final String[] ALTERNATIVE_HISTORY_COMMITS = { null, COMMIT_TAG_1, COMMIT_TAG_2, COMMIT_TAG_3,
            COMMIT_TAG_4, COMMIT_TAG_5, COMMIT_TAG_6, COMMIT_TAG_7 };

    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return super.getGitRepositoryWrapper().withLocalSubmodule(CIPM_GIT_DIR, CASESTUDY_SUBMODULE)
            .initialize();
    }
//    
//    @BeforeEach
//    public void serveCallSetup() {
//        Config.setIsTrackServeCallsEnabled(false);
//    }

    @Test
    public void testPropagationIndividually(TestInfo testInfo) {
        propagateAndEvaluateIndividually(testInfo, COMMITS);
    }

    @Test
    public void testPropagationAlternativeIndividually(TestInfo testInfo) {
        propagateAndEvaluateIndividually(testInfo, ALTERNATIVE_HISTORY_COMMITS);
    }

    @Test
    public void testPropagationComplete() {
        propagateAndEvaluate(COMMITS);
    }

    @Test
    public void testPropagationAlternativeComplete() {
        propagateAndEvaluate(ALTERNATIVE_HISTORY_COMMITS);
    }

    @Test
    public void testPropagationVersion100twice() {
        var propagatedChanges = propagateAndEvaluate(null, COMMIT_TAG_1_0_0, COMMIT_TAG_1_0_0);
        var lastPropagatedChanges = propagatedChanges.get(propagatedChanges.size() - 1);
        Assert.assertTrue(lastPropagatedChanges.isEmpty());
    }
}
