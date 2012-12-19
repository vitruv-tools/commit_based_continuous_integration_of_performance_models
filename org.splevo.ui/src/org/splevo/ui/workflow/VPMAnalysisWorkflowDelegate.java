package org.splevo.ui.workflow;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.splevo.project.SPLevoProject;
import org.splevo.ui.jobs.BuildRefinementModelJob;
import org.splevo.ui.jobs.LoadVPMJob;
import org.splevo.ui.jobs.OpenVPMRefinementBrowserJob;
import org.splevo.ui.jobs.SPLevoBlackBoard;
import org.splevo.ui.jobs.SaveRefinementModelJob;
import org.splevo.ui.jobs.UpdateUIJob;
import org.splevo.ui.jobs.SaveRefinementModelJob.FORMAT;
import org.splevo.ui.jobs.VPMAnalysisJob;
import org.splevo.vpm.refinement.VPMRefinementAnalyzer;

import de.uka.ipd.sdq.workflow.Blackboard;
import de.uka.ipd.sdq.workflow.IJob;
import de.uka.ipd.sdq.workflow.OrderPreservingBlackboardCompositeJob;
import de.uka.ipd.sdq.workflow.ParallelBlackboardInteractingCompositeJob;
import de.uka.ipd.sdq.workflow.ui.UIBasedWorkflow;
import de.uka.ipd.sdq.workflow.workbench.AbstractWorkbenchDelegate;

/**
 * A workflow delegate defining the workflow for the refinement of the variation point model.
 */
public class VPMAnalysisWorkflowDelegate extends
        AbstractWorkbenchDelegate<VPMAnalysisWorkflowConfiguration, UIBasedWorkflow<Blackboard<?>>> {

    /** The logger for this class. */
    private Logger logger = Logger.getLogger(VPMAnalysisWorkflowDelegate.class);

    /** The configuration of the workflow. */
    private VPMAnalysisWorkflowConfiguration config = null;

    /**
     * Constructor requiring a diffing workflow configuration.
     * 
     * @param config
     *            The configuration of the workflow.
     */
    public VPMAnalysisWorkflowDelegate(VPMAnalysisWorkflowConfiguration config) {
        this.config = config;
    }

    /**
     * Create the workflow.
     * 
     * @param config
     *            The configuration object for this workflow.
     * @return The prepared job.
     */
    @Override
    protected IJob createWorkflowJob(VPMAnalysisWorkflowConfiguration config) {

        // initialize the basic elements
        SPLevoProject splevoProject = config.getSplevoProjectEditor().getSplevoProject();
        OrderPreservingBlackboardCompositeJob<SPLevoBlackBoard> compositeJob = new OrderPreservingBlackboardCompositeJob<SPLevoBlackBoard>();
        compositeJob.setBlackboard(new SPLevoBlackBoard());

        // load the latest vpm model
        LoadVPMJob loadVPMJob = new LoadVPMJob(splevoProject);
        compositeJob.add(loadVPMJob);

        // trigger the configured refinement analysis
        ParallelBlackboardInteractingCompositeJob<SPLevoBlackBoard> parallelAnalysisJob = new ParallelBlackboardInteractingCompositeJob<SPLevoBlackBoard>();
        if (config.getAnalyzers().size() < 1) {
            logger.error("No analysis to perform configured.");
            return null;
        }
        for (VPMRefinementAnalyzer analyzerInstance : config.getAnalyzers()) {
            VPMAnalysisJob vpmAnalysisJob = new VPMAnalysisJob(analyzerInstance);
            parallelAnalysisJob.add(vpmAnalysisJob);
        }
        compositeJob.add(parallelAnalysisJob);

        BuildRefinementModelJob createRefinementModelJob = new BuildRefinementModelJob();
        compositeJob.add(createRefinementModelJob);

        IJob openViewerJob = new OpenVPMRefinementBrowserJob(config.getSplevoProjectEditor());
        compositeJob.add(openViewerJob);

        SaveRefinementModelJob saveRefinementModelJob = new SaveRefinementModelJob(splevoProject, null, FORMAT.CSV);
        compositeJob.add(saveRefinementModelJob);

        // job to update the user interface and see the recently created
        IJob updateUiJob = new UpdateUIJob(config.getSplevoProjectEditor(), null);
        compositeJob.add(updateUiJob);

        // return the prepared workflow
        return compositeJob;
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // nothing to do here
    }

    @Override
    protected boolean useSeparateConsoleForEachJobRun() {
        return false;
    }

    @Override
    protected VPMAnalysisWorkflowConfiguration getConfiguration() {
        return this.config;
    }
}
