package mir.reactions.reactionsJavaToPcm.packageMappingIntegration;

import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.imports.Import;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.eobject.DeleteEObject;
import tools.vitruv.framework.change.echange.feature.reference.RemoveEReference;

@SuppressWarnings("all")
class RemoveImportReactionReaction extends AbstractReactionRealization {
  private RemoveEReference<JavaRoot, Import> removeChange;
  
  private DeleteEObject<EObject> deleteChange;
  
  private int currentlyMatchedChange;
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    org.emftext.language.java.containers.JavaRoot affectedEObject = removeChange.getAffectedEObject();
    EReference affectedFeature = removeChange.getAffectedFeature();
    org.emftext.language.java.imports.Import oldValue = removeChange.getOldValue();
    int index = removeChange.getIndex();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.routines.packageMappingIntegration.RoutinesFacade routinesFacade = new mir.routines.packageMappingIntegration.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveImportReactionReaction.ActionUserExecution userExecution = new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.RemoveImportReactionReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(removeChange, affectedEObject, affectedFeature, oldValue, index, routinesFacade);
    
    resetChanges();
  }
  
  private boolean matchDeleteChange(final EChange change) {
    if (change instanceof DeleteEObject<?>) {
    	DeleteEObject<org.eclipse.emf.ecore.EObject> _localTypedChange = (DeleteEObject<org.eclipse.emf.ecore.EObject>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof org.eclipse.emf.ecore.EObject)) {
    		return false;
    	}
    	this.deleteChange = (DeleteEObject<org.eclipse.emf.ecore.EObject>) change;
    	return true;
    }
    
    return false;
  }
  
  private void resetChanges() {
    removeChange = null;
    deleteChange = null;
    currentlyMatchedChange = 0;
  }
  
  private boolean matchRemoveChange(final EChange change) {
    if (change instanceof RemoveEReference<?, ?>) {
    	RemoveEReference<org.emftext.language.java.containers.JavaRoot, org.emftext.language.java.imports.Import> _localTypedChange = (RemoveEReference<org.emftext.language.java.containers.JavaRoot, org.emftext.language.java.imports.Import>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof org.emftext.language.java.containers.JavaRoot)) {
    		return false;
    	}
    	if (!_localTypedChange.getAffectedFeature().getName().equals("imports")) {
    		return false;
    	}
    	if (!(_localTypedChange.getOldValue() instanceof org.emftext.language.java.imports.Import)) {
    		return false;
    	}
    	this.removeChange = (RemoveEReference<org.emftext.language.java.containers.JavaRoot, org.emftext.language.java.imports.Import>) change;
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
    	return false; // Only proceed on the last of the expected changes
    }
    if (currentlyMatchedChange == 1) {
    	if (!matchDeleteChange(change)) {
    		resetChanges();
    		checkPrecondition(change); // Reexecute to potentially register this as first change
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
    
    public void callRoutine1(final RemoveEReference removeChange, final JavaRoot affectedEObject, final EReference affectedFeature, final Import oldValue, final int index, @Extension final RoutinesFacade _routinesFacade) {
    }
  }
}
