package cipm.consistency.vsum.test.appspace;

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

/**
 * A test class for the TeaStore.
 * 
 * @author Martin Armbruster
 */
public class MinimalCaseStudyTest extends AppSpaceCITest {
    private static final String COMMIT_1 = "842ec92f3406da965a5f9e7f468eb80eeb287b04";
    private static final String COMMIT_2 = "f63d05a18a5dd36f29e7312d797f2806d935e3a3";
    private static final String COMMIT_PROBLEM = "4cbb4e16fb348920fbf28ca192e78ec5ce5f07eb";

    @BeforeEach
    public void initialize() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        super.initialize(this);
    }

    @AfterEach
    public void dispose() {
//        state.createCopyWithTimeStamp("after_testrun");
        state.getDirLayout().delete();
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
        var propagatedChanges = assertSuccessfulPropagation(null, COMMIT_2);
        Assert.assertTrue("One change exists", propagatedChanges.size() == 1);
//        var result = executePropagationAndEvaluation(null, getLatestCommitId(), 0);
//        Assert.assertTrue(result);
    }

    @Test
    public void test_minimal_1_2() throws Exception {
        // two subsequent propagations
        var propagatedChanges = assertSuccessfulPropagation(null, COMMIT_1, COMMIT_2);
        Assert.assertTrue("Two changes exist", propagatedChanges.size() == 2);
    }

    @Test
    public void test_minimal_2_2() throws Exception {
        state.setTag("minimal_2_2");
        // propagating the same version twice
        var propagatedChanges = assertSuccessfulPropagation(null, COMMIT_2, COMMIT_2);
        var lastPropagationResult = propagatedChanges.get(propagatedChanges.size() - 1);
        Assert.assertTrue("No changes when propagating previously propagated commit", lastPropagationResult.isEmpty());
    }

    @Test
    public void test_minimal_caseStudy1_matching() throws Exception {
        // propagating the same version twice
        var propagatedChanges = assertSuccessfulPropagation(null, COMMIT_2, COMMIT_PROBLEM);
        Assert.assertTrue("Two changes exist", propagatedChanges.size() == 2);
    }

    @Test
    public void test_minimal_1_2_3() throws Exception {
        // propagating the same version twice
        var propagatedChanges = assertSuccessfulPropagation(null, COMMIT_1, COMMIT_2, COMMIT_PROBLEM);
        Assert.assertTrue("Three changes exist", propagatedChanges.size() == 3);
    }
}
