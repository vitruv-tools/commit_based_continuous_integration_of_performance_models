package org.splevo.ui.workflow;

import java.util.ArrayList;
import java.util.List;

import org.splevo.vpm.refinement.Refinement;
import org.splevo.vpm.variability.VariationPointModel;

/**
 * A configuration for a VPM analysis work flow to be executed.
 * A workflow contains a set of VPM analyses recommending refinements to be performed. 
 */
public class VPMRefinementWorkflowConfiguration extends
		BasicSPLevoWorkflowConfiguration {
	
	/** The variation point model. */
	private VariationPointModel variationPointModel = null;

	/** A list of refinements to be applied. */ 
	private final List<Refinement> refinements = new ArrayList<Refinement>();

	/**
	 * @return the variationPointModel
	 */
	public VariationPointModel getVariationPointModel() {
		return variationPointModel;
	}

	/**
	 * @param variationPointModel the variationPointModel to set
	 */
	public void setVariationPointModel(VariationPointModel variationPointModel) {
		this.variationPointModel = variationPointModel;
	}

	/**
	 * Get the list of refinements to be executed.
	 * @return the refinements
	 */
	public List<Refinement> getRefinements() {
		return refinements;
	}	
}
