package cipm.consistency.commitintegration.lang.impl.java;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.commitintegration.util.ExternalCommandExecutionUtils;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

/**
 * This class propagates changes from Git commits to JaMoPP models within Vitruv
 * by directly propagating the changes of a commit within a VSUM using the
 * state-based change propagation.
 * 
 * @author Martin Armbruster
 * @author Ilia Chupakhin
 */
@SuppressWarnings("restriction")
public class JavaCommitChangePropagator extends CommitChangePropagator {
	public JavaCommitChangePropagator(InternalVirtualModel vsum, GitRepositoryWrapper repoWrapper,
            LanguageFileSystemLayout fileLayout) {
        super(vsum, repoWrapper, fileLayout);
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
	public boolean propagateChanges(RevCommit start, RevCommit end) throws GitAPIException, IOException {
		String commitId = end.getId().getName();
		LOGGER.debug("Obtaining all differences.");
		List<DiffEntry> diffs = repoWrapper.computeDiffsBetweenTwoCommits(start, end);
		if (diffs.size() == 0) {
			LOGGER.debug("No Java files changed for " + commitId + " so that no propagation is performed.");
			return false;
		}
		var cs = EvaluationDataContainer.getGlobalContainer().getChangeStatistic();
		String oldId = start != null ? start.getId().getName() : null;
		cs.setOldCommit(oldId != null ? oldId : "");
		cs.setNewCommit(commitId);
		cs.setNumberCommits(repoWrapper.getAllCommitsBetweenTwoCommits(oldId, commitId).size() + 1);
//		LOGGER.debug("Cleaning the repository.");
//		repoWrapper.performCompleteClean();
		LOGGER.debug("Checkout of " + commitId);
		repoWrapper.checkout(commitId);
		boolean preprocessResult = preprocess();
		if (!preprocessResult) {
			LOGGER.debug("The preprocessing failed. Aborting.");
			return false;
		}
		LOGGER.debug("Delegating the change propagation to the JavaParserAndPropagatorUtility.");
		JavaParserAndPropagatorUtils.parseAndPropagateJavaCode(repoWrapper.getRepoPath(),
				fileLayout.getModelFile(), vsum, fileLayout.getModuleConfiguration());
		LOGGER.debug("Finished the propagation of " + commitId);
		return true;
	}

	private boolean preprocess() {
		File possibleFile = new File(CommitIntegrationSettingsContainer.getSettingsContainer()
				.getProperty(SettingKeys.PATH_TO_PREPROCESSING_SCRIPT));
		String absPath = possibleFile.getAbsolutePath();
		if (possibleFile.exists()) {
			return ExternalCommandExecutionUtils.runScript(repoWrapper.getRepoPath().toFile(), absPath);
		} else {
			LOGGER.debug(absPath + " not found.");
		}
		return false;
	}
}
