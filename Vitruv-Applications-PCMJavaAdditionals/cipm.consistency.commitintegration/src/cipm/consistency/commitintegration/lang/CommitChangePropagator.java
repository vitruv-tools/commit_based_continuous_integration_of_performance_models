package cipm.consistency.commitintegration.lang;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
public abstract class CommitChangePropagator {
	protected Logger LOGGER;
    protected InternalVirtualModel vsum;
    protected GitRepositoryWrapper repoWrapper;
    protected LanguageFileSystemLayout fileLayout;


    public CommitChangePropagator(InternalVirtualModel vsum, GitRepositoryWrapper repoWrapper, LanguageFileSystemLayout fileLayout) {
        LOGGER = Logger.getLogger(this.getClass().getPackageName());
        this.vsum = vsum;
        this.repoWrapper = repoWrapper;
        this.fileLayout = fileLayout;
    }

    public LanguageFileSystemLayout getFileSystemLayout() {
        return fileLayout;
    }

    /**
     * Propagates the changes between an empty repository and the latest commit.
     * 
     * @throws IOException
     *             if something from the repositories cannot be read.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     */
    public void propagteChanges() throws IOException, GitAPIException {
        propagateChanges(repoWrapper.getLatestCommit());
    }

    /**
     * Propagates changes for a given list of commits.
     * 
     * @param ids
     *            ids of the commits.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     * @throws IOException
     *             if the repository cannot be read.
     */
    public void propagateChanges(String... ids) throws GitAPIException, IOException {
        List<RevCommit> commits = new ArrayList<>();
        for (String id : ids) {
            commits.add(repoWrapper.getCommitForId(id));
        }
        propagateChanges(commits);
    }

    /**
     * Fetches changes from the remote repository and propagates them to the VSUM.
     * 
     * @throws IOException
     *             if something from the repositories cannot be read.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     */
    public void fetchAndPropagateChanges() throws IOException, GitAPIException {
        RevCommit lastCommit = repoWrapper.getLatestCommit();
        LOGGER.debug("Latest commit is " + lastCommit.getId()
            .getName());
        LOGGER.debug("Fetching remote repository to get new commits.");
        List<RevCommit> nextCommits = repoWrapper.fetchAndGetNewCommits();
        LOGGER.debug("Got " + nextCommits.size() + " new commits.");
        nextCommits.add(0, lastCommit);
        propagateChanges(nextCommits);
        LOGGER.debug("Finished the change propagation.");
    }

    /**
     * Propagates changes from a given list of commits to the VSUM.
     * 
     * @param commits
     *            the list of commits with changes to propagate.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     * @throws IOException
     *             if something from the repositories cannot be read.
     */
    public void propagateChanges(List<RevCommit> commits) throws GitAPIException, IOException {
        if (commits.size() > 0) {
            RevCommit first = commits.remove(0);
            LOGGER.debug("Propagating " + commits.size() + " commits.");
            for (RevCommit next : commits) {
                boolean result = propagateChanges(first, next);
                if (result) {
                    first = next;
                }
            }
            LOGGER.debug("Finished propagating the commits.");
        }
    }

    /**
     * Propagates changes between an empty repository and a specific commit.
     * 
     * @param commitId
     *            id of the commit.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     * @throws IOException
     *             if the repository cannot be read.
     */
    public void propagateChanges(String commitId) throws GitAPIException, IOException {
        propagateChanges(repoWrapper.getCommitForId(commitId));
    }

    /**
     * Propagates changes between an empty repository and a specific commit.
     * 
     * @param commit
     *            the commit.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     * @throws IOException
     *             if something from the repositories cannot be read.
     */
    public void propagateChanges(RevCommit commit) throws GitAPIException, IOException {
        propagateChanges(null, commit);
    }

    /**
     * Propagates changes between two commits to the VSUM.
     * 
     * @param startId
     *            id of the first commit.
     * @param endId
     *            id of the second commit.
     * @return true if the changes are successfully propagated. false indicates that there are no
     *         changes for Java files or the pre-processing failed.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     * @throws IOException
     *             it the repository cannot be read.
     */
    public boolean propagateChanges(String startId, String endId) throws GitAPIException, IOException {
        return propagateChanges(repoWrapper.getCommitForId(startId), repoWrapper.getCommitForId(endId));
    }

	/**
	 * Propagates changes between two commits to the VSUM.
	 * 
	 * @param start the first commit.
	 * @param end   the second commit.
	 * @return true if the changes are successfully propagated. false indicates that
	 *         there are no changes for Java files or the pre-processing failed.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 * @throws IOException     if something from the repositories cannot be read.
	 */
	public abstract boolean propagateChanges(RevCommit start, RevCommit end) throws GitAPIException, IOException; 
}
