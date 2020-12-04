package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.Interface;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemovedInterfaceEventRoutine extends AbstractRepairRoutineRealization {
  private RemovedInterfaceEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final Interface interfaze, final RepositoryComponent pcmComponent) {
      return pcmComponent;
    }
    
    public EObject getCorrepondenceSourcePcmComponent(final Interface interfaze) {
      return interfaze;
    }
  }
  
  public RemovedInterfaceEventRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface interfaze) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RemovedInterfaceEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.interfaze = interfaze;
  }
  
  private Interface interfaze;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemovedInterfaceEventRoutine with input:");
    getLogger().debug("   interfaze: " + this.interfaze);
    
    org.palladiosimulator.pcm.repository.RepositoryComponent pcmComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourcePcmComponent(interfaze), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (pcmComponent == null) {
    	return false;
    }
    registerObjectUnderModification(pcmComponent);
    deleteObject(userExecution.getElement1(interfaze, pcmComponent));
    
    postprocessElements();
    
    return true;
  }
}
