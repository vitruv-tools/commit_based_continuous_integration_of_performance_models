package org.splevo.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;
import org.splevo.ui.listeners.ApplyRefinementsAction;
import org.splevo.vpm.refinement.Refinement;

public class VPMRefinementBrowser extends EditorPart {

	/** The public id to reference the editor. */
	public static final String ID = "org.splevo.ui.editor.VPMRefinementEditor"; //$NON-NLS-1$

	/** The editor input of the browser. */
	private VPMRefinementBrowserInput input;
	
	/** The checkbox tree viewer to select the refinements to apply. */
	private CheckboxTreeViewer treeViewer;

	private FormToolkit toolkit;
	private Form form;

	@Override
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof VPMRefinementBrowserInput)) {
			throw new RuntimeException("Wrong input");
		}

		this.input = (VPMRefinementBrowserInput) editorInput;
		setSite(site);
		setInput(editorInput);
		setPartName("VPM Refinement Browser");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.setText("SPLevo Refinement Browser");
		toolkit.decorateFormHeading(form);
		form.getMenuManager().add(
				new ApplyRefinementsAction(this, "Apply Refinements"));

		createFormContent(form.getBody());
	}

	/**
	 * Create the form body providing the real content
	 * 
	 * @param parent
	 */
	private void createFormContent(Composite parent) {

		this.treeViewer = new CheckboxTreeViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setBounds(0, 0, 321, 427);

		treeViewer.setContentProvider(new RefinementTreeContentProvider());
		treeViewer.setLabelProvider(new RefinementTreeLabelProvider());
		treeViewer.setAutoExpandLevel(2);
		treeViewer.setInput(input.getRefinements());

		// Listener to expand the tree
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer = (TreeViewer) event.getViewer();
				IStructuredSelection thisSelection = (IStructuredSelection) event
						.getSelection();
				Object selectedNode = thisSelection.getFirstElement();
				viewer.setExpandedState(selectedNode,
						!viewer.getExpandedState(selectedNode));
			}
		});
	}
	
	/**
	 * Get the selected refinements.
	 * @return The list of refinements activated in the tree view.
	 */
	public List<Refinement> getSelectedRefinements(){
		List<Refinement> refinements = new ArrayList<Refinement>();
		Object[] checkedElements = this.treeViewer.getCheckedElements();
		for (Object object : checkedElements) {
			if(object instanceof Refinement){
				refinements.add((Refinement) object);
			}
		}
		return refinements;
	}
	
	/** 
	 * Get the splevo project editor that was originally used to trigger
	 * the analysis process.
	 */
	public SPLevoProjectEditor getSPLevoProjectEditor(){
		return this.input.getSplevoEditor();
	}

	/** Passing the focus request to the form. */
	public void setFocus() {
		form.setFocus();
	}

	/**
	 * Disposes the toolkit
	 */
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// never be called
	}

	@Override
	public void doSaveAs() {
		// never be called
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}
