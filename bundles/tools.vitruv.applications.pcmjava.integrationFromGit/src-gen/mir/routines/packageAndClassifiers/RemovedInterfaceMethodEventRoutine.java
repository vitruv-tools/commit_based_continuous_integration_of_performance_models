package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.InterfaceMethod;
import org.palladiosimulator.pcm.repository.OperationSignature;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemovedInterfaceMethodEventRoutine extends AbstractRepairRoutineRealization {
  private RemovedInterfaceMethodEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final InterfaceMethod interfaceMethod, final OperationSignature operationSignature) {
      return operationSignature;
    }
    
    public EObject getCorrepondenceSourceOperationSignature(final InterfaceMethod interfaceMethod) {
      return interfaceMethod;
    }
  }
  
  public RemovedInterfaceMethodEventRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final InterfaceMethod interfaceMethod) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RemovedInterfaceMethodEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.interfaceMethod = interfaceMethod;
  }
  
  private InterfaceMethod interfaceMethod;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemovedInterfaceMethodEventRoutine with input:");
    getLogger().debug("   interfaceMethod: " + this.interfaceMethod);
    
    org.palladiosimulator.pcm.repository.OperationSignature operationSignature = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceOperationSignature(interfaceMethod), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationSignature.class,
    	(org.palladiosimulator.pcm.repository.OperationSignature _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (operationSignature == null) {
    	return false;
    }
    registerObjectUnderModification(operationSignature);
    deleteObject(userExecution.getElement1(interfaceMethod, operationSignature));
    
    postprocessElements();
    
    return true;
  }
}
