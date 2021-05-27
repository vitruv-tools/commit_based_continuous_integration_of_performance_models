package mir.reactions.remToPCM;

import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.eobject.CreateEObject;

@SuppressWarnings("all")
public class CreateHostLinkCorrespondenceReaction extends AbstractReactionRealization {
  private CreateEObject<RuntimeResourceContainerConnection> createChange;
  
  private int currentlyMatchedChange;
  
  public CreateHostLinkCorrespondenceReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection affectedEObject = createChange.getAffectedEObject();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.remToPCM.CreateHostLinkCorrespondenceReaction.ActionUserExecution userExecution = new mir.reactions.remToPCM.CreateHostLinkCorrespondenceReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(createChange, affectedEObject, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private void resetChanges() {
    createChange = null;
    currentlyMatchedChange = 0;
  }
  
  private boolean matchCreateChange(final EChange change) {
    if (change instanceof CreateEObject<?>) {
    	CreateEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection> _localTypedChange = (CreateEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection)) {
    		return false;
    	}
    	this.createChange = (CreateEObject<cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection>) change;
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
    
    public void callRoutine1(final CreateEObject createChange, final RuntimeResourceContainerConnection affectedEObject, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.syncHostConnectionCreation(affectedEObject);
    }
  }
}
