package tools.vitruv.applications.pcmjava.commitintegration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import tools.vitruv.framework.vsum.VirtualModel;

/**
 * This class propagates changes from Git commits to JaMoPP models within Vitruv by directly propagating the changes of a commit
 * within a VSUM using the state-based change propagation.
 * 
 * @author Martin Armbruster
 * @author Ilia Chupakhin
 */
public class CommitChangePropagator {
	private static final Logger logger = Logger.getLogger(CommitChangePropagator.class.getSimpleName());
	private GitRepositoryWrapper repoWrapper;
	private VirtualModel vsum;
	private File localRemoteRepository;
	private String remoteRepository;
	private boolean isCurrentStateIntegratedIntoVSUM = false;
	private File workSpaceCopy;

	/**
	 * Creates a new instance.
	 * 
	 * @param repositoryPath path to a local Git repository which will be observed.
	 * @param javaCacheDir path to a local directory in which Java files are cached.
	 *                     This includes the cloning of the Git repository.
	 * @param vSUM the VSUM which is used to propagate the changes.
	 */
	public CommitChangePropagator(File repositoryPath, String javaCacheDir, VirtualModel vSUM) {
		localRemoteRepository = repositoryPath;
		vsum = vSUM;
		prepareJavaCacheDir(javaCacheDir);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param repositoryPath path to a remote repository which will be observed.
	 * @param javaCacheDir path to a local directory in which Java files are cached.
	 *                     This includes the cloning of the Git repository.
	 * @param vSUM the VSUM which is used to propagate the changes.
	 */
	public CommitChangePropagator(String repositoryPath, String javaCacheDir, VirtualModel vSUM) {
		remoteRepository = repositoryPath;
		vsum = vSUM;
		prepareJavaCacheDir(javaCacheDir);
	}
	
	private void prepareJavaCacheDir(String dir) {
		File localCacheDir = new File(dir);
		File rootDir = new File(localCacheDir, "repo-cache");
		repoWrapper = new GitRepositoryWrapper(rootDir);
		workSpaceCopy = new File(localCacheDir, "vsum-java-cache");
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
	 * The changes are fetched from the remote repository and propagated to the VSUM.
	 * 
	 * @throws IOException if something from the repositories cannot be read.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 */
	public void propagteChanges() throws IOException, GitAPIException {
		RevCommit lastCommit = repoWrapper.getLatestCommit();
		logger.debug("Latest commit is " + lastCommit.getId().getName());
		logger.debug("Fetching remote repository to get new commits.");
		List<RevCommit> nextCommits = repoWrapper.fetchAndGetNewCommits();
		logger.debug("Got " + nextCommits.size() + " new commits.");
		nextCommits.add(0, lastCommit);
		propagateChanges(nextCommits);
		logger.debug("Finished the change propagation.");
	}
	
	/**
	 * Propagates changes from a given list of commits to the VSUM.
	 * 
	 * @param commits the list of commits with changes to propagate.
	 * @throws GitAPIException if something from the repositories cannot be read.
	 * @throws IOException if there is an exception within the Git usage.
	 */
	public void propagateChanges(List<RevCommit> commits) throws GitAPIException, IOException {
		if (commits.size() > 0) {
			RevCommit first = commits.remove(0);
			logger.debug("Propagating " + commits.size() + " commits.");
			for (RevCommit next : commits) {
				propagateChanges(first, next);
				first = next;
			}
			logger.debug("Finished propagating the commits.");
		}
	}
	
	/**
	 * Propagates changes between two commits to the VSUM.
	 * 
	 * @param start the first commit.
	 * @param end the second commit.
	 * @throws GitAPIException if something from the repositories cannot be read.
	 * @throws IOException if there is an exception within the Git usage.
	 */
	public void propagateChanges(RevCommit start, RevCommit end) throws GitAPIException, IOException {
		String commitId = end.getId().getName();
		logger.debug("Checkout of " + commitId);
		repoWrapper.checkout(commitId);
		logger.debug("Obtaining the differences.");
		List<DiffEntry> diffs = repoWrapper.computeDiffsBetweenTwoCommits(start, end, true, true);
		logger.debug("Sorting the differences.");
		diffs = sortDiffs(diffs);
		logger.debug("Analyzing the differences.");
		for (DiffEntry diffEntry : diffs) {
			processDiff(diffEntry);
		}
		logger.debug("Finished the propagation of " + commitId);
	}
	
	/**
	 * Initializes the propagator.
	 * The remote repository is cloned into the local directory. If the repository was
	 * already cloned beforehand, the Git repository is initialized from the local directory.
	 * If the remote repository is cloned and the state is not integrated into the VSUM, all commits from
	 * the start to the latest commit of the remote repository are integrated into the VSUM.
	 * 
	 * @throws IOException if something from the repositories cannot be read.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 */
	public void initialize() throws IOException, GitAPIException {
		if (!repoWrapper.isInitialized()) {
			boolean isInitializedFromRemoteRepository = false;
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
			if (isInitializedFromRemoteRepository && !isCurrentStateIntegratedIntoVSUM) {
				logger.debug("Obtaining all commits from the start to the latest commit " + repoWrapper.getLatestCommit().getId().getName());
				List<RevCommit> commits = repoWrapper.getAllCommitsBetweenTwoCommits(null, repoWrapper.getLatestCommit().getId().getName());
				logger.debug("Got " + commits.size() + " commits.");
				commits.add(0, null);
				propagateChanges(commits);
				logger.debug("Finished integration.");
			}
			logger.debug("Finished initialization.");
		}
	}
	
	/**
	 * Shuts the propagator down by freeing up resources.
	 */
	public void shutdown() {
		logger.debug("Shutting down.");
		repoWrapper.closeRepository();
	}
	
	/**
	 * Processes a diff.
	 * 
	 * @param entry the diff.
	 */
	protected void processDiff(DiffEntry entry) {
		String prefix = repoWrapper.getRootDirectory().getAbsolutePath() + File.separator;
		String prefixInCopy = workSpaceCopy.getAbsolutePath() + File.separator;
		String newFileInCopy = prefixInCopy + entry.getNewPath();
		// Perform file operations to transfer the changes in the local repository to the workspace copy.
		switch (entry.getChangeType()) {
			case ADD:
			case COPY:
			case RENAME:
			case MODIFY:
				try {
					FileUtils.copyFile(new File(prefix + entry.getNewPath()),
							new File(newFileInCopy));
				} catch (IOException e) {
					logger.error(e);
				}
				break;
			default:
				break;
		}
		switch (entry.getChangeType()) {
			case DELETE:
			case RENAME:
				new File(prefixInCopy + entry.getOldPath()).delete();
				break;
			default:
				break;
		}
		// Propagate the changes.
		switch (entry.getChangeType()) {
			case ADD:
			case COPY:
				internalPropagateChanges(null, URI.createFileURI(newFileInCopy));
				break;
			case DELETE:
				internalPropagateChanges(URI.createFileURI(prefixInCopy + entry.getOldPath()), null);
				break;
			case RENAME:
			case MODIFY:
				internalPropagateChanges(URI.createFileURI(prefixInCopy + entry.getOldPath()), URI.createFileURI(newFileInCopy));
				break;
			default:
				break;
		}
	}
	
	private void internalPropagateChanges(URI oldURI, URI newURI) {
		logger.debug("Starting to propagate the changes between " + oldURI + " and " + newURI);
		Resource newResource = null;
		if (newURI != null) {
			logger.debug("Loading " + newURI);
			newResource = new ResourceSetImpl().getResource(newURI, true);
			logger.debug("Resolving the references for " + newURI);
			EcoreUtil.resolveAll(newResource);
		}
		logger.debug("Propagating the changes.");
		vsum.propagateChangedState(newResource, oldURI);
		logger.debug("Finished the propagation.");
	}
	
	/**
	 * Sorts changes in the form of <code>DiffEntry</code> in a particular order:
	 * [COPY,...,RENAME,...,DELETE,...,ADD,...,MODIFY,...]
	 * This is necessary to avoid certain problems.
	 * For example, adding a reference in an existing class to a new class. 
	 * In this case, the new class must be created before the reference is added.
	 * Thus, ADD new class has to be done before MODIFY in the existing class.
	 * 
	 * @param diffs unsorted changes.
	 * @return the sorted changes.
	 */
	private List<DiffEntry> sortDiffs(List<DiffEntry> diffs) {
		
		// Temporary lists for all diff types.
		ArrayList<DiffEntry> copies = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> renames = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> deletes = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> adds = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> modifies = new ArrayList<DiffEntry>();
		
		for (DiffEntry diff : diffs) {
			switch (diff.getChangeType()) {
			case COPY:
				copies.add(diff);
				break;
			case RENAME:
				renames.add(diff);
				break;
			case DELETE:
				deletes.add(diff);
				break;
			case ADD:
				adds.add(diff);
				break;
			case MODIFY:
				modifies.add(diff);
				break;
			}
		}
		
		ArrayList<DiffEntry> result = new ArrayList<DiffEntry>();
		
		result.addAll(copies);
		result.addAll(renames);
		result.addAll(deletes);
		result.addAll(adds);
		result.addAll(modifies);
		
		return result;
	}
	
	protected GitRepositoryWrapper getWrapper() {
		return repoWrapper;
	}
}
