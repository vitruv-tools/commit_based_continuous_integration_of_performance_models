package cipm.consistency.commitintegration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;

import cipm.consistency.commitintegration.lang.lua.runtimedata.ChangedResources;
import cipm.consistency.models.CodeModelFacade;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.vsum.Propagation;

/**
 * This class is responsible for controlling the complete change propagation and adaptive
 * instrumentation.
 * 
 * @param <CM>
 *            The code model class that is used for the integration
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public abstract class CommitIntegrationController<CM extends CodeModelFacade> {
    private static final Logger LOGGER = Logger.getLogger(CommitIntegrationController.class.getName());
    protected CommitIntegrationState<CM> state;

    public void initialize(CommitIntegration<CM> commitIntegration)
            throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        state = new CommitIntegrationState<CM>();
        state.initialize(commitIntegration);
    }

    /**
     * Disposes the integration state if it is not fresh
     * 
     * @throws InvalidRemoteException
     * @throws TransportException
     * @throws IOException
     * @throws GitAPIException
     */
    protected void reset() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        if (!state.isFresh()) {
            LOGGER.info("Resetting commitintegration");
            var ci = state.getCommitIntegration();
            state.getDirLayout()
                .delete();
            state.dispose();
            state.initialize(ci, true);
        }
    }

    /**
     * Reload the current integration state from disk
     * 
     * @throws GitAPIException
     * @throws IOException
     * @throws TransportException
     * @throws InvalidRemoteException
     */
    protected void reload() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        var ci = state.getCommitIntegration();
        state.dispose();
        state.initialize(ci);
    }

    /**
     * Propagate the work tree that is currently checked out by the git repo wrapper.
     * 
     * @return The Propagation instance including the used model paths
     */
    private Optional<Propagation> propagateCurrentCheckout() {
        LOGGER.info(String.format("\n\tPropagating commit #%d: %s", state.getSnapshotCount() + 1,
                state.getGitRepositoryWrapper()
                    .getCurrentCommitHash()));

        var workTree = state.getGitRepositoryWrapper()
            .getWorkTree()
            .toPath();

        var previousParsedModelPath = state.getCurrentParsedModelPath();

        var resource = state.getCodeModelFacade()
            .parseSourceCodeDir(workTree);
        if (resource == null) {
            LOGGER.error("Error parsing code model, not running propagation");
            return Optional.empty();
        }
        
        
        // this informs the component set info singleton that we changed resourced which it had mapped infos for 
        ChangedResources.setResourcesWereChanged();

        var parsedModelPath = state.createParsedCodeModelSnapshot();
        state.setCurrentParsedModelPath(parsedModelPath);

        // do the actual propagation
        var propagation = state.getVsumFacade()
            .propagateResource(resource, state.getDirLayout()
                .getVsumCodeModelURI());

        var propagationResultCodeModelPath = state.createVsumCodeModelSnapshot();
        var propagationResultRepositoryModelPath = state.createRepositoryModelSnapshot();
        var propagationResultIMMPath = state.createInstrumentationModelSnapshot();

        propagation.setCommitIntegrationStateCopyPath(state.createSnapshot());

        // the rest should not be needed anymore
        propagation.setParsedCodeModelPreviousVersionPath(previousParsedModelPath);
        propagation.setParsedCodeModelTargetVersionPath(parsedModelPath);
        propagation.setPropagationResultCodeModelPath(propagationResultCodeModelPath);
        propagation.setPropagationResultRepositoryModelPath(propagationResultRepositoryModelPath);
        propagation.setPropagationResultIMMPath(propagationResultIMMPath);

//        state.createSnapshotWithCount(String.format("after_changes_original-%d_consequential-%d",
//                propagatedChanges.getOriginalChangeCount(), propagatedChanges.getConsequentialChangeCount()));
        return Optional.of(propagation);
    }

    /**
     * Propagates changes for a given list of commitsIds. If no commitIds are given, the current
     * checkout of the git repo will be propagated. If there is one commitIds are given, it is
     * checked out and propagated to the state. If the first commitId is null, a fresh commit
     * integration state will be used for the commit integration. If the first commitId is not null,
     * it is expected that this commitId was the last propagated commitId of the commit integration
     * state
     * 
     * @param commitIds
     *            ids of the commits.
     * @throws GitAPIException
     *             if there is an exception within the Git usage.
     * @throws IOException
     *             if the repository cannot be read.
     */
    public List<Optional<Propagation>> propagateChanges(String... commitIds) throws GitAPIException, IOException {
        if (commitIds.length == 0) {
            return List.of(propagateCurrentCheckout());
        } else if (commitIds.length == 1 && commitIds[0] != null) {
            checkout(commitIds[0]);
            return List.of(propagateCurrentCheckout());
        }

        // make sure the state is clean if the first id is null
        if (commitIds[0] == null) {
            reset();
        }

        var numberOfPropagations = commitIds.length - 1;
        List<Optional<Propagation>> allPropagatedChanges = new ArrayList<>(numberOfPropagations);

        for (var i = 0; i < numberOfPropagations; i++) {
            var propagatedChanges = propagateChanges(commitIds[i], commitIds[i + 1]);
            allPropagatedChanges.add(propagatedChanges);
        }
        return allPropagatedChanges;
    }


    protected boolean prePropagationChecks(String firstCommitId, String secondCommitId) {
        if (firstCommitId != null) {
            return true;
        }
        LOGGER.debug("Obtaining all differences.");
        List<DiffEntry> diffs;
        try {
            diffs = state.getGitRepositoryWrapper()
                .computeDiffsBetweenTwoCommits(firstCommitId, secondCommitId);
        } catch (RevisionSyntaxException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        if (diffs.isEmpty()) {
            LOGGER.info("No source files changed between " + firstCommitId + " and " + secondCommitId + ".");
            return false;
        }
        return true;
    }

    protected Optional<Propagation> propagateChanges(String firstCommitId, String secondCommitId)
            throws IncorrectObjectTypeException, IOException {
        if (!prePropagationChecks(firstCommitId, secondCommitId)) {
            LOGGER.info("Prechecks indicate no propagation is needed.");
            return Optional.empty();
        }

        var cs = EvaluationDataContainer.getGlobalContainer()
            .getChangeStatistic();
        cs.setOldCommit(firstCommitId);
        cs.setNewCommit(secondCommitId);
        cs.setNumberCommits(state.getGitRepositoryWrapper()
            .getAllCommitsBetweenTwoCommits(firstCommitId, secondCommitId)
            .size() + 1);

        if (checkout(secondCommitId)) {
            return propagateCurrentCheckout();
        }

        return Optional.empty();
    }

    /**
     * Can be overwritten to do processing after every checkout
     * 
     * @return
     */
    protected boolean preprocessCheckout() {
        return true;
    }

    protected boolean checkout(String commitId) {
        LOGGER.debug("Checkout of " + commitId);
        try {
            state.getGitRepositoryWrapper()
                .checkout(commitId);
            if (!preprocessCheckout()) {
                LOGGER.debug("The preprocessing failed. Aborting.");
                return false;
            }
            return true;
        } catch (GitAPIException e) {
            LOGGER.error("Unable to checkout", e);
        }
        return false;
    }

}
