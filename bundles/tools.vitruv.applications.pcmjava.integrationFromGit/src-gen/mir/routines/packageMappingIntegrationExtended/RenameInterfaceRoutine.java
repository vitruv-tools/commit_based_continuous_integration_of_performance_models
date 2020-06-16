package mir.routines.packageMappingIntegrationExtended;

import java.io.IOException;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RenameInterfaceRoutine extends AbstractRepairRoutineRealization {
  private RenameInterfaceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final Interface javaInterface, final OperationInterface pcmInterface) {
      return pcmInterface;
    }
    
    public void update0Element(final Interface javaInterface, final OperationInterface pcmInterface) {
      pcmInterface.setEntityName(javaInterface.getName());
    }
    
    public EObject getCorrepondenceSourcePcmInterface(final Interface javaInterface) {
      return javaInterface;
    }
  }
  
  public RenameInterfaceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface javaInterface) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.RenameInterfaceRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaInterface = javaInterface;
  }
  
  private Interface javaInterface;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RenameInterfaceRoutine with input:");
    getLogger().debug("   javaInterface: " + this.javaInterface);
    
    org.palladiosimulator.pcm.repository.OperationInterface pcmInterface = getCorrespondingElement(
    	userExecution.getCorrepondenceSourcePcmInterface(javaInterface), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationInterface.class,
    	(org.palladiosimulator.pcm.repository.OperationInterface _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (pcmInterface == null) {
    	return false;
    }
    registerObjectUnderModification(pcmInterface);
    // val updatedElement userExecution.getElement1(javaInterface, pcmInterface);
    userExecution.update0Element(javaInterface, pcmInterface);
    
    postprocessElements();
    
    return true;
  }
}
