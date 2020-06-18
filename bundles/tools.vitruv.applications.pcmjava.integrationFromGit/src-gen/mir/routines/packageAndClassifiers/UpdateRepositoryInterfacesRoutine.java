package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.Interface;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class UpdateRepositoryInterfacesRoutine extends AbstractRepairRoutineRealization {
  private UpdateRepositoryInterfacesRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final OperationInterface pcmInterface, final Repository pcmRepository) {
      return pcmRepository;
    }
    
    public void update0Element(final OperationInterface pcmInterface, final Repository pcmRepository) {
      EList<Interface> _interfaces__Repository = pcmRepository.getInterfaces__Repository();
      _interfaces__Repository.add(pcmInterface);
    }
  }
  
  public UpdateRepositoryInterfacesRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final OperationInterface pcmInterface, final Repository pcmRepository) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.UpdateRepositoryInterfacesRoutine.ActionUserExecution(getExecutionState(), this);
    this.pcmInterface = pcmInterface;this.pcmRepository = pcmRepository;
  }
  
  private OperationInterface pcmInterface;
  
  private Repository pcmRepository;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine UpdateRepositoryInterfacesRoutine with input:");
    getLogger().debug("   pcmInterface: " + this.pcmInterface);
    getLogger().debug("   pcmRepository: " + this.pcmRepository);
    
    // val updatedElement userExecution.getElement1(pcmInterface, pcmRepository);
    userExecution.update0Element(pcmInterface, pcmRepository);
    
    postprocessElements();
    
    return true;
  }
}
