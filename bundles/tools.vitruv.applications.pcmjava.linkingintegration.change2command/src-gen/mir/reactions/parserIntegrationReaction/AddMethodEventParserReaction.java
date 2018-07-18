package mir.reactions.parserIntegrationReaction;

import mir.routines.parserIntegrationReaction.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.eobject.CreateEObject;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;

@SuppressWarnings("all")
public class AddMethodEventParserReaction extends AbstractReactionRealization {
  private CreateEObject<EObject> createChange;
  
  private InsertEReference<ConcreteClassifier, Member> insertChange;
  
  private int currentlyMatchedChange;
  
  public AddMethodEventParserReaction(final RoutinesFacade routinesFacade) {
    super(routinesFacade);
  }
  
  public void executeReaction(final EChange change) {
    if (!checkPrecondition(change)) {
    	return;
    }
    org.emftext.language.java.classifiers.ConcreteClassifier affectedEObject = insertChange.getAffectedEObject();
    EReference affectedFeature = insertChange.getAffectedFeature();
    org.emftext.language.java.members.Member newValue = insertChange.getNewValue();
    int index = insertChange.getIndex();
    				
    getLogger().trace("Passed change matching of Reaction " + this.getClass().getName());
    if (!checkUserDefinedPrecondition(insertChange, affectedEObject, affectedFeature, newValue, index)) {
    	resetChanges();
    	return;
    }
    getLogger().trace("Passed complete precondition check of Reaction " + this.getClass().getName());
    				
    mir.reactions.parserIntegrationReaction.AddMethodEventParserReaction.ActionUserExecution userExecution = new mir.reactions.parserIntegrationReaction.AddMethodEventParserReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(insertChange, affectedEObject, affectedFeature, newValue, index, this.getRoutinesFacade());
    
    resetChanges();
  }
  
  private void resetChanges() {
    createChange = null;
    insertChange = null;
    currentlyMatchedChange = 0;
  }
  
  private boolean matchCreateChange(final EChange change) {
    if (change instanceof CreateEObject<?>) {
    	CreateEObject<org.eclipse.emf.ecore.EObject> _localTypedChange = (CreateEObject<org.eclipse.emf.ecore.EObject>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof org.eclipse.emf.ecore.EObject)) {
    		return false;
    	}
    	this.createChange = (CreateEObject<org.eclipse.emf.ecore.EObject>) change;
    	return true;
    }
    
    return false;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (currentlyMatchedChange == 0) {
    	if (!matchCreateChange(change)) {
    		resetChanges();
    		return false;
    	} else {
    		currentlyMatchedChange++;
    	}
    	return false; // Only proceed on the last of the expected changes
    }
    if (currentlyMatchedChange == 1) {
    	if (!matchInsertChange(change)) {
    		resetChanges();
    		checkPrecondition(change); // Reexecute to potentially register this as first change
    		return false;
    	} else {
    		currentlyMatchedChange++;
    	}
    }
    
    return true;
  }
  
  private boolean matchInsertChange(final EChange change) {
    if (change instanceof InsertEReference<?, ?>) {
    	InsertEReference<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member> _localTypedChange = (InsertEReference<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member>) change;
    	if (!(_localTypedChange.getAffectedEObject() instanceof org.emftext.language.java.classifiers.ConcreteClassifier)) {
    		return false;
    	}
    	if (!_localTypedChange.getAffectedFeature().getName().equals("members")) {
    		return false;
    	}
    	if (!(_localTypedChange.getNewValue() instanceof org.emftext.language.java.members.Member)) {
    		return false;
    	}
    	this.insertChange = (InsertEReference<org.emftext.language.java.classifiers.ConcreteClassifier, org.emftext.language.java.members.Member>) change;
    	return true;
    }
    
    return false;
  }
  
  private boolean checkUserDefinedPrecondition(final InsertEReference insertChange, final ConcreteClassifier affectedEObject, final EReference affectedFeature, final Member newValue, final int index) {
    return (newValue instanceof Method);
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final InsertEReference insertChange, final ConcreteClassifier affectedEObject, final EReference affectedFeature, final Member newValue, final int index, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.addedMethodEventParser(affectedEObject, ((Method) newValue));
    }
  }
}
