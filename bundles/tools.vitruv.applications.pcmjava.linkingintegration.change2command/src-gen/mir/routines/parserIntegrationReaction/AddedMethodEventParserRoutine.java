package mir.routines.parserIntegrationReaction;

import java.io.IOException;
import mir.routines.parserIntegrationReaction.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;

@SuppressWarnings("all")
public class AddedMethodEventParserRoutine extends AbstractRepairRoutineRealization {
  private AddedMethodEventParserRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getCorrepondenceSourceOpInterface(final ConcreteClassifier clazz, final Method method) {
      return clazz;
    }
    
    public void updateOpSigElement(final ConcreteClassifier clazz, final Method method, final OperationInterface opInterface, final OperationSignature opSig) {
      opSig.setEntityName(method.getName());
      opSig.setInterface__OperationSignature(opInterface);
      CorrespondenceModelUtil.createAndAddCorrespondence(this.correspondenceModel, opSig, method);
    }
  }
  
  public AddedMethodEventParserRoutine(final RoutinesFacade routinesFacade, final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final ConcreteClassifier clazz, final Method method) {
    super(routinesFacade, reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.parserIntegrationReaction.AddedMethodEventParserRoutine.ActionUserExecution(getExecutionState(), this);
    this.clazz = clazz;this.method = method;
  }
  
  private ConcreteClassifier clazz;
  
  private Method method;
  
  protected boolean executeRoutine() throws IOException {
    getLogger().debug("Called routine AddedMethodEventParserRoutine with input:");
    getLogger().debug("   clazz: " + this.clazz);
    getLogger().debug("   method: " + this.method);
    
    org.palladiosimulator.pcm.repository.OperationInterface opInterface = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceOpInterface(clazz, method), // correspondence source supplier
    	org.palladiosimulator.pcm.repository.OperationInterface.class,
    	(org.palladiosimulator.pcm.repository.OperationInterface _element) -> true, // correspondence precondition checker
    	null, 
    	false // asserted
    	);
    if (opInterface == null) {
    	return false;
    }
    registerObjectUnderModification(opInterface);
    org.palladiosimulator.pcm.repository.OperationSignature opSig = org.palladiosimulator.pcm.repository.impl.RepositoryFactoryImpl.eINSTANCE.createOperationSignature();
    notifyObjectCreated(opSig);
    userExecution.updateOpSigElement(clazz, method, opInterface, opSig);
    
    postprocessElements();
    
    return true;
  }
}
