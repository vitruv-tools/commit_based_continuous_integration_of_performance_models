package mir.routines.packageMappingIntegrationExtended;

import com.google.common.base.Objects;
import java.io.IOException;
import java.util.Optional;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.emftext.language.java.containers.ContainersPackage;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindRepositoryRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindRepositoryRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourceFoundRepository(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
      return RepositoryPackage.Literals.REPOSITORY;
    }
    
    public EObject getCorrepondenceSource1(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
      return javaPackage;
    }
    
    public boolean getCorrespondingModelElementsPreconditionFoundRepository(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag, final Repository foundRepository) {
      String _firstLower = StringExtensions.toFirstLower(foundRepository.getEntityName());
      boolean _equals = Objects.equal(_firstLower, packageName);
      return _equals;
    }
    
    public EObject getCorrepondenceSource2(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
      return ContainersPackage.Literals.PACKAGE;
    }
    
    public String getRetrieveTag1(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
      return newTag;
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag, final Optional<Repository> foundRepository, @Extension final RoutinesFacade _routinesFacade) {
      boolean _isPresent = foundRepository.isPresent();
      if (_isPresent) {
        _routinesFacade.ensureFirstCaseUpperCaseRepositoryNaming(foundRepository.get(), javaPackage);
        _routinesFacade.addRepositoryCorrespondence(foundRepository.get(), javaPackage, newTag);
      } else {
        _routinesFacade.createRepository(javaPackage, packageName, newTag);
      }
    }
  }
  
  public CreateOrFindRepositoryRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.CreateOrFindRepositoryRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;this.packageName = packageName;this.newTag = newTag;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private String packageName;
  
  private String newTag;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindRepositoryRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   packageName: " + this.packageName);
    getLogger().debug("   newTag: " + this.newTag);
    
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(javaPackage, packageName, newTag), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.Repository.class,
    	(org.palladiosimulator.pcm.repository.Repository _element) -> true, // correspondence precondition checker
    	userExecution.getRetrieveTag1(javaPackage, packageName, newTag)
    ).isEmpty()) {
    	return false;
    }
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource2(javaPackage, packageName, newTag), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.Repository.class,
    	(org.palladiosimulator.pcm.repository.Repository _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    	Optional<org.palladiosimulator.pcm.repository.Repository> foundRepository = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceFoundRepository(javaPackage, packageName, newTag), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.Repository.class,
    		(org.palladiosimulator.pcm.repository.Repository _element) -> userExecution.getCorrespondingModelElementsPreconditionFoundRepository(javaPackage, packageName, newTag, _element), // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(foundRepository.isPresent() ? foundRepository.get() : null);
    userExecution.callRoutine1(javaPackage, packageName, newTag, foundRepository, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
