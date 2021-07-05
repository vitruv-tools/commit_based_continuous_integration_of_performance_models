package tools.vitruv.applications.pcmjava.commitintegration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

import tools.vitruv.applications.pcmjava.commitintegration.propagation.JavaStateBasedChangeResolutionStrategy;
import tools.vitruv.framework.change.description.VitruviusChange;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

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
	private InternalVirtualModel vsum;
	private File localRemoteRepository;
	private String remoteRepository;
	private File workSpaceCopy;
	private File dependencyDir;
	
	private static class URIURIMapping {
		private URI oldURI;
		private URI newURI;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param repositoryPath path to a local Git repository which will be observed.
	 * @param javaCacheDir path to a local directory in which Java files are cached.
	 *                     This includes the cloning of the Git repository.
	 * @param vSUM the VSUM which is used to propagate the changes.
	 */
	public CommitChangePropagator(File repositoryPath, String javaCacheDir, InternalVirtualModel vSUM) {
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
	public CommitChangePropagator(String repositoryPath, String javaCacheDir, InternalVirtualModel vSUM) {
		remoteRepository = repositoryPath;
		vsum = vSUM;
		prepareJavaCacheDir(javaCacheDir);
	}
	
	private void prepareJavaCacheDir(String dir) {
		File localCacheDir = new File(dir);
		File rootDir = new File(localCacheDir, "local-repo-clone");
		repoWrapper = new GitRepositoryWrapper(rootDir);
		workSpaceCopy = new File(localCacheDir, "vsum-variant");
		dependencyDir = new File(workSpaceCopy, "dependencies");
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
		logger.debug("Checkout of " + commitId);
		repoWrapper.checkout(commitId);
		boolean preprocessResult = preprocess();
		if (!preprocessResult) {
			logger.debug("The preprocessing failed. Aborting.");
			return false;
		}
		logger.debug("Analyzing the differences.");
		ArrayList<URIURIMapping> changed = new ArrayList<>();
		for (DiffEntry diffEntry : diffs) {
			changed.add(updateSources(diffEntry));
		}
		logger.debug("Loading the changed files.");
		ResourceSetImpl set = new ResourceSetImpl();
		changed.forEach(u -> {
			if (u.newURI != null) {
				set.getResource(u.newURI, true);
			}
		});
		List<Resource> newResources = new ArrayList<>(set.getResources());
		for (Resource r : newResources) {
			EcoreUtil.resolveAll(r);
		}
		List<Resource> oldResources = new ArrayList<>();
		changed.forEach(u -> {
			if (u.oldURI != null) {
				oldResources.add(vsum.getModelInstance(u.oldURI).getResource());
			}
		});
		if (oldResources.size() == 0 && start == null) {
			logger.debug("Integrating the code with the JavaIntegrationUtility.");
			JavaIntegrationUtility.integrateJavaCode(workSpaceCopy.toPath(), vsum);
		} else {
			logger.debug("Calculating the change sequence for the changed files.");
			var strategy = new JavaStateBasedChangeResolutionStrategy();
			VitruviusChange changeSequence = null;
			if (oldResources.size() == 0) {
				changeSequence = strategy.getChangeSequenceForResourceSet(set, newResources);
			} else {
				changeSequence = strategy.getChangeSequenceBetweenResourceSet(set,
						oldResources.get(0).getResourceSet(), newResources, oldResources);
			}
			logger.debug("Propagating the change sequence.");
			vsum.propagateChange(changeSequence);
		}
		logger.debug("Finished the propagation of " + commitId);
		return true;
	}
	
	private boolean preprocess() {
		int result = -1;
		repoWrapper.performCompleteClean();
		File possibleFile = new File("preprocess.bat");
		String absPath = possibleFile.getAbsolutePath();
		if (possibleFile.exists()) {
			logger.debug("Executing " + absPath + " for the preprocessing.");
			result = runPreprocessingScript("cmd.exe", "/c", "\"" + absPath + "\"");
		} else {
			logger.debug(absPath + " not found.");
			possibleFile = new File("preprocess.sh");
			absPath = possibleFile.getAbsolutePath();
			if (possibleFile.exists()) {
				logger.debug("Executing " + absPath + " for the preprocessing.");
				result = runPreprocessingScript(absPath);
			} else {
				logger.debug(absPath + " not found.");
			}
		}
		if (result != 0) {
			return false;
		}
		FileUtils.deleteQuietly(dependencyDir);
		logger.debug("Copying the dependencies.");
		try {
			Files.walk(repoWrapper.getRootDirectory().getParentFile().toPath())
				.filter(p -> p.getFileName().toString().endsWith(".jar"))
				.forEach(p -> {
					try {
						FileUtils.copyFileToDirectory(p.toFile(), dependencyDir);
					} catch (IOException e) {
						logger.error(e);
					}
				});
		} catch (IOException e) {
			logger.error(e);
		}
		return true;
	}
	
	private int runPreprocessingScript(String... command) {
		try {
			Process process = new ProcessBuilder().directory(repoWrapper.getRootDirectory().getParentFile())
					.command(command).start();
			return process.waitFor();
		} catch (IOException | InterruptedException e) {
			return -1;
		}
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
			} else if (localRemoteRepository != null) {
				logger.debug("Initializing the git repository from " + localRemoteRepository.getAbsolutePath());
				repoWrapper.initFromLocalRepository(localRemoteRepository);
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
	
	private URIURIMapping updateSources(DiffEntry entry) {
		String prefix = repoWrapper.getRootDirectory().getAbsolutePath() + File.separator;
		String prefixInCopy = workSpaceCopy.getAbsolutePath() + File.separator;
		String newFileInCopy = prefixInCopy + entry.getNewPath();
		URIURIMapping u = new URIURIMapping();
		// Perform file operations to transfer the changes in the local repository to the workspace copy.
		switch (entry.getChangeType()) {
			case MODIFY:
				u.oldURI = URI.createFileURI(prefixInCopy + entry.getOldPath());
			case COPY:
			case RENAME:	
			case ADD:
				u.newURI = URI.createFileURI(newFileInCopy);
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
			case RENAME:
			case DELETE:
				u.oldURI = URI.createFileURI(prefixInCopy + entry.getOldPath());
				new File(prefixInCopy + entry.getOldPath()).delete();
				break;
			default:
				break;
		}
		return u;
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
	
	protected GitRepositoryWrapper getWrapper() {
		return repoWrapper;
	}
}
