package tools.vitruv.applications.pcmjava.integrationFromGit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import tools.vitruv.domains.java.ui.monitorededitor.changeclassification.ResourceChange;
import tools.vitruv.framework.vsum.VirtualModel;

/**
 * This class propagates changes from Git commits to JaMoPP models within Vitruv by directly propagating the changes of a commit
 * within a VSUM using the state-based change propagation.
 * 
 * @author Martin Armbruster
 */
public class CommitChangePropagator {
	private static final Logger logger = Logger.getLogger(CommitChangePropagator.class.getSimpleName());
	private GitRepositoryWrapper repoWrapper;
	private VirtualModel vsum;
	private File localRemoteRepository;
	private String remoteRepository;
	private boolean isCurrentStateIntegratedIntoVSUM = false;

	/**
	 * Creates a new instance.
	 * 
	 * @param repositoryPath path to a local Git repository which will be observed.
	 * @param localRepositoryPath path to a local directory in which the Git repository will be cloned.
	 * @param vSUM the VSUM which is used to propagate the changes.
	 */
	public CommitChangePropagator(File repositoryPath, String localRepositoryPath, VirtualModel vSUM) {
		localRemoteRepository = repositoryPath;
		repoWrapper = new GitRepositoryWrapper(new File(localRepositoryPath));
		vsum = vSUM;
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param repositoryPath path to a remote repository which will be observed.
	 * @param localRepositoryPath path to a local directory in which the Git repository will be cloned.
	 * @param vSUM the VSUM which is used to propagate the changes.
	 */
	public CommitChangePropagator(String repositoryPath, String localRepositoryPath, VirtualModel vSUM) {
		remoteRepository = repositoryPath;
		repoWrapper = new GitRepositoryWrapper(new File(localRepositoryPath));
		vsum = vSUM;
	}
	
	/**
	 * Indicates if the current state of the Git repository, i. e., the latest commit of the default branch,
	 * is already integrated in the VSUM.
	 * 
	 * @param integrated true if the current state is integrated. false otherwise.
	 */
	public void setCurrentStateIntegratedIntoVSUM(boolean integrated) {
		isCurrentStateIntegratedIntoVSUM = integrated;
	}
	
	/**
	 * Propagates changes of commits of a remote repository to the VSUM.
	 * On the first call, the remote repository is cloned into the local directory. If the repository was
	 * already cloned beforehand, the Git repository is initialized from the local directory.
	 * If the remote repository is cloned and the state is not integrated into the VSUM, all commits from
	 * the start to the latest commit of the remote repository are integrated into the VSUM.
	 * Otherwise, the changes from the remote repository are fetched and propagated to the VSUM.
	 * 
	 * @throws IOException if something from the repositories cannot be read.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 */
	public void propagteChanges() throws IOException, GitAPIException {
		boolean isInitializedFromRemoteRepository = false;
		if (!repoWrapper.isInitialized()) {
			String[] files = repoWrapper.getRootDirectory().list();
			if (files != null && files.length > 0) {
				logger.debug("Initializing the git repository in " + repoWrapper.getRootDirectory().getAbsolutePath());
				repoWrapper.initFromRootDirectory();
			} else if (localRemoteRepository != null) {
				logger.debug("Initializing the git repository from " + localRemoteRepository.getAbsolutePath());
				repoWrapper.initFromLocalRepository(localRemoteRepository);
				isInitializedFromRemoteRepository = true;
			} else {
				logger.debug("Initializing the git repository from " + remoteRepository);
				repoWrapper.initFromRemoteRepository(remoteRepository);
				isInitializedFromRemoteRepository = true;
			}
		}
		RevCommit lastCommit;
		List<RevCommit> nextCommits;
		if (isInitializedFromRemoteRepository && !isCurrentStateIntegratedIntoVSUM) {
			lastCommit = null;
			logger.debug("Obtaining all commits from the start to the latest commit " + repoWrapper.getLatestCommit().getId().getName());
			nextCommits = repoWrapper.getAllCommitsBetweenTwoCommits(null, repoWrapper.getLatestCommit().getId().getName());
			logger.debug("Got " + nextCommits.size() + " commits.");
		} else {
			lastCommit = repoWrapper.getLatestCommit();
			logger.debug("Latest commit is " + lastCommit.getId().getName());
			logger.debug("Fetching remote repository to get new commits.");
			nextCommits = repoWrapper.fetchAndGetNewCommits();
			logger.debug("Got " + nextCommits.size() + " new commits.");
		}
		for (RevCommit next : nextCommits) {
			String commitId = next.getId().getName();
			logger.debug("Checkout of " + commitId);
			repoWrapper.checkout(commitId);
			logger.debug("Obtaining the differences.");
			List<DiffEntry> diffs = repoWrapper.computeDiffsBetweenTwoCommits(lastCommit, next, true, true);
			logger.debug("Sorting the differences.");
			diffs = GitChangeApplier.sortDiffs(diffs);
			logger.debug("Analyzing the differences.");
			List<ResourceChange> resChanges = new ArrayList<>();
			for (DiffEntry diffEntry : diffs) {
				ResourceChange change = null;
				switch (diffEntry.getChangeType()) {
					case ADD:
					case COPY:
						change = new ResourceChange(null, URI.createFileURI(diffEntry.getNewPath()));
						break;
					case DELETE:
						change = new ResourceChange(URI.createFileURI(diffEntry.getOldPath()), null);
						break;
					case RENAME:
					case MODIFY:
						change = new ResourceChange(URI.createFileURI(diffEntry.getOldPath()), URI.createFileURI(diffEntry.getNewPath()));
						break;
					default:
						break;
				}
				resChanges.add(change);
			}
			internalPropagateChanges(resChanges);
			lastCommit = next;
		}
		logger.debug("Finished the change propagation.");
	}
	
	private void internalPropagateChanges(List<ResourceChange> changes) {
		for (ResourceChange resChange : changes) {
			logger.debug("Starting to propagate the changes between " + resChange.getOldResourceURI()
					+ " and " + resChange.getNewResourceURI());
			Resource newResource = null;
			if (resChange.getNewResourceURI() != null) {
				logger.debug("Loading " + resChange.getNewResourceURI());
				newResource = new ResourceSetImpl().getResource(resChange.getNewResourceURI(), true);
				logger.debug("Resolving the references for " + resChange.getNewResourceURI());
				EcoreUtil.resolveAll(newResource);
			}
			logger.debug("Propagating the changes.");
			vsum.propagateChangedState(newResource, resChange.getOldResourceURI());
			logger.debug("Finished the propagation.");
		}
	}
}
