package cipm.consistency.commitintegration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * This class propagates changes from Git commits to JaMoPP models within Vitruv by directly propagating the changes of a commit
 * within a VSUM using the state-based change propagation.
 * 
 * @author Martin Armbruster
 * @author Ilia Chupakhin
 */
public class CommitChangePropagator {
	private static final Logger logger = Logger.getLogger("cipm." + CommitChangePropagator.class.getSimpleName());
	private GitRepositoryWrapper repoWrapper;
	private InternalVirtualModel vsum;
	private String remoteRepository;
	private JavaFileSystemLayout fileLayout;

	/**
	 * Creates a new instance.
	 * 
	 * @param repositoryPath path to a local Git repository which will be observed.
	 * @param javaCacheDir path to a local directory in which Java files are cached.
	 *                     This includes the cloning of the Git repository.
	 * @param vSUM the VSUM which is used to propagate the changes.
	 */
	public CommitChangePropagator(File repositoryPath, String javaCacheDir, InternalVirtualModel vSUM) {
		this(repositoryPath.getAbsolutePath(), javaCacheDir, vSUM);
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param repositoryPath path to a remote repository which will be observed.
	 * @param javaCacheDir path to a local directory in which Java files are cached.
	 *                     This includes the cloning of the Git repository.
	 * @param vSUM the VSUM which is used to propagate the changes.
	 */
	public CommitChangePropagator(String repositoryPath, String javaCacheDir, InternalVirtualModel vSUM) {
		remoteRepository = repositoryPath;
		vsum = vSUM;
		fileLayout = new JavaFileSystemLayout(Paths.get(javaCacheDir).toAbsolutePath());
		repoWrapper = new GitRepositoryWrapper(fileLayout.getLocalJavaRepo().toFile());
	}
	
	/**
	 * Propagates the changes between an empty repository and the latest commit.
	 * 
	 * @throws IOException if something from the repositories cannot be read.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 */
	public void propagteChanges() throws IOException, GitAPIException {
		propagateChanges(repoWrapper.getLatestCommit());
	}
	
	/**
	 * Fetches changes from the remote repository and propagates them to the VSUM.
	 * 
	 * @throws IOException if something from the repositories cannot be read.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 */
	public void fetchAndPropagateChanges() throws IOException, GitAPIException {
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
	 * Propagates changes for a given list of commits.
	 * 
	 * @param ids ids of the commits.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 * @throws IOException if the repository cannot be read.
	 */
	public void propagateChanges(String...ids) throws GitAPIException, IOException {
		List<RevCommit> commits = new ArrayList<>();
		for (String id : ids) {
			commits.add(repoWrapper.getCommitForId(id));
		}
		propagateChanges(commits);
	}
	
	/**
	 * Propagates changes from a given list of commits to the VSUM.
	 * 
	 * @param commits the list of commits with changes to propagate.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 * @throws IOException if something from the repositories cannot be read.
	 */
	public void propagateChanges(List<RevCommit> commits) throws GitAPIException, IOException {
		if (commits.size() > 0) {
			RevCommit first = commits.remove(0);
			logger.debug("Propagating " + commits.size() + " commits.");
			for (RevCommit next : commits) {
				boolean result = propagateChanges(first, next);
				if (result) {
					first = next;
				}
			}
			logger.debug("Finished propagating the commits.");
		}
	}
	
	/**
	 * Propagates changes between an empty repository and a specific commit.
	 * 
	 * @param commitId id of the commit.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 * @throws IOException if the repository cannot be read.
	 */
	public void propagateChanges(String commitId) throws GitAPIException, IOException {
		propagateChanges(repoWrapper.getCommitForId(commitId));
	}

	/**
	 * Propagates changes between an empty repository and a specific commit.
	 * 
	 * @param commit the commit.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 * @throws IOException if something from the repositories cannot be read.
	 */
	public void propagateChanges(RevCommit commit) throws GitAPIException, IOException {
		propagateChanges(null, commit);
	}
	
	/**
	 * Propagates changes between two commits to the VSUM.
	 * 
	 * @param startId id of the first commit.
	 * @param endId id of the second commit.
	 * @return true if the changes are successfully propagated. false indicates that there are no changes for Java files
	 *         or the pre-processing failed.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 * @throws IOException it the repository cannot be read.
	 */
	public boolean propagateChanges(String startId, String endId) throws GitAPIException, IOException {
		return propagateChanges(repoWrapper.getCommitForId(startId), repoWrapper.getCommitForId(endId));
	}
	
	/**
	 * Propagates changes between two commits to the VSUM.
	 * 
	 * @param start the first commit.
	 * @param end the second commit.
	 * @return true if the changes are successfully propagated. false indicates that there are no changes for Java files
	 *         or the pre-processing failed.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 * @throws IOException if something from the repositories cannot be read.
	 */
	public boolean propagateChanges(RevCommit start, RevCommit end) throws GitAPIException, IOException {
		String commitId = end.getId().getName();
		logger.debug("Obtaining all differences.");
		List<DiffEntry> diffs = repoWrapper.computeDiffsBetweenTwoCommits(start, end, true, true);
		if (diffs.size() == 0) {
			logger.debug("No Java files changed for " + commitId + " so that no propagation is performed.");
			return false;
		}
		var cs = EvaluationDataContainer.getGlobalContainer().getChangeStatistic();
		String oldId = start != null ? start.getId().getName() : null;
		cs.setOldCommit(oldId != null ? oldId : "");
		cs.setNewCommit(commitId);
		cs.setNumberCommits(repoWrapper.getAllCommitsBetweenTwoCommits(oldId,
				commitId).size() + 1);
		logger.debug("Cleaning the repository.");
		repoWrapper.performCompleteClean();
		logger.debug("Checkout of " + commitId);
		repoWrapper.checkout(commitId);
		boolean preprocessResult = preprocess();
		if (!preprocessResult) {
			logger.debug("The preprocessing failed. Aborting.");
			return false;
		}
		logger.debug("Delegating the change propagation to the JavaParserAndPropagatorUtility.");
		JavaParserAndPropagatorUtility.parseAndPropagateJavaCode(repoWrapper.getRootDirectory().toPath(),
				fileLayout.getJavaModelFile(), vsum, fileLayout.getModuleConfiguration());
		logger.debug("Finished the propagation of " + commitId);
		return true;
	}
	
	private boolean preprocess() {
		File possibleFile = new File(CommitIntegrationSettingsContainer.getSettingsContainer()
				.getProperty(SettingKeys.PATH_TO_PREPROCESSING_SCRIPT));
		String absPath = possibleFile.getAbsolutePath();
		if (possibleFile.exists()) {
			return ExternalCommandExecutionUtility.runScript(
					this.repoWrapper.getRootDirectory(), absPath);
		} else {
			logger.debug(absPath + " not found.");
		}
		return false;
	}
	
	/**
	 * Initializes the propagator.
	 * The remote repository is cloned into the local directory. If the repository was
	 * already cloned beforehand, the Git repository is initialized from the local directory.
	 * 
	 * @throws IOException if something from the repositories cannot be read.
	 * @throws GitAPIException if there is an exception within the Git usage.
	 */
	public void initialize() throws IOException, GitAPIException {
		if (!repoWrapper.isInitialized()) {
			String[] files = repoWrapper.getRootDirectory().list();
			if (files != null && files.length > 0) {
				logger.debug("Initializing the git repository in " + repoWrapper.getRootDirectory().getAbsolutePath());
				repoWrapper.initFromRootDirectory();
			} else {
				logger.debug("Initializing the git repository from " + remoteRepository);
				repoWrapper.initFromRemoteRepository(remoteRepository);
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
	
	public GitRepositoryWrapper getWrapper() {
		return repoWrapper;
	}
	
	public JavaFileSystemLayout getJavaFileSystemLayout() {
		return fileLayout;
	}
}
