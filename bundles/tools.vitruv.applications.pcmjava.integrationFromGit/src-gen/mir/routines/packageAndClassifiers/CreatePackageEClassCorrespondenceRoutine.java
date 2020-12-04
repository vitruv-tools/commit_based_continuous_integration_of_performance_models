package mir.routines.packageAndClassifiers;

import java.io.IOException;
import java.util.List;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

/**
 * *
 * nRequired to enable locating existing packages with missing correspondences when keeping more than two models consistent.
 *  
 */
@SuppressWarnings("all")
public class CreatePackageEClassCorrespondenceRoutine extends AbstractRepairRoutineRealization {
  private CreatePackageEClassCorrespondenceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.containers.Package jPackage, final List<org.emftext.language.java.containers.Package> allPackages) {
      return jPackage;
    }
    
    public EObject getElement2(final org.emftext.language.java.containers.Package jPackage, final List<org.emftext.language.java.containers.Package> allPackages) {
      EClass _eClass = jPackage.eClass();
      return _eClass;
    }
    
    public EObject getCorrepondenceSourceAllPackages(final org.emftext.language.java.containers.Package jPackage) {
      EClass _eClass = jPackage.eClass();
      return _eClass;
    }
    
    public boolean checkMatcherPrecondition1(final org.emftext.language.java.containers.Package jPackage, final List<org.emftext.language.java.containers.Package> allPackages) {
      boolean _contains = allPackages.contains(jPackage);
      boolean _not = (!_contains);
      return _not;
    }
  }
  
  public CreatePackageEClassCorrespondenceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package jPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreatePackageEClassCorrespondenceRoutine.ActionUserExecution(getExecutionState(), this);
    this.jPackage = jPackage;
  }
  
  private org.emftext.language.java.containers.Package jPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreatePackageEClassCorrespondenceRoutine with input:");
    getLogger().debug("   jPackage: " + this.jPackage);
    
    List<org.emftext.language.java.containers.Package> allPackages = getCorrespondingElements(
    	userExecution.getCorrepondenceSourceAllPackages(jPackage), // correspondence source supplier
    	org.emftext.language.java.containers.Package.class,
    	(org.emftext.language.java.containers.Package _element) -> true, // correspondence precondition checker
    	null
    );
    for (EObject _element : allPackages) {	
    	registerObjectUnderModification(_element);
    }
    if (!userExecution.checkMatcherPrecondition1(jPackage, allPackages)) {
    	return false;
    }
    addCorrespondenceBetween(userExecution.getElement1(jPackage, allPackages), userExecution.getElement2(jPackage, allPackages), "");
    
    postprocessElements();
    
    return true;
  }
}
