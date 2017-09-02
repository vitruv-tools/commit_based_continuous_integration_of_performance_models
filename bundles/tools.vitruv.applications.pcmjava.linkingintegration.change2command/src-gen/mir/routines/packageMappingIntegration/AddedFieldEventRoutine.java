package mir.routines.packageMappingIntegration;

import java.io.IOException;
import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class AddedFieldEventRoutine extends AbstractRepairRoutineRealization {
  private RoutinesFacade actionsFacade;
  
  private AddedFieldEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourceBasicComponent(final ConcreteClassifier clazz, final Field field) {
      return clazz;
    }
    
    public EObject getCorrepondenceSourceOpInterface(final ConcreteClassifier clazz, final Field field, final BasicComponent basicComponent) {
      TypeReference _typeReference = field.getTypeReference();
      return _typeReference;
    }
    
    public void callRoutine1(final ConcreteClassifier clazz, final Field field, final BasicComponent basicComponent, final OperationInterface opInterface, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.createRequiredRole(basicComponent, opInterface, field);
    }
  }
  
  public AddedFieldEventRoutine(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ConcreteClassifier clazz, final Field field) {
    super(reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegration.AddedFieldEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.actionsFacade = new mir.routines.packageMappingIntegration.RoutinesFacade(getExecutionState(), this);
    this.clazz = clazz;this.field = field;
  }
  
  private ConcreteClassifier clazz;
  
  private Field field;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddedFieldEventRoutine with input:");
    getLogger().debug("   clazz: " + this.clazz);
    getLogger().debug("   field: " + this.field);
    
    org.palladiosimulator.pcm.repository.BasicComponent basicComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceBasicComponent(clazz, field), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.BasicComponent.class,
    	(org.palladiosimulator.pcm.repository.BasicComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (basicComponent == null) {
    	return false;
    }
    registerObjectUnderModification(basicComponent);
    org.palladiosimulator.pcm.repository.OperationInterface opInterface = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceOpInterface(clazz, field, basicComponent), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationInterface.class,
    	(org.palladiosimulator.pcm.repository.OperationInterface _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (opInterface == null) {
    	return false;
    }
    registerObjectUnderModification(opInterface);
    userExecution.callRoutine1(clazz, field, basicComponent, opInterface, actionsFacade);
    
    postprocessElements();
    
    return true;
  }
}
