package cipm.consistency.vsum.test.appspace;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Assert;
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

    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return super.getGitRepositoryWrapper().withLocalSubmodule(CIPM_GIT_DIR, CASESTUDY_SUBMODULE)
            .initialize();
    }

    @Test
    public void testIntegratingVersion010() {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0);
    }

    @Test
    public void testIntegratingVersion100() {
        // Integrates casestudy version 1.0.
        assertSuccessfulPropagation(null, COMMIT_TAG_1_0_0);
//        Assert.assertTrue(executePropagationAndEvaluation(null, COMMIT_TAG_1_0_0, 0));
//		performIndependentEvaluation();
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
    public void testIntegratingVersions010and100fromDisk()
            throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        assertSuccessfulPropagation(null, COMMIT_TAG_0_1_0);
        reload(); // dispose an reload from disk
        assertSuccessfulPropagation(COMMIT_TAG_1_0_0);
    }
}
