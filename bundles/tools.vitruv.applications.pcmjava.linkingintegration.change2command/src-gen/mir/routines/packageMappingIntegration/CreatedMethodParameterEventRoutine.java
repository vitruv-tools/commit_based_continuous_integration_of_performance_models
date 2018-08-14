package mir.routines.packageMappingIntegration;

import java.io.IOException;
import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.Parameter;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import tools.vitruv.applications.pcmjava.util.java2pcm.TypeReferenceCorrespondenceHelper;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.userinteraction.UserInteractionOptions;

@SuppressWarnings("all")
public class CreatedMethodParameterEventRoutine extends AbstractRepairRoutineRealization {
  private CreatedMethodParameterEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final Method method, final Parameter parameter, final OperationSignature opSignature) {
      return opSignature;
    }
    
    public void update0Element(final Method method, final Parameter parameter, final OperationSignature opSignature) {
      this.userInteractor.getNotificationDialogBuilder().message(("Created new parameter for OperationSiganture" + opSignature)).windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
      final org.palladiosimulator.pcm.repository.Parameter pcmParameter = RepositoryFactory.eINSTANCE.createParameter();
      pcmParameter.setDataType__Parameter(TypeReferenceCorrespondenceHelper.getCorrespondingPCMDataTypeForTypeReference(parameter.getTypeReference(), this.correspondenceModel, 
        this.userInteractor, opSignature.getInterface__OperationSignature().getRepository__Interface(), parameter.getArrayDimension()));
      pcmParameter.setParameterName(parameter.getName());
    }
    
    public EObject getElement2(final Method method, final Parameter parameter, final OperationSignature opSignature) {
      return parameter;
    }
    
    public EObject getElement3(final Method method, final Parameter parameter, final OperationSignature opSignature) {
      return opSignature;
    }
    
    public EObject getCorrepondenceSourceOpSignature(final Method method, final Parameter parameter) {
      return method;
    }
  }
  
  public CreatedMethodParameterEventRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Method method, final Parameter parameter) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegration.CreatedMethodParameterEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.method = method;this.parameter = parameter;
  }
  
  private Method method;
  
  private Parameter parameter;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreatedMethodParameterEventRoutine with input:");
    getLogger().debug("   method: " + this.method);
    getLogger().debug("   parameter: " + this.parameter);
    
    org.palladiosimulator.pcm.repository.OperationSignature opSignature = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceOpSignature(method, parameter), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationSignature.class,
    	(org.palladiosimulator.pcm.repository.OperationSignature _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (opSignature == null) {
    	return false;
    }
    registerObjectUnderModification(opSignature);
    addCorrespondenceBetween(userExecution.getElement1(method, parameter, opSignature), userExecution.getElement2(method, parameter, opSignature), "");
    
    // val updatedElement userExecution.getElement3(method, parameter, opSignature);
    userExecution.update0Element(method, parameter, opSignature);
    
    postprocessElements();
    
    return true;
  }
}
