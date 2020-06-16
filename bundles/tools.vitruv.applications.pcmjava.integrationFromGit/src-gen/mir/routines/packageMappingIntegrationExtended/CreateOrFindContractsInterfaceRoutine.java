package mir.routines.packageMappingIntegrationExtended;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.io.IOException;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.containers.CompilationUnit;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindContractsInterfaceRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindContractsInterfaceRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package contractsPackage, final Repository pcmRepository, @Extension final RoutinesFacade _routinesFacade) {
      final Function1<OperationInterface, Boolean> _function = new Function1<OperationInterface, Boolean>() {
        public Boolean apply(final OperationInterface it) {
          String _entityName = it.getEntityName();
          String _name = javaInterface.getName();
          return Boolean.valueOf(Objects.equal(_entityName, _name));
        }
      };
      final OperationInterface pcmInterface = IterableExtensions.<OperationInterface>findFirst(Iterables.<OperationInterface>filter(pcmRepository.getInterfaces__Repository(), OperationInterface.class), _function);
      if ((pcmInterface == null)) {
        _routinesFacade.createInterface(javaInterface, compilationUnit, contractsPackage);
      } else {
        _routinesFacade.addInterfaceCorrespondence(pcmInterface, javaInterface, compilationUnit);
      }
    }
  }
  
  public CreateOrFindContractsInterfaceRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Interface javaInterface, final CompilationUnit compilationUnit, final org.emftext.language.java.containers.Package contractsPackage, final Repository pcmRepository) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.CreateOrFindContractsInterfaceRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaInterface = javaInterface;this.compilationUnit = compilationUnit;this.contractsPackage = contractsPackage;this.pcmRepository = pcmRepository;
  }
  
  private Interface javaInterface;
  
  private CompilationUnit compilationUnit;
  
  private org.emftext.language.java.containers.Package contractsPackage;
  
  private Repository pcmRepository;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindContractsInterfaceRoutine with input:");
    getLogger().debug("   javaInterface: " + this.javaInterface);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    getLogger().debug("   contractsPackage: " + this.contractsPackage);
    getLogger().debug("   pcmRepository: " + this.pcmRepository);
    
    userExecution.callRoutine1(javaInterface, compilationUnit, contractsPackage, pcmRepository, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
