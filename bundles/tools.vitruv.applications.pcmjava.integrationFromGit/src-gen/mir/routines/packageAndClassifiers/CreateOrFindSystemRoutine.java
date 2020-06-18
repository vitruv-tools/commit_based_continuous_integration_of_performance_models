package mir.routines.packageAndClassifiers;

import com.google.common.base.Objects;
import java.io.IOException;
import java.util.Optional;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.palladiosimulator.pcm.system.SystemPackage;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindSystemRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindSystemRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public boolean getCorrespondingModelElementsPreconditionFoundSystem(final org.emftext.language.java.containers.Package javaPackage, final String name, final org.palladiosimulator.pcm.system.System foundSystem) {
      String _firstLower = StringExtensions.toFirstLower(foundSystem.getEntityName());
      String _name = javaPackage.getName();
      boolean _equals = Objects.equal(_firstLower, _name);
      return _equals;
    }
    
    public EObject getCorrepondenceSource1(final org.emftext.language.java.containers.Package javaPackage, final String name) {
      return javaPackage;
    }
    
    public EObject getCorrepondenceSourceFoundSystem(final org.emftext.language.java.containers.Package javaPackage, final String name) {
      return SystemPackage.Literals.SYSTEM;
    }
    
    public void callRoutine1(final org.emftext.language.java.containers.Package javaPackage, final String name, final Optional<org.palladiosimulator.pcm.system.System> foundSystem, @Extension final RoutinesFacade _routinesFacade) {
      boolean _isPresent = foundSystem.isPresent();
      if (_isPresent) {
        _routinesFacade.addSystemCorrespondence(foundSystem.get(), javaPackage);
      } else {
        _routinesFacade.createSystem(javaPackage, name);
      }
    }
  }
  
  public CreateOrFindSystemRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.containers.Package javaPackage, final String name) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateOrFindSystemRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaPackage = javaPackage;this.name = name;
  }
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  private String name;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindSystemRoutine with input:");
    getLogger().debug("   javaPackage: " + this.javaPackage);
    getLogger().debug("   name: " + this.name);
    
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(javaPackage, name), // correspondence source supplier
    	org.palladiosimulator.pcm.system.System.class,
    	(org.palladiosimulator.pcm.system.System _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    	Optional<org.palladiosimulator.pcm.system.System> foundSystem = Optional.ofNullable(getCorrespondingElement(
    		userExecution.getCorrepondenceSourceFoundSystem(javaPackage, name), // correspondence source supplier
    		org.palladiosimulator.pcm.system.System.class,
    		(org.palladiosimulator.pcm.system.System _element) -> userExecution.getCorrespondingModelElementsPreconditionFoundSystem(javaPackage, name, _element), // correspondence precondition checker
    		null, 
    		false // asserted
    		)
    );
    registerObjectUnderModification(foundSystem.isPresent() ? foundSystem.get() : null);
    userExecution.callRoutine1(javaPackage, name, foundSystem, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
