package mir.routines.classifierBody;

import mir.routines.classifierBody.AddAssemblyContextCorrespondenceRoutine;
import mir.routines.classifierBody.ChangeInnerDeclarationTypeRoutine;
import mir.routines.classifierBody.ChangeParameterNameRoutine;
import mir.routines.classifierBody.ChangeReturnTypeRoutine;
import mir.routines.classifierBody.ChangedFieldTypeEventRoutine;
import mir.routines.classifierBody.CreateAssemblyContextRoutine;
import mir.routines.classifierBody.CreateInnerDeclarationRoutine;
import mir.routines.classifierBody.CreateOperationRequiredRoleCorrespondingToFieldRoutine;
import mir.routines.classifierBody.CreateOrFindAssemblyContextRoutine;
import mir.routines.classifierBody.CreateParameterRoutine;
import mir.routines.classifierBody.CreateRequiredRoleRoutine;
import mir.routines.classifierBody.DeleteParameterRoutine;
import mir.routines.classifierBody.FieldCreatedCorrespondingToOperationInterfaceRoutine;
import mir.routines.classifierBody.FieldCreatedCorrespondingToRepositoryComponentRoutine;
import mir.routines.classifierBody.RemoveRequiredRoleAndCorrespondenceRoutine;
import mir.routines.classifierBody.RemovedFieldEventRoutine;
import mir.routines.classifierBody.RenameMemberRoutine;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.parameters.OrdinaryParameter;
import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.Parametrizable;
import org.emftext.language.java.types.TypeReference;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
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
  
  public boolean renameMember(final Member javaMember) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RenameMemberRoutine routine = new RenameMemberRoutine(_routinesFacade, _reactionExecutionState, _caller, javaMember);
    return routine.applyRoutine();
  }
  
  public boolean createParameter(final OrdinaryParameter javaParameter, final Parametrizable javaMethod) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateParameterRoutine routine = new CreateParameterRoutine(_routinesFacade, _reactionExecutionState, _caller, javaParameter, javaMethod);
    return routine.applyRoutine();
  }
  
  public boolean deleteParameter(final OrdinaryParameter javaParameter) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    DeleteParameterRoutine routine = new DeleteParameterRoutine(_routinesFacade, _reactionExecutionState, _caller, javaParameter);
    return routine.applyRoutine();
  }
  
  public boolean changeParameterName(final String newName, final Parameter javaParameter) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    ChangeParameterNameRoutine routine = new ChangeParameterNameRoutine(_routinesFacade, _reactionExecutionState, _caller, newName, javaParameter);
    return routine.applyRoutine();
  }
  
  public boolean createInnerDeclaration(final ConcreteClassifier classifier, final Field javaField) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateInnerDeclarationRoutine routine = new CreateInnerDeclarationRoutine(_routinesFacade, _reactionExecutionState, _caller, classifier, javaField);
    return routine.applyRoutine();
  }
  
  public boolean createOrFindAssemblyContext(final ConcreteClassifier classifier, final Field javaField) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateOrFindAssemblyContextRoutine routine = new CreateOrFindAssemblyContextRoutine(_routinesFacade, _reactionExecutionState, _caller, classifier, javaField);
    return routine.applyRoutine();
  }
  
  public boolean createAssemblyContext(final ConcreteClassifier classifier, final Field javaField) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateAssemblyContextRoutine routine = new CreateAssemblyContextRoutine(_routinesFacade, _reactionExecutionState, _caller, classifier, javaField);
    return routine.applyRoutine();
  }
  
  public boolean addAssemblyContextCorrespondence(final AssemblyContext assemblyContext, final Field javaField) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    AddAssemblyContextCorrespondenceRoutine routine = new AddAssemblyContextCorrespondenceRoutine(_routinesFacade, _reactionExecutionState, _caller, assemblyContext, javaField);
    return routine.applyRoutine();
  }
  
  public boolean fieldCreatedCorrespondingToOperationInterface(final Classifier classifier, final Field javaField) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    FieldCreatedCorrespondingToOperationInterfaceRoutine routine = new FieldCreatedCorrespondingToOperationInterfaceRoutine(_routinesFacade, _reactionExecutionState, _caller, classifier, javaField);
    return routine.applyRoutine();
  }
  
  public boolean fieldCreatedCorrespondingToRepositoryComponent(final Classifier classifier, final Field javaField) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    FieldCreatedCorrespondingToRepositoryComponentRoutine routine = new FieldCreatedCorrespondingToRepositoryComponentRoutine(_routinesFacade, _reactionExecutionState, _caller, classifier, javaField);
    return routine.applyRoutine();
  }
  
  public boolean createOperationRequiredRoleCorrespondingToField(final Field javaField, final OperationInterface operationInterface, final RepositoryComponent repoComponent) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateOperationRequiredRoleCorrespondingToFieldRoutine routine = new CreateOperationRequiredRoleCorrespondingToFieldRoutine(_routinesFacade, _reactionExecutionState, _caller, javaField, operationInterface, repoComponent);
    return routine.applyRoutine();
  }
  
  public boolean changeInnerDeclarationType(final TypeReference typeReference, final Field javaField) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    ChangeInnerDeclarationTypeRoutine routine = new ChangeInnerDeclarationTypeRoutine(_routinesFacade, _reactionExecutionState, _caller, typeReference, javaField);
    return routine.applyRoutine();
  }
  
  public boolean changedFieldTypeEvent(final Field field, final TypeReference oldType, final TypeReference newType) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    ChangedFieldTypeEventRoutine routine = new ChangedFieldTypeEventRoutine(_routinesFacade, _reactionExecutionState, _caller, field, oldType, newType);
    return routine.applyRoutine();
  }
  
  public boolean createRequiredRole(final BasicComponent basicComponent, final OperationInterface opInterface, final Field field) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateRequiredRoleRoutine routine = new CreateRequiredRoleRoutine(_routinesFacade, _reactionExecutionState, _caller, basicComponent, opInterface, field);
    return routine.applyRoutine();
  }
  
  public boolean removeRequiredRoleAndCorrespondence(final OperationRequiredRole orr, final Field field) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RemoveRequiredRoleAndCorrespondenceRoutine routine = new RemoveRequiredRoleAndCorrespondenceRoutine(_routinesFacade, _reactionExecutionState, _caller, orr, field);
    return routine.applyRoutine();
  }
  
  public boolean removedFieldEvent(final Field field) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RemovedFieldEventRoutine routine = new RemovedFieldEventRoutine(_routinesFacade, _reactionExecutionState, _caller, field);
    return routine.applyRoutine();
  }
  
  public boolean changeReturnType(final Method javaMethod, final TypeReference typeReference) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    ChangeReturnTypeRoutine routine = new ChangeReturnTypeRoutine(_routinesFacade, _reactionExecutionState, _caller, javaMethod, typeReference);
    return routine.applyRoutine();
  }
}
