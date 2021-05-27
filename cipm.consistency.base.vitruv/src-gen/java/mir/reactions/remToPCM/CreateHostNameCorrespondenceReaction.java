package mir.reactions.remToPCM;

import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.eobject.CreateEObject;

@SuppressWarnings("all")
public class CreateHostNameCorrespondenceReaction extends AbstractReactionRealization {
  private CreateEObject<RuntimeResourceContainer> createChange;
  
  private int currentlyMatchedChange;
  
  public CreateHostNameCorrespondenceReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer affectedEObject = createChange.getAffectedEObject();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.remToPCM.CreateHostNameCorrespondenceReaction.ActionUserExecution userExecution = new mir.reactions.remToPCM.CreateHostNameCorrespondenceReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(createChange, affectedEObject, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private void resetChanges() {
    createChange = null;
    currentlyMatchedChange = 0;
  }
  
  private boolean matchCreateChange(final EChange change) {
    if (change instanceof CreateEObject<?>) {
    	CreateEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer> _localTypedChange = (CreateEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer)) {
    		return false;
    	}
    	this.createChange = (CreateEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer>) change;
    	return true;
    }
    
    return false;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (currentlyMatchedChange == 0) {
    	if (!matchCreateChange(change)) {
    		resetChanges();
    		return false;
    	} else {
    		currentlyMatchedChange++;
    	}
    }
    
    return true;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final CreateEObject createChange, final RuntimeResourceContainer affectedEObject, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.syncHostCreation(affectedEObject);
    }
  }
}
