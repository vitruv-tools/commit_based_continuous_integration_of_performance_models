package mir.routines.remToPCM;

import mir.routines.remToPCM.BuildLinkAndMapRoutine;
import mir.routines.remToPCM.CreateCorrespondenceContainersRoutine;
import mir.routines.remToPCM.CreateCorrespondenceLinksRoutine;
import mir.routines.remToPCM.EnrichContainerRoutine;
import mir.routines.remToPCM.RemoveCorrespondenceContainersRoutine;
import mir.routines.remToPCM.SyncHostConnectionCreationRoutine;
import mir.routines.remToPCM.SyncHostConnectionDeletionRoutine;
import mir.routines.remToPCM.SyncHostCreationRoutine;
import mir.routines.remToPCM.SyncHostDeletionRoutine;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
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
  
  public boolean syncHostDeletion(final RuntimeResourceContainer affectedObject) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    SyncHostDeletionRoutine routine = new SyncHostDeletionRoutine(_routinesFacade, _reactionExecutionState, _caller, affectedObject);
    return routine.applyRoutine();
  }
  
  public boolean syncHostCreation(final RuntimeResourceContainer affectedObject) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    SyncHostCreationRoutine routine = new SyncHostCreationRoutine(_routinesFacade, _reactionExecutionState, _caller, affectedObject);
    return routine.applyRoutine();
  }
  
  public boolean enrichContainer(final ResourceContainer container, final RuntimeResourceContainer corr) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    EnrichContainerRoutine routine = new EnrichContainerRoutine(_routinesFacade, _reactionExecutionState, _caller, container, corr);
    return routine.applyRoutine();
  }
  
  public boolean syncHostConnectionDeletion(final RuntimeResourceContainerConnection affectedObject) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    SyncHostConnectionDeletionRoutine routine = new SyncHostConnectionDeletionRoutine(_routinesFacade, _reactionExecutionState, _caller, affectedObject);
    return routine.applyRoutine();
  }
  
  public boolean syncHostConnectionCreation(final RuntimeResourceContainerConnection affectedObject) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    SyncHostConnectionCreationRoutine routine = new SyncHostConnectionCreationRoutine(_routinesFacade, _reactionExecutionState, _caller, affectedObject);
    return routine.applyRoutine();
  }
  
  public boolean buildLinkAndMap(final RuntimeResourceContainerConnection conn, final ResourceEnvironment parent, final ResourceContainer containerFrom, final ResourceContainer containerTo) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    BuildLinkAndMapRoutine routine = new BuildLinkAndMapRoutine(_routinesFacade, _reactionExecutionState, _caller, conn, parent, containerFrom, containerTo);
    return routine.applyRoutine();
  }
  
  public boolean removeCorrespondenceContainers(final ResourceContainer filtered, final RuntimeResourceContainer affectedObject) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    RemoveCorrespondenceContainersRoutine routine = new RemoveCorrespondenceContainersRoutine(_routinesFacade, _reactionExecutionState, _caller, filtered, affectedObject);
    return routine.applyRoutine();
  }
  
  public boolean createCorrespondenceContainers(final ResourceContainer filtered, final RuntimeResourceContainer affectedObject) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateCorrespondenceContainersRoutine routine = new CreateCorrespondenceContainersRoutine(_routinesFacade, _reactionExecutionState, _caller, filtered, affectedObject);
    return routine.applyRoutine();
  }
  
  public boolean createCorrespondenceLinks(final RuntimeResourceContainerConnection conn, final LinkingResource link) {
    RoutinesFacade _routinesFacade = this;
    ReactionExecutionState _reactionExecutionState = this._getExecutionState().getReactionExecutionState();
    CallHierarchyHaving _caller = this._getExecutionState().getCaller();
    CreateCorrespondenceLinksRoutine routine = new CreateCorrespondenceLinksRoutine(_routinesFacade, _reactionExecutionState, _caller, conn, link);
    return routine.applyRoutine();
  }
}
