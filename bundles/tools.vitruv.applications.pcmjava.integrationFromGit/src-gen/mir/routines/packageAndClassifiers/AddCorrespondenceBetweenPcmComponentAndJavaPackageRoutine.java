package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class AddCorrespondenceBetweenPcmComponentAndJavaPackageRoutine extends AbstractRepairRoutineRealization {
  private AddCorrespondenceBetweenPcmComponentAndJavaPackageRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage) {
      return pcmComponent;
    }
    
    public EObject getElement2(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage) {
      return javaPackage;
    }
  }
  
  public AddCorrespondenceBetweenPcmComponentAndJavaPackageRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.AddCorrespondenceBetweenPcmComponentAndJavaPackageRoutine.ActionUserExecution(getExecutionState(), this);
    this.pcmComponent = pcmComponent;this.javaPackage = javaPackage;
  }
  
  private RepositoryComponent pcmComponent;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddCorrespondenceBetweenPcmComponentAndJavaPackageRoutine with input:");
    getLogger().debug("   pcmComponent: " + this.pcmComponent);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    addCorrespondenceBetween(userExecution.getElement1(pcmComponent, javaPackage), userExecution.getElement2(pcmComponent, javaPackage), "");
    
    postprocessElements();
    
    return true;
  }
}
