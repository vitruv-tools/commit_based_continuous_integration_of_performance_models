package mir.routines.remToPCM;

import com.google.common.base.Objects;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;

import java.io.IOException;
import mir.routines.remToPCM.RoutinesFacade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class SyncHostCreationRoutine extends AbstractRepairRoutineRealization {
  private SyncHostCreationRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSource1(final RuntimeResourceContainer affectedObject) {
      return affectedObject;
    }
    
    public EObject getCorrepondenceSourceParent(final RuntimeResourceContainer affectedObject) {
      EObject _eContainer = affectedObject.eContainer();
      return _eContainer;
    }
    
    public void callRoutine1(final RuntimeResourceContainer affectedObject, final ResourceEnvironment parent, final ResourceContainer nContainer, @Extension final RoutinesFacade _routinesFacade) {
      final Function1<ResourceContainer, Boolean> _function = new Function1<ResourceContainer, Boolean>() {
        public Boolean apply(final ResourceContainer it) {
          String _entityName = it.getEntityName();
          String _hostname = affectedObject.getHostname();
          return Boolean.valueOf(Objects.equal(_entityName, _hostname));
        }
      };
      final ResourceContainer filtered = IterableExtensions.<ResourceContainer>findFirst(parent.getResourceContainer_ResourceEnvironment(), _function);
      if ((filtered != null)) {
        _routinesFacade.createCorrespondenceContainers(filtered, affectedObject);
      } else {
        EList<ResourceContainer> _resourceContainer_ResourceEnvironment = parent.getResourceContainer_ResourceEnvironment();
        _resourceContainer_ResourceEnvironment.add(nContainer);
        _routinesFacade.enrichContainer(nContainer, affectedObject);
        _routinesFacade.createCorrespondenceContainers(nContainer, affectedObject);
      }
    }
  }
  
  public SyncHostCreationRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final RuntimeResourceContainer affectedObject) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.remToPCM.SyncHostCreationRoutine.ActionUserExecution(getExecutionState(), this);
    this.affectedObject = affectedObject;
  }
  
  private RuntimeResourceContainer affectedObject;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine SyncHostCreationRoutine with input:");
    getLogger().debug("   affectedObject: " + this.affectedObject);
    
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(affectedObject), // correspondence source supplier
    	org.palladiosimulator.pcm.resourceenvironment.ResourceContainer.class,
    	(org.palladiosimulator.pcm.resourceenvironment.ResourceContainer _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment parent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceParent(affectedObject), // correspondence source supplier
    	org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment.class,
    	(org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (parent == null) {
    	return false;
    }
    registerObjectUnderModification(parent);
    org.palladiosimulator.pcm.resourceenvironment.ResourceContainer nContainer = org.palladiosimulator.pcm.resourceenvironment.impl.ResourceenvironmentFactoryImpl.eINSTANCE.createResourceContainer();
    notifyObjectCreated(nContainer);
    
    userExecution.callRoutine1(affectedObject, parent, nContainer, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
