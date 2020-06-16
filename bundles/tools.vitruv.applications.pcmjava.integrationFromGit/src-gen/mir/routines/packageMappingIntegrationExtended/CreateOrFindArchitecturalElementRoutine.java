package mir.routines.packageMappingIntegrationExtended;

import com.google.common.base.Objects;
import java.io.IOException;
import java.util.Optional;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.ContainersPackage;
import tools.vitruv.applications.util.temporary.java.JavaContainerAndClassifierUtil;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindArchitecturalElementRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindArchitecturalElementRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public boolean getCorrespondingModelElementsPreconditionContainerPackage(final org.emftext.language.java.containers.Package javaPackage, final String containerName, final org.emftext.language.java.containers.Package containerPackage) {
      String _name = containerPackage.getName();
      boolean _equals = Objects.equal(_name, containerName);
      return _equals;
    }
    
    public EObject getCorrepondenceSourceContainerPackage(final org.emftext.language.java.containers.Package javaPackage, final String containerName) {
      return ContainersPackage.Literals.PACKAGE;
    }
    
    public EObject getCorrepondenceSource1(final org.emftext.language.java.containers.Package javaPackage, final String containerName, final Optional<org.emftext.language.java.containers.Package> containerPackage) {
      return javaPackage;
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final String containerName, final Optional<org.emftext.language.java.containers.Package> containerPackage, @Extension final RoutinesFacade _routinesFacade) {
      final String rootPackageName = JavaContainerAndClassifierUtil.getRootPackageName(javaPackage.getName());
      boolean _isPresent = containerPackage.isPresent();
      if (_isPresent) {
        _routinesFacade.createOrFindArchitecturalElementInPackage(javaPackage, containerPackage.get(), rootPackageName);
      } else {
        _routinesFacade.createArchitecturalElement(javaPackage, containerName, rootPackageName);
      }
    }
  }
  
  public CreateOrFindArchitecturalElementRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final String containerName) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.CreateOrFindArchitecturalElementRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;this.containerName = containerName;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private String containerName;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindArchitecturalElementRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   containerName: " + this.containerName);
    
    	Optional<org.emftext.language.java.containers.Package> containerPackage = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceContainerPackage(javaPackage, containerName), // correspondence source supplier
    		org.emftext.language.java.containers.Package.class,
    		(org.emftext.language.java.containers.Package _element) -> userExecution.getCorrespondingModelElementsPreconditionContainerPackage(javaPackage, containerName, _element), // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(containerPackage.isPresent() ? containerPackage.get() : null);
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(javaPackage, containerName, containerPackage), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    userExecution.callRoutine1(javaPackage, containerName, containerPackage, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
