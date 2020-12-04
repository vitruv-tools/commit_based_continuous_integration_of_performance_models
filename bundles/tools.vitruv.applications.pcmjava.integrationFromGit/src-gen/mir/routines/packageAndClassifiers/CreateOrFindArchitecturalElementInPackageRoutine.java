package mir.routines.packageAndClassifiers;

import com.google.common.base.Objects;
import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindArchitecturalElementInPackageRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindArchitecturalElementInPackageRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSource1(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package containingPackage, final String rootPackageName) {
      return javaPackage;
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package containingPackage, final String rootPackageName, @Extension final RoutinesFacade _routinesFacade) {
      final Repository pcmRepository = IterableExtensions.<Repository>toList(this.correspondenceModel.<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class)).get(0);
      final Function1<RepositoryComponent, Boolean> _function = new Function1<RepositoryComponent, Boolean>() {
        public Boolean apply(final RepositoryComponent it) {
          String _entityName = it.getEntityName();
          String _firstUpper = StringExtensions.toFirstUpper(javaPackage.getName());
          return Boolean.valueOf(Objects.equal(_entityName, _firstUpper));
        }
      };
      final RepositoryComponent pcmComponentCandidate = IterableExtensions.<RepositoryComponent>findFirst(pcmRepository.getComponents__Repository(), _function);
      if ((pcmComponentCandidate == null)) {
        _routinesFacade.createArchitecturalElement(javaPackage, containingPackage.getName(), rootPackageName);
      } else {
        _routinesFacade.addCorrespondenceAndUpdateRepository(pcmComponentCandidate, javaPackage, pcmRepository);
      }
    }
  }
  
  public CreateOrFindArchitecturalElementInPackageRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.containers.Package containingPackage, final String rootPackageName) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateOrFindArchitecturalElementInPackageRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;this.containingPackage = containingPackage;this.rootPackageName = rootPackageName;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private org.emftext.language.java.containers.Package containingPackage;
  
  private String rootPackageName;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindArchitecturalElementInPackageRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   containingPackage: " + this.containingPackage);
    getLogger().debug("   rootPackageName: " + this.rootPackageName);
    
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(javaPackage, containingPackage, rootPackageName), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    userExecution.callRoutine1(javaPackage, containingPackage, rootPackageName, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
