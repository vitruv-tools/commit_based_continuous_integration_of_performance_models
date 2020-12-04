package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.containers.CompilationUnit;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingRequiringEntity;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class AddCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnitRoutine extends AbstractRepairRoutineRealization {
  private AddCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnitRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final InterfaceProvidingRequiringEntity pcmComponentOrSystem, final CompilationUnit compilationUnit) {
      return pcmComponentOrSystem;
    }
    
    public EObject getElement2(final InterfaceProvidingRequiringEntity pcmComponentOrSystem, final CompilationUnit compilationUnit) {
      return compilationUnit;
    }
  }
  
  public AddCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnitRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final InterfaceProvidingRequiringEntity pcmComponentOrSystem, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.AddCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnitRoutine.ActionUserExecution(getExecutionState(), this);
    this.pcmComponentOrSystem = pcmComponentOrSystem;this.compilationUnit = compilationUnit;
  }
  
  private InterfaceProvidingRequiringEntity pcmComponentOrSystem;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnitRoutine with input:");
    getLogger().debug("   pcmComponentOrSystem: " + this.pcmComponentOrSystem);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    
    addCorrespondenceBetween(userExecution.getElement1(pcmComponentOrSystem, compilationUnit), userExecution.getElement2(pcmComponentOrSystem, compilationUnit), "");
    
    postprocessElements();
    
    return true;
  }
}
