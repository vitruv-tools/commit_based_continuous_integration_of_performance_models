package mir.routines.classifierBody;

import java.io.IOException;
import mir.routines.classifierBody.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Field;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemoveRequiredRoleAndCorrespondenceRoutine extends AbstractRepairRoutineRealization {
  private RemoveRequiredRoleAndCorrespondenceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final OperationRequiredRole orr, final Field field) {
      return orr;
    }
  }
  
  public RemoveRequiredRoleAndCorrespondenceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final OperationRequiredRole orr, final Field field) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.classifierBody.RemoveRequiredRoleAndCorrespondenceRoutine.ActionUserExecution(getExecutionState(), this);
    this.orr = orr;this.field = field;
  }
  
  private OperationRequiredRole orr;
  
  private Field field;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemoveRequiredRoleAndCorrespondenceRoutine with input:");
    getLogger().debug("   orr: " + this.orr);
    getLogger().debug("   field: " + this.field);
    
    deleteObject(userExecution.getElement1(orr, field));
    
    postprocessElements();
    
    return true;
  }
}
