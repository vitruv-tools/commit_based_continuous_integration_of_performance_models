package mir.routines.packageMappingIntegration;

import java.io.IOException;
import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.applications.pcmjava.util.java2pcm.TypeReferenceCorrespondenceHelper;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.userinteraction.UserInteractionOptions;

@SuppressWarnings("all")
public class ChangedMethodTypeRoutine extends AbstractRepairRoutineRealization {
  private ChangedMethodTypeRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final Method method, final TypeReference newType, final OperationSignature opSignature) {
      return opSignature;
    }
    
    public void update0Element(final Method method, final TypeReference newType, final OperationSignature opSignature) {
      final Repository repo = opSignature.getInterface__OperationSignature().getRepository__Interface();
      final DataType newReturnValue = TypeReferenceCorrespondenceHelper.getCorrespondingPCMDataTypeForTypeReference(newType, 
        this.correspondenceModel, this.userInteractor, repo, method.getArrayDimension());
      opSignature.setReturnType__OperationSignature(newReturnValue);
      this.userInteractor.getNotificationDialogBuilder().message(("Changed return type of opSig to " + newReturnValue)).windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
    }
    
    public EObject getCorrepondenceSourceOpSignature(final Method method, final TypeReference newType) {
      return method;
    }
  }
  
  public ChangedMethodTypeRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Method method, final TypeReference newType) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegration.ChangedMethodTypeRoutine.ActionUserExecution(getExecutionState(), this);
    this.method = method;this.newType = newType;
  }
  
  private Method method;
  
  private TypeReference newType;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine ChangedMethodTypeRoutine with input:");
    getLogger().debug("   method: " + this.method);
    getLogger().debug("   newType: " + this.newType);
    
    org.palladiosimulator.pcm.repository.OperationSignature opSignature = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceOpSignature(method, newType), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationSignature.class,
    	(org.palladiosimulator.pcm.repository.OperationSignature _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (opSignature == null) {
    	return false;
    }
    registerObjectUnderModification(opSignature);
    // val updatedElement userExecution.getElement1(method, newType, opSignature);
    userExecution.update0Element(method, newType, opSignature);
    
    postprocessElements();
    
    return true;
  }
}
