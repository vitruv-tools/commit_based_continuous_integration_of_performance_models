package mir.reactions.classifierBody;

import mir.routines.classifierBody.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.modifiers.AnnotationInstanceOrModifier;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.feature.reference.RemoveEReference;

@SuppressWarnings("all")
public class RemoveFieldModifierEventReaction extends AbstractReactionRealization {
  private RemoveEReference<Field, AnnotationInstanceOrModifier> removeChange;
  
  private int currentlyMatchedChange;
  
  public RemoveFieldModifierEventReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    org.emftext.language.java.members.Field affectedEObject = removeChange.getAffectedEObject();
    EReference affectedFeature = removeChange.getAffectedFeature();
    org.emftext.language.java.modifiers.AnnotationInstanceOrModifier oldValue = removeChange.getOldValue();
    int index = removeChange.getIndex();
    				
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.classifierBody.RemoveFieldModifierEventReaction.ActionUserExecution userExecution = new mir.reactions.classifierBody.RemoveFieldModifierEventReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(removeChange, affectedEObject, affectedFeature, oldValue, index, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private void resetChanges() {
    removeChange = null;
    currentlyMatchedChange = 0;
  }
  
  private boolean matchRemoveChange(final EChange change) {
    if (change instanceof RemoveEReference<?, ?>) {
    	RemoveEReference<org.emftext.language.java.members.Field, org.emftext.language.java.modifiers.AnnotationInstanceOrModifier> _localTypedChange = (RemoveEReference<org.emftext.language.java.members.Field, org.emftext.language.java.modifiers.AnnotationInstanceOrModifier>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof org.emftext.language.java.members.Field)) {
    		return false;
    	}
    	if (!_localTypedChange.getAffectedFeature().getName().equals("annotationsAndModifiers")) {
    		return false;
    	}
    	if (!(_localTypedChange.getOldValue() instanceof org.emftext.language.java.modifiers.AnnotationInstanceOrModifier)) {
    		return false;
    	}
    	this.removeChange = (RemoveEReference<org.emftext.language.java.members.Field, org.emftext.language.java.modifiers.AnnotationInstanceOrModifier>) change;
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
    
    public void callRoutine1(final RemoveEReference removeChange, final Field affectedEObject, final EReference affectedFeature, final AnnotationInstanceOrModifier oldValue, final int index, @Extension final RoutinesFacade _routinesFacade) {
    }
  }
}
