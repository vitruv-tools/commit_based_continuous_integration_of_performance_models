package mir.reactions.packageAndClassifiers;

import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.root.RemoveRootEObject;

@SuppressWarnings("all")
public class RemovePackageReaction extends AbstractReactionRealization {
  private RemoveRootEObject<org.emftext.language.java.containers.Package> removeChange;
  
  private int currentlyMatchedChange;
  
  public RemovePackageReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    org.emftext.language.java.containers.Package oldValue = removeChange.getOldValue();
    int index = removeChange.getIndex();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.packageAndClassifiers.RemovePackageReaction.ActionUserExecution userExecution = new mir.reactions.packageAndClassifiers.RemovePackageReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(removeChange, oldValue, index, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private void resetChanges() {
    removeChange = null;
    currentlyMatchedChange = 0;
  }
  
  private boolean matchRemoveChange(final EChange change) {
    if (change instanceof RemoveRootEObject<?>) {
    	RemoveRootEObject<org.emftext.language.java.containers.Package> _localTypedChange = (RemoveRootEObject<org.emftext.language.java.containers.Package>) change;
    	if (!(_localTypedChange.getOldValue() instanceof org.emftext.language.java.containers.Package)) {
    		return false;
    	}
    	this.removeChange = (RemoveRootEObject<org.emftext.language.java.containers.Package>) change;
    	return true;
    }
    
    return false;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (currentlyMatchedChange == 0) {
    	if (!matchRemoveChange(change)) {
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
    
    public void callRoutine1(final RemoveRootEObject removeChange, final org.emftext.language.java.containers.Package oldValue, final int index, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.removedPackageRoutine(oldValue);
    }
  }
}
