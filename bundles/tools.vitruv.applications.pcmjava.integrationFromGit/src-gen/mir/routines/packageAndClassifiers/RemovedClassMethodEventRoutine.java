package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.ClassMethod;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RemovedClassMethodEventRoutine extends AbstractRepairRoutineRealization {
  private RemovedClassMethodEventRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final ClassMethod classMethod, final ResourceDemandingSEFF seff) {
      return seff;
    }
    
    public EObject getCorrepondenceSourceSeff(final ClassMethod classMethod) {
      return classMethod;
    }
  }
  
  public RemovedClassMethodEventRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ClassMethod classMethod) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RemovedClassMethodEventRoutine.ActionUserExecution(getExecutionState(), this);
    this.classMethod = classMethod;
  }
  
  private ClassMethod classMethod;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RemovedClassMethodEventRoutine with input:");
    getLogger().debug("   classMethod: " + this.classMethod);
    
    org.palladiosimulator.pcm.seff.ResourceDemandingSEFF seff = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceSeff(classMethod), // correspondence source supplier
    	org.palladiosimulator.pcm.seff.ResourceDemandingSEFF.class,
    	(org.palladiosimulator.pcm.seff.ResourceDemandingSEFF _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (seff == null) {
    	return false;
    }
    registerObjectUnderModification(seff);
    deleteObject(userExecution.getElement1(classMethod, seff));
    
    postprocessElements();
    
    return true;
  }
}
