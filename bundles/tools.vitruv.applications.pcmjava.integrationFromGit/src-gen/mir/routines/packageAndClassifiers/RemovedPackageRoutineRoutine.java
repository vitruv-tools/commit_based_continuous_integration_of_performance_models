package mir.routines.packageAndClassifiers;

import java.io.IOException;
import java.util.Optional;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemovedPackageRoutineRoutine extends AbstractRepairRoutineRealization {
  private RemovedPackageRoutineRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.containers.Package javaPackage, final Optional<org.emftext.language.java.containers.Package> metaElement, final RepositoryComponent pcmComponent) {
      return pcmComponent;
    }
    
    public EObject getCorrepondenceSourcePcmComponent(final org.emftext.language.java.containers.Package javaPackage, final Optional<org.emftext.language.java.containers.Package> metaElement) {
      return javaPackage;
    }
    
    public EObject getCorrepondenceSourceMetaElement(final org.emftext.language.java.containers.Package javaPackage) {
      return javaPackage;
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final Optional<org.emftext.language.java.containers.Package> metaElement, final RepositoryComponent pcmComponent, @Extension final RoutinesFacade _routinesFacade) {
      boolean _isPresent = metaElement.isPresent();
      if (_isPresent) {
        _routinesFacade.deleteMetaElementForPackage(metaElement.get());
      }
    }
  }
  
  public RemovedPackageRoutineRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RemovedPackageRoutineRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemovedPackageRoutineRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    	Optional<org.emftext.language.java.containers.Package> metaElement = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceMetaElement(javaPackage), // correspondence source supplier
    		org.emftext.language.java.containers.Package.class,
    		(org.emftext.language.java.containers.Package _element) -> true, // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(metaElement.isPresent() ? metaElement.get() : null);
    org.palladiosimulator.pcm.repository.RepositoryComponent pcmComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourcePcmComponent(javaPackage, metaElement), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (pcmComponent == null) {
    	return false;
    }
    registerObjectUnderModification(pcmComponent);
    deleteObject(userExecution.getElement1(javaPackage, metaElement, pcmComponent));
    
    userExecution.callRoutine1(javaPackage, metaElement, pcmComponent, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
