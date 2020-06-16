package mir.routines.packageMappingIntegrationExtended;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.io.IOException;
import mir.routines.packageMappingIntegrationExtended.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersPackage;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindCompositeDataTypeRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindCompositeDataTypeRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourcePcmRepository(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
      return ContainersPackage.Literals.PACKAGE;
    }
    
    public void callRoutine1(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final Repository pcmRepository, @Extension final RoutinesFacade _routinesFacade) {
      final Function1<CompositeDataType, Boolean> _function = new Function1<CompositeDataType, Boolean>() {
        public Boolean apply(final CompositeDataType it) {
          return Boolean.valueOf((Objects.equal(StringExtensions.toFirstUpper(it.getEntityName()), StringExtensions.toFirstUpper(javaClass.getName())) || ((javaClass.getName() == null) && Objects.equal(it.getEntityName(), "aName"))));
        }
      };
      final CompositeDataType foundCompositeDataType = IterableUtil.<Iterable<CompositeDataType>, CompositeDataType>claimNotMany(IterableExtensions.<CompositeDataType>filter(Iterables.<CompositeDataType>filter(pcmRepository.getDataTypes__Repository(), CompositeDataType.class), _function));
      if ((foundCompositeDataType == null)) {
        _routinesFacade.createCompositeDataType(javaClass, compilationUnit);
      } else {
        _routinesFacade.addDataTypeCorrespondence(javaClass, compilationUnit, foundCompositeDataType);
      }
    }
  }
  
  public CreateOrFindCompositeDataTypeRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageMappingIntegrationExtended.CreateOrFindCompositeDataTypeRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaClass = javaClass;this.compilationUnit = compilationUnit;
  }
  
  private org.emftext.language.java.classifiers.Class javaClass;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindCompositeDataTypeRoutine with input:");
    getLogger().debug("   javaClass: " + this.javaClass);
    getLogger().debug("   compilationUnit: " + this.compilationUnit);
    
    org.palladiosimulator.pcm.repository.Repository pcmRepository = getCorrespondingElement(
    	userExecution.getCorrepondenceSourcePcmRepository(javaClass, compilationUnit), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.Repository.class,
    	(org.palladiosimulator.pcm.repository.Repository _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (pcmRepository == null) {
    	return false;
    }
    registerObjectUnderModification(pcmRepository);
    userExecution.callRoutine1(javaClass, compilationUnit, pcmRepository, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
