package mir.routines.remToPCM;

import java.io.IOException;
import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemoveCorrespondenceContainersRoutine extends AbstractRepairRoutineRealization {
  private RemoveCorrespondenceContainersRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final ResourceContainer filtered, final RuntimeResourceContainer affectedObject) {
      return affectedObject;
    }
    
    public EObject getElement2(final ResourceContainer filtered, final RuntimeResourceContainer affectedObject) {
      return filtered;
    }
  }
  
  public RemoveCorrespondenceContainersRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ResourceContainer filtered, final RuntimeResourceContainer affectedObject) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.remToPCM.RemoveCorrespondenceContainersRoutine.ActionUserExecution(getExecutionState(), this);
    this.filtered = filtered;this.affectedObject = affectedObject;
  }
  
  private ResourceContainer filtered;
  
  private RuntimeResourceContainer affectedObject;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemoveCorrespondenceContainersRoutine with input:");
    getLogger().debug("   filtered: " + this.filtered);
    getLogger().debug("   affectedObject: " + this.affectedObject);
    
    removeCorrespondenceBetween(userExecution.getElement1(filtered, affectedObject), userExecution.getElement2(filtered, affectedObject), "");
    
    postprocessElements();
    
    return true;
  }
}
