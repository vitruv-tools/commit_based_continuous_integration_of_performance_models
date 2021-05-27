package mir.reactions.remToPCM;

import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.eobject.DeleteEObject;

@SuppressWarnings("all")
public class DeleteHostLinkCorrespondenceReaction extends AbstractReactionRealization {
  private DeleteEObject<RuntimeResourceContainerConnection> deleteChange;
  
  private int currentlyMatchedChange;
  
  public DeleteHostLinkCorrespondenceReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection affectedEObject = deleteChange.getAffectedEObject();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.remToPCM.DeleteHostLinkCorrespondenceReaction.ActionUserExecution userExecution = new mir.reactions.remToPCM.DeleteHostLinkCorrespondenceReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(deleteChange, affectedEObject, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private boolean matchDeleteChange(final EChange change) {
    if (change instanceof DeleteEObject<?>) {
    	DeleteEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection> _localTypedChange = (DeleteEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection)) {
    		return false;
    	}
    	this.deleteChange = (DeleteEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection>) change;
    	return true;
    }
    
    return false;
  }
  
  private void resetChanges() {
    deleteChange = null;
    currentlyMatchedChange = 0;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (currentlyMatchedChange == 0) {
    	if (!matchDeleteChange(change)) {
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
    
    public void callRoutine1(final DeleteEObject deleteChange, final RuntimeResourceContainerConnection affectedEObject, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.syncHostConnectionDeletion(affectedEObject);
    }
  }
}
