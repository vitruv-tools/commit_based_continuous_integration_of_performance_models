package mir.routines.remToPCM;

import java.io.IOException;
import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class SyncHostDeletionRoutine extends AbstractRepairRoutineRealization {
  private SyncHostDeletionRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourceEl(final RuntimeResourceContainer affectedObject) {
      return affectedObject;
    }
    
    public void callRoutine1(final RuntimeResourceContainer affectedObject, final ResourceContainer el, @Extension final RoutinesFacade _routinesFacade) {
      if ((el != null)) {
        _routinesFacade.removeCorrespondenceContainers(el, affectedObject);
      }
    }
  }
  
  public SyncHostDeletionRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final RuntimeResourceContainer affectedObject) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.remToPCM.SyncHostDeletionRoutine.ActionUserExecution(getExecutionState(), this);
    this.affectedObject = affectedObject;
  }
  
  private RuntimeResourceContainer affectedObject;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine SyncHostDeletionRoutine with input:");
    getLogger().debug("   affectedObject: " + this.affectedObject);
    
    org.palladiosimulator.pcm.resourceenvironment.ResourceContainer el = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceEl(affectedObject), // correspondence source supplier
    	org.palladiosimulator.pcm.resourceenvironment.ResourceContainer.class,
    	(org.palladiosimulator.pcm.resourceenvironment.ResourceContainer _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (el == null) {
    	return false;
    }
    registerObjectUnderModification(el);
    userExecution.callRoutine1(affectedObject, el, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
