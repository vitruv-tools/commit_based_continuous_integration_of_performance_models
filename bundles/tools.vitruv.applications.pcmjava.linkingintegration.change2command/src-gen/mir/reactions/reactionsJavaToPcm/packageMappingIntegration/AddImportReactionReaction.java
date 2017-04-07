package mir.reactions.reactionsJavaToPcm.packageMappingIntegration;

import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.imports.Import;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.compound.CreateAndInsertNonRoot;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;
import tools.vitruv.framework.userinteraction.UserInteracting;

@SuppressWarnings("all")
class AddImportReactionReaction extends AbstractReactionRealization {
  public AddImportReactionReaction(final UserInteracting userInteracting) {
    super(userInteracting);
  }
  
  public void executeReaction(final EChange change) {
    InsertEReference<JavaRoot, Import> typedChange = ((CreateAndInsertNonRoot<JavaRoot, Import>)change).getInsertChange();
    JavaRoot affectedEObject = typedChange.getAffectedEObject();
    EReference affectedFeature = typedChange.getAffectedFeature();
    Import newValue = typedChange.getNewValue();
    mir.routines.packageMappingIntegration.RoutinesFacade routinesFacade = new mir.routines.packageMappingIntegration.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddImportReactionReaction.ActionUserExecution userExecution = new mir.reactions.reactionsJavaToPcm.packageMappingIntegration.AddImportReactionReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(affectedEObject, affectedFeature, newValue, routinesFacade);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return CreateAndInsertNonRoot.class;
  }
  
  private boolean checkChangeProperties(final EChange change) {
    InsertEReference<JavaRoot, Import> relevantChange = ((CreateAndInsertNonRoot<JavaRoot, Import>)change).getInsertChange();
    if (!(relevantChange.getAffectedEObject() instanceof JavaRoot)) {
    	return false;
    }
    if (!relevantChange.getAffectedFeature().getName().equals("imports")) {
    	return false;
    }
    if (!(relevantChange.getNewValue() instanceof Import)) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof CreateAndInsertNonRoot)) {
    	return false;
    }
    getLogger().debug("Passed change type check of reaction " + this.getClass().getName());
    if (!checkChangeProperties(change)) {
    	return false;
    }
    getLogger().debug("Passed change properties check of reaction " + this.getClass().getName());
    getLogger().debug("Passed complete precondition check of reaction " + this.getClass().getName());
    return true;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final JavaRoot affectedEObject, final EReference affectedFeature, final Import newValue, @Extension final RoutinesFacade _routinesFacade) {
    }
  }
}
