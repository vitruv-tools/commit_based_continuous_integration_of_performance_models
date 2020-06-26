package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemoveOperationProvidedRoleRoutine extends AbstractRepairRoutineRealization {
  private RemoveOperationProvidedRoleRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final TypeReference typeReference, final OperationProvidedRole operationProvidedRole) {
      return operationProvidedRole;
    }
    
    public EObject getCorrepondenceSourceOperationProvidedRole(final TypeReference typeReference) {
      return typeReference;
    }
  }
  
  public RemoveOperationProvidedRoleRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final TypeReference typeReference) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RemoveOperationProvidedRoleRoutine.ActionUserExecution(getExecutionState(), this);
    this.typeReference = typeReference;
  }
  
  private TypeReference typeReference;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemoveOperationProvidedRoleRoutine with input:");
    getLogger().debug("   typeReference: " + this.typeReference);
    
    org.palladiosimulator.pcm.repository.OperationProvidedRole operationProvidedRole = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceOperationProvidedRole(typeReference), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationProvidedRole.class,
    	(org.palladiosimulator.pcm.repository.OperationProvidedRole _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (operationProvidedRole == null) {
    	return false;
    }
    registerObjectUnderModification(operationProvidedRole);
    deleteObject(userExecution.getElement1(typeReference, operationProvidedRole));
    
    postprocessElements();
    
    return true;
  }
}
