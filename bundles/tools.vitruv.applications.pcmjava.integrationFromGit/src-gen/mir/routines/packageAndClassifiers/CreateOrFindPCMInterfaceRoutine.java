package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.applications.util.temporary.java.JavaContainerAndClassifierUtil;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindPCMInterfaceRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindPCMInterfaceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final Interface javaInterface, final CompilationUnit compilationUnit, @Extension final RoutinesFacade _routinesFacade) {
      final org.emftext.language.java.containers.Package containingPackage = JavaContainerAndClassifierUtil.getContainingPackageFromCorrespondenceModel(javaInterface, this.correspondenceModel);
      final Repository pcmRepository = IterableExtensions.<Repository>toList(this.correspondenceModel.<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class)).get(0);
      _routinesFacade.createNonContractsInterface(javaInterface, compilationUnit, containingPackage);
    }
  }
  
  public CreateOrFindPCMInterfaceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface javaInterface, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateOrFindPCMInterfaceRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaInterface = javaInterface;this.compilationUnit = compilationUnit;
  }
  
  private Interface javaInterface;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindPCMInterfaceRoutine with input:");
    getLogger().debug("   javaInterface: " + this.javaInterface);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    
    userExecution.callRoutine1(javaInterface, compilationUnit, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
