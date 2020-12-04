package mir.routines.classifierBody;

import java.io.IOException;
import java.util.Optional;
import mir.routines.classifierBody.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import tools.vitruv.applications.util.temporary.java.JavaTypeUtil;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

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
      Classifier _normalizedClassifierFromTypeReference = JavaTypeUtil.getNormalizedClassifierFromTypeReference(oldType);
      return _normalizedClassifierFromTypeReference;
    }
    
    public EObject getCorrepondenceSourceOpRequiredRole(final Field field, final TypeReference oldType, final TypeReference newType, final Optional<OperationInterface> oldCorrespondingOpInterface, final Optional<OperationInterface> opInterface) {
      return field;
    }
    
    public EObject getCorrepondenceSourceOpInterface(final Field field, final TypeReference oldType, final TypeReference newType, final Optional<OperationInterface> oldCorrespondingOpInterface) {
      Classifier _normalizedClassifierFromTypeReference = JavaTypeUtil.getNormalizedClassifierFromTypeReference(newType);
      return _normalizedClassifierFromTypeReference;
    }
    
    public void callRoutine1(final Field field, final TypeReference oldType, final TypeReference newType, final Optional<OperationInterface> oldCorrespondingOpInterface, final Optional<OperationInterface> opInterface, final Optional<OperationRequiredRole> opRequiredRole, final BasicComponent basicComponent, @Extension final RoutinesFacade _routinesFacade) {
      if (((oldCorrespondingOpInterface.isPresent() && opInterface.isPresent()) && opRequiredRole.isPresent())) {
        OperationRequiredRole _get = opRequiredRole.get();
        _get.setRequiredInterface__OperationRequiredRole(opInterface.get());
        return;
      }
      if (((!oldCorrespondingOpInterface.isPresent()) && opInterface.isPresent())) {
        _routinesFacade.createRequiredRole(basicComponent, opInterface.get(), field);
        return;
      }
      if (((oldCorrespondingOpInterface.isPresent() && (!opInterface.isPresent())) && opRequiredRole.isPresent())) {
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
