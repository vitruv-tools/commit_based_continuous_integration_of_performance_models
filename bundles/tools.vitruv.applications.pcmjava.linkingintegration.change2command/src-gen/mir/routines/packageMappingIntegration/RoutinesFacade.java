package mir.routines.packageMappingIntegration;

import mir.routines.packageMappingIntegration.AddedFieldEventRoutine;
import mir.routines.packageMappingIntegration.AddedMethodEventRoutine;
import mir.routines.packageMappingIntegration.ChangedFieldTypeEventRoutine;
import mir.routines.packageMappingIntegration.ChangedMethodModifierEventRoutine;
import mir.routines.packageMappingIntegration.ChangedMethodTypeRoutine;
import mir.routines.packageMappingIntegration.CreateOperationSignatureRoutine;
import mir.routines.packageMappingIntegration.CreateRequiredRoleRoutine;
import mir.routines.packageMappingIntegration.CreatedMethodParameterEventRoutine;
import mir.routines.packageMappingIntegration.MethodParameterNameChangedEventRoutine;
import mir.routines.packageMappingIntegration.RemoveRequiredRoleAndCorrespondenceRoutine;
import mir.routines.packageMappingIntegration.RemovedFieldEventRoutine;
import mir.routines.packageMappingIntegration.RemovedMethodEventRoutine;
import mir.routines.packageMappingIntegration.RenamedMethodRoutine;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.modifiers.AnnotationInstanceOrModifier;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutinesFacade;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadeExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadesProvider;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.extensions.dslsruntime.reactions.structure.ReactionsImportPath;

@SuppressWarnings("all")
public class RoutinesFacade extends AbstractRepairRoutinesFacade {
  public RoutinesFacade(final RoutinesFacadesProvider routinesFacadesProvider, final ReactionsImportPath reactionsImportPath, final RoutinesFacadeExecutionState executionState) {
    super(routinesFacadesProvider, reactionsImportPath, executionState);
  }
  
  public boolean renamedMethod(final Method method, final String newMethodName) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RenamedMethodRoutine routine = new RenamedMethodRoutine(_routinesFacade, _reactionExecutionState, _caller, method, newMethodName);
    return routine.applyRoutine();
  }
  
  public boolean removedMethodEvent(final Method method) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RemovedMethodEventRoutine routine = new RemovedMethodEventRoutine(_routinesFacade, _reactionExecutionState, _caller, method);
    return routine.applyRoutine();
  }
  
  public boolean addedMethodEvent(final ConcreteClassifier clazz, final Method method) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    AddedMethodEventRoutine routine = new AddedMethodEventRoutine(_routinesFacade, _reactionExecutionState, _caller, clazz, method);
    return routine.applyRoutine();
  }
  
  public boolean createOperationSignature(final OperationInterface opInterface, final Method newMethod) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateOperationSignatureRoutine routine = new CreateOperationSignatureRoutine(_routinesFacade, _reactionExecutionState, _caller, opInterface, newMethod);
    return routine.applyRoutine();
  }
  
  public boolean createdMethodParameterEvent(final Method method, final Parameter parameter) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreatedMethodParameterEventRoutine routine = new CreatedMethodParameterEventRoutine(_routinesFacade, _reactionExecutionState, _caller, method, parameter);
    return routine.applyRoutine();
  }
  
  public boolean methodParameterNameChangedEvent(final Parameter parameter, final String oldParameterName, final String newParameterName) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    MethodParameterNameChangedEventRoutine routine = new MethodParameterNameChangedEventRoutine(_routinesFacade, _reactionExecutionState, _caller, parameter, oldParameterName, newParameterName);
    return routine.applyRoutine();
  }
  
  public boolean changedMethodType(final Method method, final TypeReference newType) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    ChangedMethodTypeRoutine routine = new ChangedMethodTypeRoutine(_routinesFacade, _reactionExecutionState, _caller, method, newType);
    return routine.applyRoutine();
  }
  
  public boolean removedFieldEvent(final Field field) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RemovedFieldEventRoutine routine = new RemovedFieldEventRoutine(_routinesFacade, _reactionExecutionState, _caller, field);
    return routine.applyRoutine();
  }
  
  public boolean addedFieldEvent(final ConcreteClassifier clazz, final Field field) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    AddedFieldEventRoutine routine = new AddedFieldEventRoutine(_routinesFacade, _reactionExecutionState, _caller, clazz, field);
    return routine.applyRoutine();
  }
  
  public boolean createRequiredRole(final BasicComponent basicComponent, final OperationInterface opInterface, final Field field) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateRequiredRoleRoutine routine = new CreateRequiredRoleRoutine(_routinesFacade, _reactionExecutionState, _caller, basicComponent, opInterface, field);
    return routine.applyRoutine();
  }
  
  public boolean changedFieldTypeEvent(final Field field, final TypeReference oldType, final TypeReference newType) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    ChangedFieldTypeEventRoutine routine = new ChangedFieldTypeEventRoutine(_routinesFacade, _reactionExecutionState, _caller, field, oldType, newType);
    return routine.applyRoutine();
  }
  
  public boolean removeRequiredRoleAndCorrespondence(final OperationRequiredRole orr, final Field field) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RemoveRequiredRoleAndCorrespondenceRoutine routine = new RemoveRequiredRoleAndCorrespondenceRoutine(_routinesFacade, _reactionExecutionState, _caller, orr, field);
    return routine.applyRoutine();
  }
  
  public boolean changedMethodModifierEvent(final Method method, final AnnotationInstanceOrModifier annotationOrModifier) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    ChangedMethodModifierEventRoutine routine = new ChangedMethodModifierEventRoutine(_routinesFacade, _reactionExecutionState, _caller, method, annotationOrModifier);
    return routine.applyRoutine();
  }
}
