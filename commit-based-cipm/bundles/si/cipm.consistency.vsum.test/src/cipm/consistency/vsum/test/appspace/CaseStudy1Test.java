package cipm.consistency.vsum.test.appspace;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import java.io.IOException;
import java.nio.file.Paths;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * A test class for the AppSpace Case Study 1
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class CaseStudy1Test extends AppSpaceCITest {
    private static final String COMMIT_TAG_0_1_0 = "fce1b9f12c0719451141078cdc7785e866fdb12f";
    private static final String COMMIT_TAG_1_0_0 = "c771106f9e81ec996c982afb8689c43240471fc4";
    
    @BeforeEach
    public void initialize(TestInfo testInfo) throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        this.state = new CommitIntegrationState<>();
        var overwrite = true;
        state.initialize(this, overwrite);
    }

    @AfterEach
    public void dispose() {
//        state.createCopyWithTimeStamp("after_testrun");
        state.dispose();
    }
    

    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        var parentGitDir = Paths.get("../../../../.git");
        var submoduleName = "commit-based-cipm/bundles/si/cipm.consistency.vsum.test/ciTestRepos/caseStudy1";
        return super.getGitRepositoryWrapper()
            .withLocalSubmodule(parentGitDir, submoduleName)
            .initialize();
    }

    @Test
    public void test_010_integration() throws Exception {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0);
    }

    @Test
    public void test_100_integration() throws Exception {
        // Integrates casestudy version 1.0.
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0);
//        Assert.assertTrue(executePropagationAndEvaluation(null, COMMIT_TAG_1_0_0, 0));
//		performIndependentEvaluation();
    }

    @Test
    public void test_100_100_integration() throws Exception {
        var propagatedChanges = assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0, COMMIT_TAG_1_0_0);
        var lastPropagationResult = propagatedChanges.get(propagatedChanges.size() - 1);
        Assert.assertTrue(lastPropagationResult.isEmpty());
    }

    @Test
    public void test_010_100_integration() throws Exception {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0, COMMIT_TAG_1_0_0);
    }
}
