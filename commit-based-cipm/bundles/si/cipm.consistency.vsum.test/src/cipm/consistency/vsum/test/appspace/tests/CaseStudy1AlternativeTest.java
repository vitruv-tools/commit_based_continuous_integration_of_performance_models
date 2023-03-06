package cipm.consistency.vsum.test.appspace.tests;

import java.io.IOException;

import org.apache.log4j.Level;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.vsum.test.appspace.AppSpaceCITestController;
import cipm.consistency.vsum.test.appspace.LoggingSetup;

/**
 * A test class for the AppSpace Case Study
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class CaseStudy1AlternativeTest extends AppSpaceCITestController {
    private static final String CASESTUDY_SUBMODULE = "commit-based-cipm/bundles/si/cipm.consistency.vsum.test/ciTestRepos/caseStudy1";
    private static final String COMMIT_TAG_1 = "fce1b9f12c0719451141078cdc7785e866fdb12f";
    private static final String COMMIT_TAG_2 = "95f0868b5f61bb1110862da75f21cfabc0530737";
    private static final String COMMIT_TAG_3 = "a9455a2ccca6e3b1c33700f8828cbb708f4c1a95";
    private static final String COMMIT_TAG_4 = "d2aa8a9f35251bd275a80a6a781eb50de374d04b";
    private static final String COMMIT_TAG_5 = "202bef3f7824a4ca6f3e2ff8d3c8a5d32ff9ad21";
    private static final String COMMIT_TAG_6 = "5be3edca1d98e5b628e4ebfc5e25dcd43d9c4f65";
    private static final String COMMIT_TAG_7 = "327144968094a6659d5973602b1afe397016349f";

    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return super.getGitRepositoryWrapper().withLocalSubmodule(CIPM_GIT_DIR, CASESTUDY_SUBMODULE)
            .initialize();
    }

    @Test
    public void testIntegratingVersions1To7() {
        propagateAndEvaluate(null, COMMIT_TAG_1, COMMIT_TAG_2, COMMIT_TAG_3, COMMIT_TAG_4, COMMIT_TAG_5,
                COMMIT_TAG_6, COMMIT_TAG_7);
    }

    @Disabled
    @Test
    public void testIntegratingVersionsProbabilities() {
        LoggingSetup.setMinLogLevel(Level.ERROR);
        propagateAndEvaluate(null, COMMIT_TAG_4);
        LoggingSetup.resetLogLevels();

        var propagation = propagateAndEvaluate(COMMIT_TAG_5).get(0);
        if (propagation.getConsequentialChangeCount() == 0) {
            Assert.fail("No pcm changes after moving one statement");
        }
    }
}
