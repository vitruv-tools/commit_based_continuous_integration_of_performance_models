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
public class CreateCorrespondenceLinksRoutine extends AbstractRepairRoutineRealization {
  private CreateCorrespondenceLinksRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final RuntimeResourceContainerConnection conn, final LinkingResource link) {
      return conn;
    }
    
    public EObject getElement2(final RuntimeResourceContainerConnection conn, final LinkingResource link) {
      return link;
    }
  }
  
  public CreateCorrespondenceLinksRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final RuntimeResourceContainerConnection conn, final LinkingResource link) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.remToPCM.CreateCorrespondenceLinksRoutine.ActionUserExecution(getExecutionState(), this);
    this.conn = conn;this.link = link;
  }
  
  private RuntimeResourceContainerConnection conn;
  
  private LinkingResource link;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateCorrespondenceLinksRoutine with input:");
    getLogger().debug("   conn: " + this.conn);
    getLogger().debug("   link: " + this.link);
    
    addCorrespondenceBetween(userExecution.getElement1(conn, link), userExecution.getElement2(conn, link), "");
    
    postprocessElements();
    
    return true;
  }
}
