package cipm.consistency.vsum.test.ci;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import java.io.IOException;
import java.nio.file.Paths;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * A test class for the TeaStore.
 * 
 * @author Martin Armbruster
 */
public class CaseStudy1CITest extends AppSpaceCITest {
    private static final String COMMIT_TAG_0_1_0 = "fce1b9f12c0719451141078cdc7785e866fdb12f";
    private static final String COMMIT_TAG_1_0_0 = "c771106f9e81ec996c982afb8689c43240471fc4";
    
    @BeforeEach
    public void initialize() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        super.initialize(this);
    }

    @AfterEach
    public void dispose() {
        state.dispose();
    }
    

    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        var parentGitDir = Paths.get("../../.git");
        var submoduleName = "change-based-adaptive-instrumentation/cipm.consistency.vsum.test/ciTestRepos/caseStudy1";
        return super.getGitRepositoryWrapper()
            .withLocalSubmodule(parentGitDir, submoduleName)
            .initialize();
    }

    @Test
    public void testCaseStudy1_0Integration() throws Exception {
        // Integrates casestudy version 1.0.
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0);
//        Assert.assertTrue(executePropagationAndEvaluation(null, COMMIT_TAG_1_0_0, 0));
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_0To1_1Propagation() throws Exception {
        // Propagation of changes between version 0.1.0 and 1.0.0
//        assertSuccessfulPropagation(COMMIT_TAG_1_0_0, COMMIT_TAG_0_1_0);
//        executePropagationAndEvaluation(COMMIT_TAG_0_1_0, COMMIT_TAG_1_0_0, 1);
//		performIndependentEvaluation();
    }

}
