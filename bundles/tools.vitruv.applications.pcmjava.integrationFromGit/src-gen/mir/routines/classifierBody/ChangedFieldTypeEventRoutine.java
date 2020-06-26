package mir.routines.classifierBody;

import java.io.IOException;
import java.util.Optional;
import mir.routines.classifierBody.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.userinteraction.UserInteractionOptions;
import tools.vitruv.framework.userinteraction.builder.NotificationInteractionBuilder;

@SuppressWarnings("all")
public class ChangedFieldTypeEventRoutine extends AbstractRepairRoutineRealization {
  private ChangedFieldTypeEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourceBasicComponent(final Field field, final TypeReference oldType, final TypeReference newType, final Optional<OperationInterface> oldCorrespondingOpInterface, final Optional<OperationInterface> opInterface, final Optional<OperationRequiredRole> opRequiredRole) {
      ConcreteClassifier _containingConcreteClassifier = field.getContainingConcreteClassifier();
      return _containingConcreteClassifier;
    }
    
    public EObject getCorrepondenceSourceOldCorrespondingOpInterface(final Field field, final TypeReference oldType, final TypeReference newType) {
      return oldType;
    }
    
    public EObject getCorrepondenceSourceOpRequiredRole(final Field field, final TypeReference oldType, final TypeReference newType, final Optional<OperationInterface> oldCorrespondingOpInterface, final Optional<OperationInterface> opInterface) {
      return field;
    }
    
    public EObject getCorrepondenceSourceOpInterface(final Field field, final TypeReference oldType, final TypeReference newType, final Optional<OperationInterface> oldCorrespondingOpInterface) {
      return newType;
    }
    
    public void callRoutine1(final Field field, final TypeReference oldType, final TypeReference newType, final Optional<OperationInterface> oldCorrespondingOpInterface, final Optional<OperationInterface> opInterface, final Optional<OperationRequiredRole> opRequiredRole, final BasicComponent basicComponent, @Extension final RoutinesFacade _routinesFacade) {
      if (((oldCorrespondingOpInterface.isPresent() && opInterface.isPresent()) && opRequiredRole.isPresent())) {
        this.userInteractor.getNotificationDialogBuilder().message("the operation required role has been changed").windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
        OperationRequiredRole _get = opRequiredRole.get();
        _get.setRequiredInterface__OperationRequiredRole(opInterface.get());
        return;
      }
      if (((!oldCorrespondingOpInterface.isPresent()) && opInterface.isPresent())) {
        NotificationInteractionBuilder _notificationDialogBuilder = this.userInteractor.getNotificationDialogBuilder();
        String _entityName = basicComponent.getEntityName();
        String _plus = ("Create OperationRequiredRole between Component " + _entityName);
        String _plus_1 = (_plus + " and Interface ");
        String _entityName_1 = opInterface.get().getEntityName();
        String _plus_2 = (_plus_1 + _entityName_1);
        _notificationDialogBuilder.message(_plus_2).windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
        _routinesFacade.createRequiredRole(basicComponent, opInterface.get(), field);
        return;
      }
      if (((oldCorrespondingOpInterface.isPresent() && (!opInterface.isPresent())) && opRequiredRole.isPresent())) {
        NotificationInteractionBuilder _notificationDialogBuilder_1 = this.userInteractor.getNotificationDialogBuilder();
        String _entityName_2 = basicComponent.getEntityName();
        String _plus_3 = ("Remove OperationRequiredRole between Component " + _entityName_2);
        String _plus_4 = (_plus_3 + " and Interface ");
        String _entityName_3 = oldCorrespondingOpInterface.get().getEntityName();
        String _plus_5 = (_plus_4 + _entityName_3);
        _notificationDialogBuilder_1.message(_plus_5).windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
        _routinesFacade.removeRequiredRoleAndCorrespondence(opRequiredRole.get(), field);
      }
    }
  }
  
  public ChangedFieldTypeEventRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Field field, final TypeReference oldType, final TypeReference newType) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.classifierBody.ChangedFieldTypeEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.field = field;this.oldType = oldType;this.newType = newType;
  }
  
  private Field field;
  
  private TypeReference oldType;
  
  private TypeReference newType;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine ChangedFieldTypeEventRoutine with input:");
    getLogger().debug("   field: " + this.field);
    getLogger().debug("   oldType: " + this.oldType);
    getLogger().debug("   newType: " + this.newType);
    
    	Optional<org.palladiosimulator.pcm.repository.OperationInterface> oldCorrespondingOpInterface = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceOldCorrespondingOpInterface(field, oldType, newType), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.OperationInterface.class,
    		(org.palladiosimulator.pcm.repository.OperationInterface _element) -> true, // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(oldCorrespondingOpInterface.isPresent() ? oldCorrespondingOpInterface.get() : null);
    	Optional<org.palladiosimulator.pcm.repository.OperationInterface> opInterface = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceOpInterface(field, oldType, newType, oldCorrespondingOpInterface), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.OperationInterface.class,
    		(org.palladiosimulator.pcm.repository.OperationInterface _element) -> true, // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(opInterface.isPresent() ? opInterface.get() : null);
    	Optional<org.palladiosimulator.pcm.repository.OperationRequiredRole> opRequiredRole = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceOpRequiredRole(field, oldType, newType, oldCorrespondingOpInterface, opInterface), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.OperationRequiredRole.class,
    		(org.palladiosimulator.pcm.repository.OperationRequiredRole _element) -> true, // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(opRequiredRole.isPresent() ? opRequiredRole.get() : null);
    org.palladiosimulator.pcm.repository.BasicComponent basicComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceBasicComponent(field, oldType, newType, oldCorrespondingOpInterface, opInterface, opRequiredRole), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.BasicComponent.class,
    	(org.palladiosimulator.pcm.repository.BasicComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (basicComponent == null) {
    	return false;
    }
    registerObjectUnderModification(basicComponent);
    userExecution.callRoutine1(field, oldType, newType, oldCorrespondingOpInterface, opInterface, opRequiredRole, basicComponent, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
