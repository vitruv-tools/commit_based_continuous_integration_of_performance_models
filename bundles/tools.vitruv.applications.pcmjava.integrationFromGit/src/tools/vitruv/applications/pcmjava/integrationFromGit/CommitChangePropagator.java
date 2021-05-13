package tools.vitruv.applications.pcmjava.integrationFromGit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
	private GitRepository repoWrapper;
	private VirtualModel vsum;
	
	public CommitChangePropagator(String repositoryPath, String localRepositoryPath, VirtualModel vSUM) {
		repoWrapper = new GitRepository(new File(localRepositoryPath), repositoryPath);
		vsum = vSUM;
	}
	
	public void propagteChanges() {
		RevCommit lastCommit = repoWrapper.getLatestCommit();
		List<RevCommit> nextCommits = repoWrapper.getNewCommits();
		for (RevCommit next : nextCommits) {
			repoWrapper.checkoutFromCommitId(next.getId().getName());
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
		// Ensure that the default branch is checked out with the newest commit.
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
