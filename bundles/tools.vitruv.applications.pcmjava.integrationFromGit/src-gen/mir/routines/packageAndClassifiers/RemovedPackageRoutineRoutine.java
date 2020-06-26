package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.containers.ContainersPackage;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemovedPackageRoutineRoutine extends AbstractRepairRoutineRealization {
  private RemovedPackageRoutineRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.containers.Package javaPackage, final RepositoryComponent pcmComponent) {
      return pcmComponent;
    }
    
    public EObject getCorrepondenceSourcePcmComponent(final org.emftext.language.java.containers.Package javaPackage) {
      return javaPackage;
    }
    
    public EObject getElement2(final org.emftext.language.java.containers.Package javaPackage, final RepositoryComponent pcmComponent) {
      return javaPackage;
    }
    
    public EObject getElement3(final org.emftext.language.java.containers.Package javaPackage, final RepositoryComponent pcmComponent) {
      return ContainersPackage.Literals.PACKAGE;
    }
  }
  
  public RemovedPackageRoutineRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RemovedPackageRoutineRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemovedPackageRoutineRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    org.palladiosimulator.pcm.repository.RepositoryComponent pcmComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourcePcmComponent(javaPackage), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (pcmComponent == null) {
    	return false;
    }
    registerObjectUnderModification(pcmComponent);
    deleteObject(userExecution.getElement1(javaPackage, pcmComponent));
    
    removeCorrespondenceBetween(userExecution.getElement2(javaPackage, pcmComponent), userExecution.getElement3(javaPackage, pcmComponent), "");
    
    postprocessElements();
    
    return true;
  }
}
