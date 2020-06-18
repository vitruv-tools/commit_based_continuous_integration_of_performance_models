package mir.routines.packageAndClassifiers;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil;
import java.io.IOException;
import mir.routines.packageAndClassifiers.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersPackage;
import org.palladiosimulator.pcm.repository.CollectionDataType;
import org.palladiosimulator.pcm.repository.Repository;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindCollectionDataTypeRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindCollectionDataTypeRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourcePcmRepository(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
      return ContainersPackage.Literals.PACKAGE;
    }
    
    public void callRoutine1(final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit, final Repository pcmRepository, @Extension final RoutinesFacade _routinesFacade) {
      final Function1<CollectionDataType, Boolean> _function = new Function1<CollectionDataType, Boolean>() {
        public Boolean apply(final CollectionDataType it) {
          return Boolean.valueOf((Objects.equal(StringExtensions.toFirstUpper(it.getEntityName()), StringExtensions.toFirstUpper(javaClass.getName())) || ((javaClass.getName() == null) && Objects.equal(it.getEntityName(), "aName"))));
        }
      };
      final CollectionDataType foundCollectionDataType = IterableUtil.<Iterable<CollectionDataType>, CollectionDataType>claimNotMany(IterableExtensions.<CollectionDataType>filter(Iterables.<CollectionDataType>filter(pcmRepository.getDataTypes__Repository(), CollectionDataType.class), _function));
      if ((foundCollectionDataType == null)) {
        _routinesFacade.createCollectionDataType(javaClass, compilationUnit);
      } else {
        _routinesFacade.addDataTypeCorrespondence(javaClass, compilationUnit, foundCollectionDataType);
      }
    }
  }
  
  public CreateOrFindCollectionDataTypeRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final org.emftext.language.java.classifiers.Class javaClass, final CompilationUnit compilationUnit) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.packageAndClassifiers.CreateOrFindCollectionDataTypeRoutine.ActionUserExecution(getExecutionState(), this);
    this.javaClass = javaClass;this.compilationUnit = compilationUnit;
  }
  
  private org.emftext.language.java.classifiers.Class javaClass;
  
  private CompilationUnit compilationUnit;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindCollectionDataTypeRoutine with input:");
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
