package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class AddSystemCorrespondenceRoutine extends AbstractRepairRoutineRealization {
  private AddSystemCorrespondenceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.palladiosimulator.pcm.system.System pcmSystem, final org.emftext.language.java.containers.Package javaPackage) {
      return pcmSystem;
    }
    
    public EObject getElement2(final org.palladiosimulator.pcm.system.System pcmSystem, final org.emftext.language.java.containers.Package javaPackage) {
      return javaPackage;
    }
    
    public String getTag1(final org.palladiosimulator.pcm.system.System pcmSystem, final org.emftext.language.java.containers.Package javaPackage) {
      return "root_system";
    }
  }
  
  public AddSystemCorrespondenceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.palladiosimulator.pcm.system.System pcmSystem, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.AddSystemCorrespondenceRoutine.ActionUserExecution(getExecutionState(), this);
    this.pcmSystem = pcmSystem;this.javaPackage = javaPackage;
  }
  
  private org.palladiosimulator.pcm.system.System pcmSystem;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddSystemCorrespondenceRoutine with input:");
    getLogger().debug("   pcmSystem: " + this.pcmSystem);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    addCorrespondenceBetween(userExecution.getElement1(pcmSystem, javaPackage), userExecution.getElement2(pcmSystem, javaPackage), userExecution.getTag1(pcmSystem, javaPackage));
    
    postprocessElements();
    
    return true;
  }
}
