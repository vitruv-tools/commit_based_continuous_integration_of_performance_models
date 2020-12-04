package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class DeleteMetaElementForPackageRoutine extends AbstractRepairRoutineRealization {
  private DeleteMetaElementForPackageRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.containers.Package packageCorrespondingToMetaElement) {
      return packageCorrespondingToMetaElement;
    }
  }
  
  public DeleteMetaElementForPackageRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package packageCorrespondingToMetaElement) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.DeleteMetaElementForPackageRoutine.ActionUserExecution(getExecutionState(), this);
    this.packageCorrespondingToMetaElement = packageCorrespondingToMetaElement;
  }
  
  private org.emftext.language.java.containers.Package packageCorrespondingToMetaElement;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine DeleteMetaElementForPackageRoutine with input:");
    getLogger().debug("   packageCorrespondingToMetaElement: " + this.packageCorrespondingToMetaElement);
    
    deleteObject(userExecution.getElement1(packageCorrespondingToMetaElement));
    
    postprocessElements();
    
    return true;
  }
}
