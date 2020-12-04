package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.containers.CompilationUnit;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

/**
 * *
 * nCreates a new architectural element and add correspondence.
 *  
 */
@SuppressWarnings("all")
public class CreateElementRoutine extends AbstractRepairRoutineRealization {
  private CreateElementRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSource1(final org.emftext.language.java.classifiers.Class javaClass, final org.emftext.language.java.containers.Package javaPackage, final CompilationUnit compilationUnit) {
      return javaClass;
    }
    
    public void callRoutine1(final org.emftext.language.java.classifiers.Class javaClass, final org.emftext.language.java.containers.Package javaPackage, final CompilationUnit compilationUnit, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.createArchitecturalElement(javaPackage, javaClass.getName(), IterableExtensions.<String>head(compilationUnit.getNamespaces()));
    }
  }
  
  public CreateElementRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.classifiers.Class javaClass, final org.emftext.language.java.containers.Package javaPackage, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateElementRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaClass = javaClass;this.javaPackage = javaPackage;this.compilationUnit = compilationUnit;
  }
  
  private org.emftext.language.java.classifiers.Class javaClass;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateElementRoutine with input:");
    getLogger().debug("   javaClass: " + this.javaClass);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(javaClass, javaPackage, compilationUnit), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.DataType.class,
    	(org.palladiosimulator.pcm.repository.DataType _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    userExecution.callRoutine1(javaClass, javaPackage, compilationUnit, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
