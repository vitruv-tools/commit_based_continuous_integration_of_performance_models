package mir.routines.packageMappingIntegration;

import java.io.IOException;
import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.framework.userinteraction.UserInteractionOptions;
import tools.vitruv.framework.userinteraction.builder.ConfirmationInteractionBuilder;

@SuppressWarnings("all")
public class CreateOperationSignatureRoutine extends AbstractRepairRoutineRealization {
  private CreateOperationSignatureRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final OperationInterface opInterface, final Method newMethod) {
      return newMethod;
    }
    
    public void update0Element(final OperationInterface opInterface, final Method newMethod) {
      ConfirmationInteractionBuilder _confirmationDialogBuilder = this.userInteractor.getConfirmationDialogBuilder();
      String _name = newMethod.getName();
      String _plus = ("Should the new method " + _name);
      String _plus_1 = (_plus + " be part of the OperationInterface ");
      String _entityName = opInterface.getEntityName();
      String _plus_2 = (_plus_1 + _entityName);
      String _plus_3 = (_plus_2 + "? ");
      final Boolean selection = _confirmationDialogBuilder.message(_plus_3).windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
      if ((selection).booleanValue()) {
        OperationSignature opSig = RepositoryFactory.eINSTANCE.createOperationSignature();
        opSig.setEntityName(newMethod.getName());
        opSig.setInterface__OperationSignature(opInterface);
        CorrespondenceModelUtil.createAndAddCorrespondence(this.correspondenceModel, opSig, newMethod);
      }
    }
  }
  
  public CreateOperationSignatureRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final OperationInterface opInterface, final Method newMethod) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegration.CreateOperationSignatureRoutine.ActionUserExecution(getExecutionState(), this);
    this.opInterface = opInterface;this.newMethod = newMethod;
  }
  
  private OperationInterface opInterface;
  
  private Method newMethod;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOperationSignatureRoutine with input:");
    getLogger().debug("   opInterface: " + this.opInterface);
    getLogger().debug("   newMethod: " + this.newMethod);
    
    // val updatedElement userExecution.getElement1(opInterface, newMethod);
    userExecution.update0Element(opInterface, newMethod);
    
    postprocessElements();
    
    return true;
  }
}
