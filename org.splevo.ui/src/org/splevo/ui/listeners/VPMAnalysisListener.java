package org.splevo.ui.listeners;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Shell;
import org.splevo.ui.editors.SPLevoProjectEditor;
import org.splevo.ui.wizards.RefinementProcessConfigurationWizard;
import org.splevo.ui.workflow.VPMAnalysisWorkflowConfiguration;
import org.splevo.ui.workflow.VPMAnalysisWorkflowDelegate;

/**
 * Mouse adapter to listen for events which trigger the refinement process 
 * of a variation point model.
 */
public class VPMAnalysisListener extends MouseAdapter {

	/** The internal reference to the splevo project editor to work with. */
	private SPLevoProjectEditor splevoProjectEditor = null;

	/**
	 * Constructor requiring the reference to a splevoProject.
	 * 
	 * @param splevoProject
	 *            The references to the project.
	 */
	public VPMAnalysisListener(SPLevoProjectEditor splevoProjectEditor) {
		this.splevoProjectEditor = splevoProjectEditor;
	}

	@Override
	public void mouseUp(MouseEvent e) {

		// Initialize the requried data
		VPMAnalysisWorkflowConfiguration config = buildWorflowConfiguration();
		Shell shell = e.widget.getDisplay().getShells()[0];
		
		// trigger the wizard to configure the refinement process
		WizardDialog wizardDialog = new WizardDialog(shell,
			      									new RefinementProcessConfigurationWizard(config));
		if (wizardDialog.open() != Window.OK) {
			MessageDialog.openError(shell, "Variation Point Analyses canceled", 
									"The configuration of the analysis to perform was cancled.");
			return;
		}
		
		
		
		// validate configuration
		if(!config.isValid()){
			MessageDialog.openError(shell, "Invalid Project Configuration", config.getErrorMessage());
			return;
		}

		// trigger workflow
		
		VPMAnalysisWorkflowDelegate workflowDelegate = new VPMAnalysisWorkflowDelegate(config);
		IAction action = new Action("Refine VPM"){};
		workflowDelegate.run(action);
	}

	/**
	 * Build the configuration for the workflow.
	 * @return The prepared configuration.
	 */
	private VPMAnalysisWorkflowConfiguration buildWorflowConfiguration() {
		VPMAnalysisWorkflowConfiguration config = new VPMAnalysisWorkflowConfiguration();
		config.setSplevoProject(splevoProjectEditor.getSplevoProject());
		config.setSplevoProjectEditor(splevoProjectEditor);
		return config;
	}

}
