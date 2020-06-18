package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateInterfaceRoutine extends AbstractRepairRoutineRealization {
  private CreateInterfaceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void updatePcmInterfaceElement(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage, final OperationInterface pcmInterface) {
      pcmInterface.setEntityName(javaInterface.getName());
    }
    
    public void callRoutine1(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage, final OperationInterface pcmInterface, @Extension final RoutinesFacade _routinesFacade) {
      final Repository pcmRepository = IterableExtensions.<Repository>toList(this.correspondenceModel.<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class)).get(0);
      _routinesFacade.addInterfaceCorrespondence(pcmInterface, javaInterface, compilationUnit);
      _routinesFacade.updateRepositoryInterfaces(pcmInterface, pcmRepository);
    }
  }
  
  public CreateInterfaceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateInterfaceRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaInterface = javaInterface;this.compilationUnit = compilationUnit;this.javaPackage = javaPackage;
  }
  
  private Interface javaInterface;
  
  private CompilationUnit compilationUnit;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateInterfaceRoutine with input:");
    getLogger().debug("   javaInterface: " + this.javaInterface);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    org.palladiosimulator.pcm.repository.OperationInterface pcmInterface = org.palladiosimulator.pcm.repository.impl.RepositoryFactoryImpl.eINSTANCE.createOperationInterface();
    notifyObjectCreated(pcmInterface);
    userExecution.updatePcmInterfaceElement(javaInterface, compilationUnit, javaPackage, pcmInterface);
    
    userExecution.callRoutine1(javaInterface, compilationUnit, javaPackage, pcmInterface, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
