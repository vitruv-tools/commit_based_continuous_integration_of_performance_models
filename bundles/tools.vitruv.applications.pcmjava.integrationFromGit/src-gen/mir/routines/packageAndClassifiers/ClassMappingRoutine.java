package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.CompilationUnit;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class ClassMappingRoutine extends AbstractRepairRoutineRealization {
  private ClassMappingRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.checkSystemAndComponent(javaPackage, javaClass);
      _routinesFacade.createElement(javaClass, javaPackage, compilationUnit);
    }
  }
  
  public ClassMappingRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.ClassMappingRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaClass = javaClass;this.compilationUnit = compilationUnit;this.javaPackage = javaPackage;
  }
  
  private org.emftext.language.java.classifiers.Class javaClass;
  
  private CompilationUnit compilationUnit;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine ClassMappingRoutine with input:");
    getLogger().debug("   javaClass: " + this.javaClass);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    userExecution.callRoutine1(javaClass, compilationUnit, javaPackage, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
