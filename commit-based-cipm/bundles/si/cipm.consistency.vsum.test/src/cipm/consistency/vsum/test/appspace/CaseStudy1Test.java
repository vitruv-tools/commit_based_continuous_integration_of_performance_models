package cipm.consistency.vsum.test.appspace;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;

/**
 * A test class for the AppSpace Case Study
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public class CaseStudy1Test extends AppSpaceCITest {
    private static final String CASESTUDY_SUBMODULE = "commit-based-cipm/bundles/si/cipm.consistency.vsum.test/ciTestRepos/caseStudy1";
    private static final String COMMIT_TAG_0_1_0 = "fce1b9f12c0719451141078cdc7785e866fdb12f";
    private static final String COMMIT_TAG_1_0_0 = "c771106f9e81ec996c982afb8689c43240471fc4";
    private static final String COMMIT_TAG_1_0_1 = "37c7c418103595d5d33519c6332e43a45157a500";
    private static final String COMMIT_TAG_1_0_2 = "1da687c2cca1694ee49a6704b3b94fe20fb5eb50";
    private static final String COMMIT_TAG_MOVE = "a8a9bb7a5d1f0d3d276b70a0a07887841ca8205d";
    
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return super.getGitRepositoryWrapper().withLocalSubmodule(CIPM_GIT_DIR, CASESTUDY_SUBMODULE)
            .initialize();
    }

    @Disabled
    @Test
    public void testIntegratingVersion010() {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0);
    }

    @Disabled
    @Test
    public void testIntegratingVersion100() {
        // Integrates casestudy version 1.0.
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0);
//        Assert.assertTrue(executePropagationAndEvaluation(null, COMMIT_TAG_1_0_0, 0));
//		performIndependentEvaluation();
    }

    @Disabled
    @Test
    public void testIntegratingVersion101() {
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_1);
    }

    @Disabled
    @Test
    public void testIntegratingVersion102() {
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_2);
    }

    @Test
    public void testIntegratingVersion100twice() {
        var propagatedChanges = assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0, COMMIT_TAG_1_0_0);
        var lastPropagatedChanges = propagatedChanges.get(propagatedChanges.size() - 1);
        Assert.assertTrue(lastPropagatedChanges.isEmpty());
    }

    @Test
    public void testIntegratingVersions010and100() {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0, COMMIT_TAG_1_0_0);
    }

    @Test
    public void testIntegratingVersions100and101() {
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0, COMMIT_TAG_1_0_1);
    }

    @Test
    public void testIntegratingVersions100and101and102() {
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0, COMMIT_TAG_1_0_1, COMMIT_TAG_1_0_2);
    }

    @Test
    public void testIntegratingVersionsMove() {
        // the MOVE commit only switches two statements. This is for debugging move commands
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_2, COMMIT_TAG_MOVE);
    }

    @Disabled
    @Test
    public void testIntegratingVersionsComplete() {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0, COMMIT_TAG_1_0_0, COMMIT_TAG_1_0_1, COMMIT_TAG_1_0_2);
    }

    @Disabled
    @Test
    public void testIntegratingVersions010and100fromDisk()
            throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0);
        reload(); // dispose an reload from disk
        assertSuccessfulPropagation(COMMIT_TAG_1_0_0);
    }
}
