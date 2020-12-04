package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemovedClassEventRoutine extends AbstractRepairRoutineRealization {
  private RemovedClassEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.classifiers.Class clazz, final RepositoryComponent pcmComponent) {
      return pcmComponent;
    }
    
    public EObject getCorrepondenceSourcePcmComponent(final org.emftext.language.java.classifiers.Class clazz) {
      return clazz;
    }
  }
  
  public RemovedClassEventRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.classifiers.Class clazz) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RemovedClassEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.clazz = clazz;
  }
  
  private org.emftext.language.java.classifiers.Class clazz;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemovedClassEventRoutine with input:");
    getLogger().debug("   clazz: " + this.clazz);
    
    org.palladiosimulator.pcm.repository.RepositoryComponent pcmComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourcePcmComponent(clazz), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (pcmComponent == null) {
    	return false;
    }
    registerObjectUnderModification(pcmComponent);
    deleteObject(userExecution.getElement1(clazz, pcmComponent));
    
    postprocessElements();
    
    return true;
  }
}
