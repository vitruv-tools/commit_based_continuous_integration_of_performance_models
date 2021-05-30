package tools.vitruv.applications.pcmjava.seffstatements.code2seff;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.seff.AbstractAction;

public class ResourceDemandingBehaviourDiff {

	List<AbstractAction> deletedAbstractActions;
	List<AbstractAction> addedAbstractActions;
	List<AbstractAction> modifiedAbstractActions;
	
	public ResourceDemandingBehaviourDiff() {
		deletedAbstractActions =  new ArrayList<AbstractAction>();
		addedAbstractActions = new ArrayList<AbstractAction>();
		modifiedAbstractActions = new ArrayList<AbstractAction>();
	}

	public List<AbstractAction> getDeletedAbstractActions() {
		return deletedAbstractActions;
	}

	public void setDeletedAbstractActions(List<AbstractAction> deletedAbstractActions) {
		this.deletedAbstractActions = deletedAbstractActions;
	}

	public List<AbstractAction> getAddedAbstractActions() {
		return addedAbstractActions;
	}

	public void setAddedAbstractActions(List<AbstractAction> addedAbstractActions) {
		this.addedAbstractActions = addedAbstractActions;
	}

	public List<AbstractAction> getModifiedAbstractActions() {
		return modifiedAbstractActions;
	}

	public void setModifiedAbstractActions(List<AbstractAction> modifiedAbstractActions) {
		this.modifiedAbstractActions = modifiedAbstractActions;
	}
	
	
}
