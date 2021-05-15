package tools.vitruv.applications.pcmjava.integrationFromGit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private GitRepositoryWrapper repoWrapper;
	private VirtualModel vsum;
	private File localRemoteRepository;
	private String remoteRepository;
	private boolean isCurrentStateIntegratedIntoVSUM = false;
	
	public CommitChangePropagator(File repositoryPath, String localRepositoryPath, VirtualModel vSUM) {
		localRemoteRepository = repositoryPath;
		repoWrapper = new GitRepositoryWrapper(new File(localRepositoryPath));
		vsum = vSUM;
	}
	
	public CommitChangePropagator(String repositoryPath, String localRepositoryPath, VirtualModel vSUM) {
		remoteRepository = repositoryPath;
		repoWrapper = new GitRepositoryWrapper(new File(localRepositoryPath));
		vsum = vSUM;
	}
	
	public void setCurrentStateIntegratedIntoVSUM(boolean integrated) {
		isCurrentStateIntegratedIntoVSUM = integrated;
	}
	
	public void propagteChanges() throws IOException, GitAPIException {
		boolean isInitializedFromRemoteRepository = false;
		if (!repoWrapper.isInitialized()) {
			String[] files = repoWrapper.getRootDirectory().list();
			if (files != null && files.length > 0) {
				repoWrapper.initFromRootDirectory();
			} else if (localRemoteRepository != null) {
				repoWrapper.initFromLocalRepository(localRemoteRepository);
				isInitializedFromRemoteRepository = true;
			} else {
				repoWrapper.initFromRemoteRepository(remoteRepository);
				isInitializedFromRemoteRepository = true;
			}
		}
		RevCommit lastCommit;
		List<RevCommit> nextCommits;
		if (isInitializedFromRemoteRepository && !isCurrentStateIntegratedIntoVSUM) {
			lastCommit = null;
			nextCommits = repoWrapper.getAllCommitsBetweenTwoCommits(null, repoWrapper.getLatestCommit().getId().getName());
		} else {
			lastCommit = repoWrapper.getLatestCommit();
			nextCommits = repoWrapper.fetchAndGetNewCommits();
		}
		for (RevCommit next : nextCommits) {
			repoWrapper.checkout(next.getId().getName());
			List<DiffEntry> diffs = repoWrapper.computeDiffsBetweenTwoCommits(lastCommit, next, true, true);
			diffs = GitChangeApplier.sortDiffs(diffs);
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
	}
	
	private void internalPropagateChanges(List<ResourceChange> changes) {
		for (ResourceChange resChange : changes) {
			Resource newResource = null;
			if (resChange.getNewResourceURI() != null) {
				newResource = new ResourceSetImpl().getResource(resChange.getNewResourceURI(), true);
				EcoreUtil.resolveAll(newResource);
			}
			vsum.propagateChangedState(newResource, resChange.getOldResourceURI());
		}
	}
}
