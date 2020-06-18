package mir.routines.classifierBody;

import java.io.IOException;
import mir.routines.classifierBody.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Field;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class AddAssemblyContextCorrespondenceRoutine extends AbstractRepairRoutineRealization {
  private AddAssemblyContextCorrespondenceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final AssemblyContext assemblyContext, final Field javaField) {
      return assemblyContext;
    }
    
    public EObject getElement2(final AssemblyContext assemblyContext, final Field javaField) {
      return javaField;
    }
  }
  
  public AddAssemblyContextCorrespondenceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final AssemblyContext assemblyContext, final Field javaField) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.classifierBody.AddAssemblyContextCorrespondenceRoutine.ActionUserExecution(getExecutionState(), this);
    this.assemblyContext = assemblyContext;this.javaField = javaField;
  }
  
  private AssemblyContext assemblyContext;
  
  private Field javaField;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddAssemblyContextCorrespondenceRoutine with input:");
    getLogger().debug("   assemblyContext: " + this.assemblyContext);
    getLogger().debug("   javaField: " + this.javaField);
    
    addCorrespondenceBetween(userExecution.getElement1(assemblyContext, javaField), userExecution.getElement2(assemblyContext, javaField), "");
    
    postprocessElements();
    
    return true;
  }
}
