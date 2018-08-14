package mir.routines.packageMappingIntegration;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Optional;
import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.AnnotationInstanceOrModifier;
import org.emftext.language.java.modifiers.Private;
import org.emftext.language.java.modifiers.Protected;
import org.emftext.language.java.modifiers.Public;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.userinteraction.UserInteractionOptions;
import tools.vitruv.framework.userinteraction.builder.NotificationInteractionBuilder;

@SuppressWarnings("all")
public class ChangedMethodModifierEventRoutine extends AbstractRepairRoutineRealization {
  private ChangedMethodModifierEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourceOperationSignature(final Method method, final AnnotationInstanceOrModifier annotationOrModifier) {
      return method;
    }
    
    public EObject getCorrepondenceSourceOperationInterface(final Method method, final AnnotationInstanceOrModifier annotationOrModifier, final Optional<OperationSignature> operationSignature) {
      ConcreteClassifier _containingConcreteClassifier = method.getContainingConcreteClassifier();
      return _containingConcreteClassifier;
    }
    
    public void callRoutine1(final Method method, final AnnotationInstanceOrModifier annotationOrModifier, final Optional<OperationSignature> operationSignature, final OperationInterface operationInterface, @Extension final RoutinesFacade _routinesFacade) {
      if ((annotationOrModifier instanceof Public)) {
        _routinesFacade.createOperationSignature(operationInterface, method);
        return;
      } else {
        if ((operationSignature.isPresent() && ((annotationOrModifier instanceof Protected) || (annotationOrModifier instanceof Private)))) {
          NotificationInteractionBuilder _notificationDialogBuilder = this.userInteractor.getNotificationDialogBuilder();
          String _entityName = operationSignature.get().getEntityName();
          String _plus = ("Public method with correspondence has been made private. \r\n\t\t\t\t\tThe corresponding operaitonSignature " + _entityName);
          String _plus_1 = (_plus + " will be deleted as well.");
          _notificationDialogBuilder.message(_plus_1).windowModality(UserInteractionOptions.WindowModality.MODAL).startInteraction();
          this.correspondenceModel.removeCorrespondencesThatInvolveAtLeastAndDependend(Sets.<EObject>newHashSet(operationSignature.get()));
          EcoreUtil.remove(operationSignature.get());
          return;
        }
      }
    }
  }
  
  public ChangedMethodModifierEventRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Method method, final AnnotationInstanceOrModifier annotationOrModifier) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegration.ChangedMethodModifierEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.method = method;this.annotationOrModifier = annotationOrModifier;
  }
  
  private Method method;
  
  private AnnotationInstanceOrModifier annotationOrModifier;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine ChangedMethodModifierEventRoutine with input:");
    getLogger().debug("   method: " + this.method);
    getLogger().debug("   annotationOrModifier: " + this.annotationOrModifier);
    
    	Optional<org.palladiosimulator.pcm.repository.OperationSignature> operationSignature = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceOperationSignature(method, annotationOrModifier), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.OperationSignature.class,
    		(org.palladiosimulator.pcm.repository.OperationSignature _element) -> true, // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(operationSignature.isPresent() ? operationSignature.get() : null);
    org.palladiosimulator.pcm.repository.OperationInterface operationInterface = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceOperationInterface(method, annotationOrModifier, operationSignature), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationInterface.class,
    	(org.palladiosimulator.pcm.repository.OperationInterface _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (operationInterface == null) {
    	return false;
    }
    registerObjectUnderModification(operationInterface);
    userExecution.callRoutine1(method, annotationOrModifier, operationSignature, operationInterface, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
