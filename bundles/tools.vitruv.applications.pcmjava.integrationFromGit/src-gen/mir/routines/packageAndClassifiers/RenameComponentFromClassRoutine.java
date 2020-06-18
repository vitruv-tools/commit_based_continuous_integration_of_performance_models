package mir.routines.packageAndClassifiers;

import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class RenameComponentFromClassRoutine extends AbstractRepairRoutineRealization {
  private RenameComponentFromClassRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final org.emftext.language.java.classifiers.Class javaClass, final RepositoryComponent pcmComponent) {
      return pcmComponent;
    }
    
    public void update0Element(final org.emftext.language.java.classifiers.Class javaClass, final RepositoryComponent pcmComponent) {
      String newName = StringExtensions.toFirstUpper(javaClass.getName());
      boolean _endsWith = newName.endsWith("Impl");
      if (_endsWith) {
        int _length = newName.length();
        int _length_1 = "Impl".length();
        int _minus = (_length - _length_1);
        newName = newName.substring(0, _minus);
      }
      pcmComponent.setEntityName(newName);
    }
    
    public EObject getCorrepondenceSourcePcmComponent(final org.emftext.language.java.classifiers.Class javaClass) {
      return javaClass;
    }
  }
  
  public RenameComponentFromClassRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.classifiers.Class javaClass) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.RenameComponentFromClassRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaClass = javaClass;
  }
  
  private org.emftext.language.java.classifiers.Class javaClass;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine RenameComponentFromClassRoutine with input:");
    getLogger().debug("   javaClass: " + this.javaClass);
    
    org.palladiosimulator.pcm.repository.RepositoryComponent pcmComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourcePcmComponent(javaClass), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (pcmComponent == null) {
    	return false;
    }
    registerObjectUnderModification(pcmComponent);
    // val updatedElement userExecution.getElement1(javaClass, pcmComponent);
    userExecution.update0Element(javaClass, pcmComponent);
    
    postprocessElements();
    
    return true;
  }
}
