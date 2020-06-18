package mir.routines.packageAndClassifiers;

import com.google.common.base.Objects;
import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class EnsureFirstCaseUpperCaseRepositoryNamingRoutine extends AbstractRepairRoutineRealization {
  private EnsureFirstCaseUpperCaseRepositoryNamingRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final Repository pcmRepository, final org.emftext.language.java.containers.Package javaPackage) {
      return pcmRepository;
    }
    
    public void update0Element(final Repository pcmRepository, final org.emftext.language.java.containers.Package javaPackage) {
      pcmRepository.setEntityName(StringExtensions.toFirstUpper(javaPackage.getName()));
    }
    
    public boolean checkMatcherPrecondition1(final Repository pcmRepository, final org.emftext.language.java.containers.Package javaPackage) {
      String _entityName = pcmRepository.getEntityName();
      String _name = javaPackage.getName();
      boolean _equals = Objects.equal(_entityName, _name);
      return _equals;
    }
  }
  
  public EnsureFirstCaseUpperCaseRepositoryNamingRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Repository pcmRepository, final org.emftext.language.java.containers.Package javaPackage) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.EnsureFirstCaseUpperCaseRepositoryNamingRoutine.ActionUserExecution(getExecutionState(), this);
    this.pcmRepository = pcmRepository;this.javaPackage = javaPackage;
  }
  
  private Repository pcmRepository;
  
  private org.emftext.language.java.containers.Package javaPackage;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine EnsureFirstCaseUpperCaseRepositoryNamingRoutine with input:");
    getLogger().debug("   pcmRepository: " + this.pcmRepository);
    getLogger().debug("   javaPackage: " + this.javaPackage);
    
    if (!userExecution.checkMatcherPrecondition1(pcmRepository, javaPackage)) {
    	return false;
    }
    // val updatedElement userExecution.getElement1(pcmRepository, javaPackage);
    userExecution.update0Element(pcmRepository, javaPackage);
    
    postprocessElements();
    
    return true;
  }
}
