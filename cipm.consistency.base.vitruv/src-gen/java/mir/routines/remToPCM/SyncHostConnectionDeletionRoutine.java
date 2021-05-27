package mir.routines.remToPCM;

import java.io.IOException;
import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class SyncHostConnectionDeletionRoutine extends AbstractRepairRoutineRealization {
  private SyncHostConnectionDeletionRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final RuntimeResourceContainerConnection affectedObject, final LinkingResource corr) {
      return affectedObject;
    }
    
    public EObject getElement2(final RuntimeResourceContainerConnection affectedObject, final LinkingResource corr) {
      return corr;
    }
    
    public EObject getCorrepondenceSourceCorr(final RuntimeResourceContainerConnection affectedObject) {
      return affectedObject;
    }
  }
  
  public SyncHostConnectionDeletionRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final RuntimeResourceContainerConnection affectedObject) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.remToPCM.SyncHostConnectionDeletionRoutine.ActionUserExecution(getExecutionState(), this);
    this.affectedObject = affectedObject;
  }
  
  private RuntimeResourceContainerConnection affectedObject;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine SyncHostConnectionDeletionRoutine with input:");
    getLogger().debug("   affectedObject: " + this.affectedObject);
    
    org.palladiosimulator.pcm.resourceenvironment.LinkingResource corr = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceCorr(affectedObject), // correspondence source supplier
    	org.palladiosimulator.pcm.resourceenvironment.LinkingResource.class,
    	(org.palladiosimulator.pcm.resourceenvironment.LinkingResource _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (corr == null) {
    	return false;
    }
    registerObjectUnderModification(corr);
    removeCorrespondenceBetween(userExecution.getElement1(affectedObject, corr), userExecution.getElement2(affectedObject, corr), "");
    
    postprocessElements();
    
    return true;
  }
}
