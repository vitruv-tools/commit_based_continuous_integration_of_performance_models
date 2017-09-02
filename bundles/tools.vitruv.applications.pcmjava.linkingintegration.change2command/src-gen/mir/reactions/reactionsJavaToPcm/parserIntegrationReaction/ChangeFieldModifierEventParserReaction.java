package mir.reactions.reactionsJavaToPcm.parserIntegrationReaction;

import mir.routines.parserIntegrationReaction.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.modifiers.AnnotationInstanceOrModifier;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.compound.CreateAndInsertNonRoot;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;

@SuppressWarnings("all")
class ChangeFieldModifierEventParserReaction extends AbstractReactionRealization {
  public void executeReaction(final EChange change) {
    InsertEReference<org.emftext.language.java.members.Field, org.emftext.language.java.modifiers.AnnotationInstanceOrModifier> typedChange = ((CreateAndInsertNonRoot<org.emftext.language.java.members.Field, org.emftext.language.java.modifiers.AnnotationInstanceOrModifier>)change).getInsertChange();
    org.emftext.language.java.members.Field affectedEObject = typedChange.getAffectedEObject();
    EReference affectedFeature = typedChange.getAffectedFeature();
    org.emftext.language.java.modifiers.AnnotationInstanceOrModifier newValue = typedChange.getNewValue();
    mir.routines.parserIntegrationReaction.RoutinesFacade routinesFacade = new mir.routines.parserIntegrationReaction.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.ChangeFieldModifierEventParserReaction.ActionUserExecution userExecution = new mir.reactions.reactionsJavaToPcm.parserIntegrationReaction.ChangeFieldModifierEventParserReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(affectedEObject, affectedFeature, newValue, routinesFacade);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return CreateAndInsertNonRoot.class;
  }
  
  private boolean checkChangeProperties(final EChange change) {
    InsertEReference<org.emftext.language.java.members.Field, org.emftext.language.java.modifiers.AnnotationInstanceOrModifier> relevantChange = ((CreateAndInsertNonRoot<org.emftext.language.java.members.Field, org.emftext.language.java.modifiers.AnnotationInstanceOrModifier>)change).getInsertChange();
    if (!(relevantChange.getAffectedEObject() instanceof org.emftext.language.java.members.Field)) {
    	return false;
    }
    if (!relevantChange.getAffectedFeature().getName().equals("annotationsAndModifiers")) {
    	return false;
    }
    if (!(relevantChange.getNewValue() instanceof org.emftext.language.java.modifiers.AnnotationInstanceOrModifier)) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof CreateAndInsertNonRoot)) {
    	return false;
    }
    getLogger().trace("Passed change type check of reaction " + this.getClass().getName());
    if (!checkChangeProperties(change)) {
    	return false;
    }
    getLogger().trace("Passed change properties check of reaction " + this.getClass().getName());
    getLogger().trace("Passed complete precondition check of reaction " + this.getClass().getName());
    return true;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final Field affectedEObject, final EReference affectedFeature, final AnnotationInstanceOrModifier newValue, @Extension final RoutinesFacade _routinesFacade) {
    }
  }
}
