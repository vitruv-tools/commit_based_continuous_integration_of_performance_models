package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class DeleteCorrespondenceBetweenJavaPackageAndMetaElementRoutine extends AbstractRepairRoutineRealization {
  private DeleteCorrespondenceBetweenJavaPackageAndMetaElementRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package metaElement) {
      return javaPackage;
    }
    
    public EObject getElement2(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package metaElement) {
      return metaElement;
    }
  }
  
  public DeleteCorrespondenceBetweenJavaPackageAndMetaElementRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package metaElement) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.DeleteCorrespondenceBetweenJavaPackageAndMetaElementRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;this.metaElement = metaElement;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private org.emftext.language.java.containers.Package metaElement;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine DeleteCorrespondenceBetweenJavaPackageAndMetaElementRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   metaElement: " + this.metaElement);
    
    removeCorrespondenceBetween(userExecution.getElement1(javaPackage, metaElement), userExecution.getElement2(javaPackage, metaElement), "");
    
    postprocessElements();
    
    return true;
  }
}
