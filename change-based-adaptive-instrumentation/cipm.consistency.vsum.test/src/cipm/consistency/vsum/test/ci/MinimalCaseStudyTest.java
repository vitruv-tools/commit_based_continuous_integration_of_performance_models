package cipm.consistency.vsum.test.ci;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import java.io.IOException;
import java.nio.file.Paths;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A test class for the TeaStore.
 * 
 * @author Martin Armbruster
 */
public class MinimalCaseStudyTest extends AppSpaceCITest {
    private static final String COMMIT_1 = "842ec92f3406da965a5f9e7f468eb80eeb287b04";
    private static final String COMMIT_2 = "f63d05a18a5dd36f29e7312d797f2806d935e3a3";

    @BeforeEach
    public void initialize() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        state = new CommitIntegrationState<>();
        var overwrite = true;
        state.initialize(this, overwrite);
    }

    @AfterEach
    public void dispose() {
        state.dispose();
    }

    @Override
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        var parentGitDir = Paths.get("../../.git");
        var submoduleName = "change-based-adaptive-instrumentation/cipm.consistency.vsum.test/ciTestRepos/minimalCaseStudy";
        return super.getGitRepositoryWrapper()
            .withLocalSubmodule(parentGitDir, submoduleName)
            .initialize();
    }

    @Test
    public void test_minimal_2() throws Exception {
        assertSuccessfulPropagation(null, COMMIT_2);
//        var result = executePropagationAndEvaluation(null, getLatestCommitId(), 0);
//        Assert.assertTrue(result);
    }

    @Test
    public void test_minimal_1_2() throws Exception {
        assertSuccessfulPropagation(null, COMMIT_1);
        assertSuccessfulPropagation(COMMIT_1, COMMIT_2);
    }

    @Test
    public void test_minimal_2_2() throws Exception {
        assertSuccessfulPropagation(null, COMMIT_2);
        assertSuccessfulPropagation(COMMIT_2, COMMIT_2);
    }
}
