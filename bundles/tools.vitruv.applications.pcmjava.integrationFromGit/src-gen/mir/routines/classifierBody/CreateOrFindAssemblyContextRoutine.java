package mir.routines.classifierBody;

import java.io.IOException;
import mir.routines.classifierBody.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Field;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import tools.vitruv.applications.util.temporary.java.JavaTypeUtil;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateOrFindAssemblyContextRoutine extends AbstractRepairRoutineRealization {
  private CreateOrFindAssemblyContextRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSource1(final ConcreteClassifier classifier, final Field javaField, final ComposedProvidingRequiringEntity composedProvidingRequiringEntity, final RepositoryComponent repositoryComponent) {
      return javaField;
    }
    
    public EObject getCorrepondenceSourceComposedProvidingRequiringEntity(final ConcreteClassifier classifier, final Field javaField) {
      return classifier;
    }
    
    public EObject getCorrepondenceSourceRepositoryComponent(final ConcreteClassifier classifier, final Field javaField, final ComposedProvidingRequiringEntity composedProvidingRequiringEntity) {
      Classifier _classifierFromTypeReference = JavaTypeUtil.getClassifierFromTypeReference(javaField.getTypeReference());
      return _classifierFromTypeReference;
    }
    
    public void callRoutine1(final ConcreteClassifier classifier, final Field javaField, final ComposedProvidingRequiringEntity composedProvidingRequiringEntity, final RepositoryComponent repositoryComponent, @Extension final RoutinesFacade _routinesFacade) {
      final Function1<AssemblyContext, Boolean> _function = new Function1<AssemblyContext, Boolean>() {
        public Boolean apply(final AssemblyContext it) {
          String _entityName = it.getEntityName();
          String _name = javaField.getName();
          return Boolean.valueOf((_entityName == _name));
        }
      };
      final AssemblyContext pcmAssemblyContextCandidate = IterableExtensions.<AssemblyContext>findFirst(composedProvidingRequiringEntity.getAssemblyContexts__ComposedStructure(), _function);
      if ((pcmAssemblyContextCandidate == null)) {
        _routinesFacade.createAssemblyContext(classifier, javaField);
      } else {
        _routinesFacade.addAssemblyContextCorrespondence(pcmAssemblyContextCandidate, javaField);
      }
    }
  }
  
  public CreateOrFindAssemblyContextRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ConcreteClassifier classifier, final Field javaField) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.classifierBody.CreateOrFindAssemblyContextRoutine.ActionUserExecution(getExecutionState(), this);
    this.classifier = classifier;this.javaField = javaField;
  }
  
  private ConcreteClassifier classifier;
  
  private Field javaField;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateOrFindAssemblyContextRoutine with input:");
    getLogger().debug("   classifier: " + this.classifier);
    getLogger().debug("   javaField: " + this.javaField);
    
    org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity composedProvidingRequiringEntity = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceComposedProvidingRequiringEntity(classifier, javaField), // correspondence source supplier
    	org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity.class,
    	(org.palladiosimulator.pcm.core.entity.ComposedProvidingRequiringEntity _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (composedProvidingRequiringEntity == null) {
    	return false;
    }
    registerObjectUnderModification(composedProvidingRequiringEntity);
    org.palladiosimulator.pcm.repository.RepositoryComponent repositoryComponent = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceRepositoryComponent(classifier, javaField, composedProvidingRequiringEntity), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.RepositoryComponent.class,
    	(org.palladiosimulator.pcm.repository.RepositoryComponent _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (repositoryComponent == null) {
    	return false;
    }
    registerObjectUnderModification(repositoryComponent);
    if (!getCorrespondingElements(
    	userExecution.getCorrepondenceSource1(classifier, javaField, composedProvidingRequiringEntity, repositoryComponent), // correspondence source supplier
    	org.palladiosimulator.pcm.core.composition.AssemblyContext.class,
    	(org.palladiosimulator.pcm.core.composition.AssemblyContext _element) -> true, // correspondence precondition checker
    	null
    ).isEmpty()) {
    	return false;
    }
    userExecution.callRoutine1(classifier, javaField, composedProvidingRequiringEntity, repositoryComponent, this.getRoutinesFacade());
    
    postprocessElements();
    
    return true;
  }
}
