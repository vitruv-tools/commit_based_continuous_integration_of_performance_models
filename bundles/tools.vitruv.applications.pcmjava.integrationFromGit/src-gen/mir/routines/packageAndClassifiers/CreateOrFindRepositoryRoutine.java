package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.containers.ContainersPackage;
import org.palladiosimulator.pcm.repository.Repository;
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
    
    public EObject getCorrepondenceSource1(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
      return javaPackage;
    }
    
    public EObject getCorrepondenceSource2(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
      return ContainersPackage.Literals.PACKAGE;
    }
    
    public String getRetrieveTag1(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
      return newTag;
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag, @Extension final RoutinesFacade _routinesFacade) {
      final Repository foundRepository = IterableExtensions.<Repository>toList(this.correspondenceModel.<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class)).get(0);
      _routinesFacade.ensureFirstCaseUpperCaseRepositoryNaming(foundRepository, javaPackage);
      _routinesFacade.addRepositoryCorrespondence(foundRepository, javaPackage, newTag);
    }
  }
  
  public CreateOrFindRepositoryRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final String packageName, final String newTag) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateOrFindRepositoryRoutine.ActionUserExecution(getExecutionState(), this);
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
    userExecution.callRoutine1(javaPackage, packageName, newTag, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
