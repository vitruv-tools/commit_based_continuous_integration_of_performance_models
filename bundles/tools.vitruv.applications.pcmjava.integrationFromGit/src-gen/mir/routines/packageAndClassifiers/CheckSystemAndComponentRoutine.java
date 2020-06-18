package mir.routines.packageAndClassifiers;

import com.google.common.base.Objects;
import java.io.IOException;
import java.util.Optional;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.CompilationUnit;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingRequiringEntity;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

/**
 * *
 * nCheck if package has a correspondence with a component or system. 
 * nIf there is one create correspondence between component or system and the given class, as well as the compilation unit if not already exists
 *  
 */
@SuppressWarnings("all")
public class CheckSystemAndComponentRoutine extends AbstractRepairRoutineRealization {
  private CheckSystemAndComponentRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourceComponentOrSystem(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass) {
      return javaPackage;
    }
    
    public EObject getElement1(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass, final InterfaceProvidingRequiringEntity componentOrSystem, final Optional<RepositoryComponent> correspondenceToCompilationUnit) {
      return componentOrSystem;
    }
    
    public EObject getElement2(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass, final InterfaceProvidingRequiringEntity componentOrSystem, final Optional<RepositoryComponent> correspondenceToCompilationUnit) {
      return javaClass;
    }
    
    public EObject getCorrepondenceSourceCorrespondenceToCompilationUnit(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass, final InterfaceProvidingRequiringEntity componentOrSystem) {
      CompilationUnit _containingCompilationUnit = javaClass.getContainingCompilationUnit();
      return _containingCompilationUnit;
    }
    
    public boolean getCorrespondingModelElementsPreconditionCorrespondenceToCompilationUnit(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass, final InterfaceProvidingRequiringEntity componentOrSystem, final RepositoryComponent correspondenceToCompilationUnit) {
      String _entityName = correspondenceToCompilationUnit.getEntityName();
      String _entityName_1 = componentOrSystem.getEntityName();
      boolean _equals = Objects.equal(_entityName, _entityName_1);
      return _equals;
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass, final InterfaceProvidingRequiringEntity componentOrSystem, final Optional<RepositoryComponent> correspondenceToCompilationUnit, @Extension final RoutinesFacade _routinesFacade) {
      boolean _isPresent = correspondenceToCompilationUnit.isPresent();
      boolean _not = (!_isPresent);
      if (_not) {
        _routinesFacade.addCorrespondenceBetweenPcmComponentOrSystemAndJavaCompilationUnit(componentOrSystem, javaClass.getContainingCompilationUnit());
      }
    }
  }
  
  public CheckSystemAndComponentRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final org.emftext.language.java.classifiers.Class javaClass) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CheckSystemAndComponentRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;this.javaClass = javaClass;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private org.emftext.language.java.classifiers.Class javaClass;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CheckSystemAndComponentRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   javaClass: " + this.javaClass);
    
    org.palladiosimulator.pcm.core.entity.InterfaceProvidingRequiringEntity componentOrSystem = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceComponentOrSystem(javaPackage, javaClass), // correspondence source supplier
    	org.palladiosimulator.pcm.core.entity.InterfaceProvidingRequiringEntity.class,
    	(org.palladiosimulator.pcm.core.entity.InterfaceProvidingRequiringEntity _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (componentOrSystem == null) {
    	return false;
    }
    registerObjectUnderModification(componentOrSystem);
    	Optional<org.palladiosimulator.pcm.repository.RepositoryComponent> correspondenceToCompilationUnit = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceCorrespondenceToCompilationUnit(javaPackage, javaClass, componentOrSystem), // correspondence source supplier
    		org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    		(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> userExecution.getCorrespondingModelElementsPreconditionCorrespondenceToCompilationUnit(javaPackage, javaClass, componentOrSystem, _element), // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(correspondenceToCompilationUnit.isPresent() ? correspondenceToCompilationUnit.get() : null);
    addCorrespondenceBetween(userExecution.getElement1(javaPackage, javaClass, componentOrSystem, correspondenceToCompilationUnit), userExecution.getElement2(javaPackage, javaClass, componentOrSystem, correspondenceToCompilationUnit), "");
    
    userExecution.callRoutine1(javaPackage, javaClass, componentOrSystem, correspondenceToCompilationUnit, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
