package mir.routines.packageMappingIntegrationExtended;

import java.io.IOException;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import org.palladiosimulator.pcm.repository.OperationInterface;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class AddInterfaceCorrespondenceRoutine extends AbstractRepairRoutineRealization {
  private AddInterfaceCorrespondenceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final OperationInterface pcmInterface, final Interface javaInterface, final CompilationUnit compilationUnit) {
      return pcmInterface;
    }
    
    public EObject getCorrepondenceSource1(final OperationInterface pcmInterface, final Interface javaInterface, final CompilationUnit compilationUnit) {
      return javaInterface;
    }
    
    public EObject getElement4(final OperationInterface pcmInterface, final Interface javaInterface, final CompilationUnit compilationUnit) {
      return compilationUnit;
    }
    
    public EObject getElement2(final OperationInterface pcmInterface, final Interface javaInterface, final CompilationUnit compilationUnit) {
      return javaInterface;
    }
    
    public EObject getElement3(final OperationInterface pcmInterface, final Interface javaInterface, final CompilationUnit compilationUnit) {
      return pcmInterface;
    }
  }
  
  public AddInterfaceCorrespondenceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final OperationInterface pcmInterface, final Interface javaInterface, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.AddInterfaceCorrespondenceRoutine.ActionUserExecution(getExecutionState(), this);
    this.pcmInterface = pcmInterface;this.javaInterface = javaInterface;this.compilationUnit = compilationUnit;
  }
  
  private OperationInterface pcmInterface;
  
  private Interface javaInterface;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddInterfaceCorrespondenceRoutine with input:");
    getLogger().debug("   pcmInterface: " + this.pcmInterface);
    getLogger().debug("   javaInterface: " + this.javaInterface);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(pcmInterface, javaInterface, compilationUnit), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.Interface.class,
    	(org.palladiosimulator.pcm.repository.Interface _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    addCorrespondenceBetween(userExecution.getElement1(pcmInterface, javaInterface, compilationUnit), userExecution.getElement2(pcmInterface, javaInterface, compilationUnit), "");
    
    addCorrespondenceBetween(userExecution.getElement3(pcmInterface, javaInterface, compilationUnit), userExecution.getElement4(pcmInterface, javaInterface, compilationUnit), "");
    
    postprocessElements();
    
    return true;
  }
}
