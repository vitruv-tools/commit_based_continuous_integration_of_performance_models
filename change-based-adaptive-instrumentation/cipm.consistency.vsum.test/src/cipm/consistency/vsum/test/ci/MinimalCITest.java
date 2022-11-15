package cipm.consistency.vsum.test.ci;

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
public class MinimalCITest extends AppSpaceCITest {
    @BeforeEach
    public void initialize() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        super.initialize(this);
    }

    @AfterEach
    public void dispose() {
        state.dispose();
    }

    @Override
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        var workTreePath = Paths.get("ciTestRepos/minimalLuaApp");
        return super.getGitRepositoryWrapper().withLocalDirectory(workTreePath)
            .initialize();
    }

    @Test
    public void testCaseMinimalLuaApp() throws Exception {
        assertSuccessfulPropagation(null, getLatestCommitId());
//        var result = executePropagationAndEvaluation(null, getLatestCommitId(), 0);
//        Assert.assertTrue(result);
    }

}
