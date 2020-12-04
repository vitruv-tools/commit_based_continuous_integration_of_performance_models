package mir.routines.packageAndClassifiers;

import java.io.IOException;
import java.util.Optional;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

/**
 * *
 * nAdds correspondence between component and package if not already exists, and adds component into repository.
 *  
 */
@SuppressWarnings("all")
public class AddCorrespondenceAndUpdateRepositoryRoutine extends AbstractRepairRoutineRealization {
  private AddCorrespondenceAndUpdateRepositoryRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage, final Repository pcmRepository, final Optional<RepositoryComponent> correspondingElement) {
      return pcmRepository;
    }
    
    public void update0Element(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage, final Repository pcmRepository, final Optional<RepositoryComponent> correspondingElement) {
      boolean _contains = pcmRepository.getComponents__Repository().contains(pcmComponent);
      boolean _not = (!_contains);
      if (_not) {
        EList<RepositoryComponent> _components__Repository = pcmRepository.getComponents__Repository();
        _components__Repository.add(pcmComponent);
      }
    }
    
    public EObject getCorrepondenceSourceCorrespondingElement(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage, final Repository pcmRepository) {
      return javaPackage;
    }
    
    public void callRoutine1(final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage, final Repository pcmRepository, final Optional<RepositoryComponent> correspondingElement, @Extension final RoutinesFacade _routinesFacade) {
      boolean _isPresent = correspondingElement.isPresent();
      boolean _not = (!_isPresent);
      if (_not) {
        _routinesFacade.addCorrespondenceBetweenPcmComponentAndJavaPackage(pcmComponent, javaPackage);
      }
    }
  }
  
  public AddCorrespondenceAndUpdateRepositoryRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final RepositoryComponent pcmComponent, final org.emftext.language.java.containers.Package javaPackage, final Repository pcmRepository) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.AddCorrespondenceAndUpdateRepositoryRoutine.ActionUserExecution(getExecutionState(), this);
    this.pcmComponent = pcmComponent;this.javaPackage = javaPackage;this.pcmRepository = pcmRepository;
  }
  
  private RepositoryComponent pcmComponent;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private Repository pcmRepository;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddCorrespondenceAndUpdateRepositoryRoutine with input:");
    getLogger().debug("   pcmComponent: " + this.pcmComponent);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   pcmRepository: " + this.pcmRepository);
    
    	Optional<org.palladiosimulator.pcm.repository.RepositoryComponent> correspondingElement = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceCorrespondingElement(pcmComponent, javaPackage, pcmRepository), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    		(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(correspondingElement.isPresent() ? correspondingElement.get() : null);
    userExecution.callRoutine1(pcmComponent, javaPackage, pcmRepository, correspondingElement, this.getRoutinesFacade());
    
    // val updatedElement userExecution.getElement1(pcmComponent, javaPackage, pcmRepository, correspondingElement);
    userExecution.update0Element(pcmComponent, javaPackage, pcmRepository, correspondingElement);
    
    postprocessElements();
    
    return true;
  }
}
