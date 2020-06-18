package mir.reactions.packageAndClassifiers;

import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.feature.reference.RemoveEReference;

@SuppressWarnings("all")
public class RemoveInterfaceEventReaction extends AbstractReactionRealization {
  private RemoveEReference<CompilationUnit, Interface> removeChange;
  
  private int currentlyMatchedChange;
  
  public RemoveInterfaceEventReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    org.emftext.language.java.containers.CompilationUnit affectedEObject = removeChange.getAffectedEObject();
    EReference affectedFeature = removeChange.getAffectedFeature();
    org.emftext.language.java.classifiers.Interface oldValue = removeChange.getOldValue();
    int index = removeChange.getIndex();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.packageAndClassifiers.RemoveInterfaceEventReaction.ActionUserExecution userExecution = new mir.reactions.packageAndClassifiers.RemoveInterfaceEventReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(removeChange, affectedEObject, affectedFeature, oldValue, index, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private void resetChanges() {
    removeChange = null;
    currentlyMatchedChange = 0;
  }
  
  private boolean matchRemoveChange(final EChange change) {
    if (change instanceof RemoveEReference<?, ?>) {
    	RemoveEReference<org.emftext.language.java.containers.CompilationUnit, org.emftext.language.java.classifiers.Interface> _localTypedChange = (RemoveEReference<org.emftext.language.java.containers.CompilationUnit, org.emftext.language.java.classifiers.Interface>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof org.emftext.language.java.containers.CompilationUnit)) {
    		return false;
    	}
    	if (!_localTypedChange.getAffectedFeature().getName().equals("classifiers")) {
    		return false;
    	}
    	if (!(_localTypedChange.getOldValue() instanceof org.emftext.language.java.classifiers.Interface)) {
    		return false;
    	}
    	this.removeChange = (RemoveEReference<org.emftext.language.java.containers.CompilationUnit, org.emftext.language.java.classifiers.Interface>) change;
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
    
    public void callRoutine1(final RemoveEReference removeChange, final CompilationUnit affectedEObject, final EReference affectedFeature, final Interface oldValue, final int index, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.removedInterfaceEvent(oldValue);
    }
  }
}
