package tools.vitruv.applications.pcmjava.seffstatements.code2seff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.annotationsmox.inspectit2pcm.util.PCMHelper;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.gast2seff.visitors.VisitorUtils;
import org.somox.sourcecodedecorator.SeffElementSourceCodeLink;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

import com.google.common.collect.Lists;

import de.uka.ipd.sdq.identifier.Identifier;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.ExtendedClassMethodBodyChangedTransformation;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.framework.userinteraction.UserInteractor;

/**
 * @author langhamm
 */
public class FineGranularClassMethodBodyChangedTransformation extends ExtendedClassMethodBodyChangedTransformation {
	private final static Logger logger = Logger.getLogger(FineGranularClassMethodBodyChangedTransformation.class.getSimpleName());
	private final Method newMethod;
	private final BasicComponentFinding basicComponentFinder;
	private ResourceDemandingBehaviourDiff rdbDifference;
	private SourceCodeDecoratorRepository sourceCodeDecorator;
	private final SimilarityChecker similarityChecker;
	
	public FineGranularClassMethodBodyChangedTransformation(final Method oldMethod, final Method newMethod,
			final BasicComponentFinding basicComponentFinder,
			final IFunctionClassificationStrategy iFunctionClassificationStrategy,
			final InterfaceOfExternalCallFindingFactory InterfaceOfExternalCallFindingFactory,
			final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		super(newMethod, basicComponentFinder, iFunctionClassificationStrategy, InterfaceOfExternalCallFindingFactory,
				resourceDemandingBehaviourForClassMethodFinding);
		this.newMethod = newMethod;
		this.basicComponentFinder = basicComponentFinder;
		this.similarityChecker = new SimilarityChecker();
	}

	/**
	 * This method is called after a java method body has been changed. In order
	 * to keep the SEFF/ResourceDemandingInternalBehaviour consistent with the
	 * method we 1) remove all AbstractActions corresponding to the Method from
	 * the SEFF/ResourceDemandingInternalBehaviour (which are currently all
	 * AbstractActions in the SEFF/ResourceDemandingInternalBehaviour) and from
	 * the correspondenceModel 2) run the SoMoX SEFF extractor for this method,
	 * 3) reconnect the newly extracted SEFF elements with the old elements 4)
	 * create new AbstractAction 2 Method correspondences for the new method
	 * (and its inner methods)
	 * 5) link the abstract actions with the corresponding statements, this will be needed 
	 * in the instrumentation process
	 */
	public void execute(final CorrespondenceModel correspondenceModel,
			final UserInteractor userInteracting) {
		if (!this.isArchitectureRelevantChange(correspondenceModel)) {			
			logger.debug("Change within the method " + this.newMethod
					+ " is not an architecture-relevant change.");
			return;
		}

		// 1) Get old ResourceDemandingBehaviour.
		final ResourceDemandingBehaviour oldSEFF = this.findRdBehaviorToInsertElements(correspondenceModel);
		
		// 2) Create the new ResourceDemandingBehaviour.
		final ResourceDemandingBehaviour newSEFF = this.createNewResourceDemandingBehaviour(correspondenceModel);
		
		// 3) Calculate the difference between the old and new SEFF.
		this.calculateResourceDemandingBehaviourDiff(oldSEFF, newSEFF);
		
		// 6) update old resource Demanding Behaviour
		this.updateOldResourceDemandingBehaviour(oldSEFF, newSEFF);
		
		// 6) create new correspondences
		this.createNewCorrespondences(correspondenceModel, oldSEFF);
		
		// 7) create correspondence between Seff elements and statements
		this.bindAbstractActionsAndStatements(correspondenceModel);
		
		return;
	}
	
	private void updateOldResourceDemandingBehaviour(
			ResourceDemandingBehaviour rdBehavior, ResourceDemandingBehaviour newSeff) {
		final List<AbstractAction> steps = rdBehavior.getSteps_Behaviour();
		final boolean addStartAction = 0 == steps.size() || !(steps.get(0) instanceof StartAction);
		final boolean addStopAction = 0 == steps.size() || !(steps.get(steps.size() - 1) instanceof StopAction);

		if (addStartAction) {
			rdBehavior.getSteps_Behaviour().add(0, SeffFactory.eINSTANCE.createStartAction());
		}
		if (addStopAction) {
			final AbstractAction stopAction = SeffFactory.eINSTANCE.createStopAction();
			rdBehavior.getSteps_Behaviour().add(stopAction);
		}
		
		this.addNewAbstractActions(steps, newSeff);
		
		VisitorUtils.connectActions(rdBehavior);
	}
	
	
	private void addNewAbstractActions(
			List<AbstractAction> steps, ResourceDemandingBehaviour newSeff) {
		
		List<AbstractAction> newSeffAbstractActions = new ArrayList<AbstractAction>();
		for(AbstractAction aa: newSeff.getSteps_Behaviour()) {
			newSeffAbstractActions.add(aa);
		}
		
		AbstractAction newAbstractActionPredecessor = null;
		for(AbstractAction newAbstractAction: Lists.reverse(newSeffAbstractActions)) {
			AbstractAction matching = null; // this.getNewAbstractActionMatching(newAbstractAction);
			
			if(matching == null) {
				if(newAbstractActionPredecessor == null) {
					// case 1: the current element in the new SEFF has no Matching and has no Predecessor
					// ==> add it in the first position in the old SEFF
					int oldSeffFirstPositionLocation = steps.size() - 1; 
					steps.add(oldSeffFirstPositionLocation, newAbstractAction);
					
				}
				else {
					AbstractAction precessorMatching = null; // this.getNewAbstractActionMatching(newAbstractActionPredecessor);
					if(precessorMatching != null) {
						// case 2: the Predecessor of the current element in the new SEFF has a matching in the old SEFF
						// ==> use this matching in as a Predecessor of the current element in the old SEFF
						int predecessorMatchingLocation = steps.indexOf(precessorMatching);
						steps.add(predecessorMatchingLocation, newAbstractAction);	
					}
					else {
						// case 3: the Predecessor of the current element has no matching in the old SEFF
						// ==> use this Predecessor as a Predecessor in the old SEFF, because this Predecessor
						// must had been added in the old SEFF in the previous iteration in this loop
						int predecessorOldSeffLocation = steps.indexOf(newAbstractActionPredecessor);
						steps.add(predecessorOldSeffLocation, newAbstractAction);
					}
				}
				
			}
			
			newAbstractActionPredecessor = newAbstractAction;
		}
	}

	private ResourceDemandingBehaviour createNewResourceDemandingBehaviour(final CorrespondenceModel correspondenceModel) {
		ResourceDemandingBehaviour newSEFF = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
		final BasicComponent basicComponent = this.basicComponentFinder.findBasicComponentForMethod(this.newMethod,
				correspondenceModel);

		this.executeSoMoXForMethod(basicComponent, newSEFF);

		return newSEFF;
	}
	
	private void calculateResourceDemandingBehaviourDiff(ResourceDemandingBehaviour oldSEFF,
			ResourceDemandingBehaviour newSEFF) {
		rdbDifference = new ResourceDemandingBehaviourDiff();
		
		List<AbstractAction> listOldAbstractActions = this.getRelevantAbstractActions(oldSEFF);
		List<AbstractAction> listNewAbstractActions = this.getRelevantAbstractActions(newSEFF);
		
		// If the old SEFF is empty, all new AbstractActions are added.
		if (listOldAbstractActions.size() == 0) {
			rdbDifference.getAddedAbstractActions().addAll(listNewAbstractActions);
			return;
		}
		
		// Find modified AbstractActions.
		this.matchNewAndOldSeff(oldSEFF, newSEFF, null);
		
		// Find deleted AbstractActions.
		for (AbstractAction oldAbstractAction : listOldAbstractActions) {
			if (this.hasOldAbstractActionMatching(oldAbstractAction)) {
				rdbDifference.getDeletedAbstractActions().add(oldAbstractAction);
			}
		}
		
		// Find added AbstractActions.
		for (AbstractAction newAbstractAction : listNewAbstractActions) {
			if (!this.hasNewAbstractActionMatching(newAbstractAction)) {
				rdbDifference.getAddedAbstractActions().add(newAbstractAction);
			}
		}
	}
	
	private List<AbstractAction> getRelevantAbstractActions(ResourceDemandingBehaviour seff) {
		List<AbstractAction> listAbstractActions = new ArrayList<>();
		for (AbstractAction aa : seff.getSteps_Behaviour()) {
			if (!(aa instanceof StartAction || aa instanceof StopAction)) {
				listAbstractActions.add(aa);
			}
		}
		return listAbstractActions;
	}
	
	private void matchNewAndOldSeff(ResourceDemandingBehaviour oldSEFF,
			ResourceDemandingBehaviour newSEFF, CorrespondenceModel ci) {

		List<AbstractAction> oldAbstractActions = this.getRelevantAbstractActions(oldSEFF);
		Map<AbstractAction, List<Statement>> newSeffStatements = this.getNewSeffElementStatements(newSEFF);
				
		for (Map.Entry<AbstractAction, List<Statement>> entry : newSeffStatements.entrySet()) {
			AbstractAction newAbstractAction = entry.getKey();
			List<Statement> newAbstractActionStatements = entry.getValue();
			
			for (AbstractAction oldAbstractAction : oldAbstractActions) {
				// Get corresponding statements for old AbstractAction. 
				Set<Statement> oldAbstractActionStatements = CorrespondenceModelUtil
						.getCorrespondingEObjects(ci, oldAbstractAction, Statement.class);
				
				int similarStatementsCount = this.compareAbstractActions(newAbstractAction, oldAbstractAction, 
						newAbstractActionStatements, oldAbstractActionStatements);
				
				if (newAbstractActionStatements.size() == similarStatementsCount) {
					rdbDifference.getUnmodifiedAbstractActions().add(
							new AbstractActionMatching(newAbstractAction, oldAbstractAction));
					break;
				} else if (similarStatementsCount != 0) {
					rdbDifference.getModifiedAbstractActions().add(
							new AbstractActionMatching(newAbstractAction, oldAbstractAction));
					break;
				}
			}
		}
	}
	
	private Map<AbstractAction, List<Statement>> getNewSeffElementStatements(ResourceDemandingBehaviour newSEFF) {
	    Map<AbstractAction, List<Statement>> newSeffStatements = new HashMap<>();
		List<SeffElementSourceCodeLink> seffStatementLinks = sourceCodeDecorator.getSeffElementsSourceCodeLinks();
		for (SeffElementSourceCodeLink seffStatementLink : seffStatementLinks) {
			List<Statement> listStatement = seffStatementLink.getStatement();
			newSeffStatements.put((AbstractAction) seffStatementLink.getSeffElement(), listStatement);
		}
		return newSeffStatements;
	}
	
	private int compareAbstractActions(AbstractAction newAbstractAction, AbstractAction oldAbstractAction,
			 List<Statement> newAbstractActionStatements, Set<Statement> oldAbstractActionStatements) {
		if (oldAbstractAction.getClass() != newAbstractAction.getClass()) {
			return 0;
		} else {
			// Compare statements.
		    return this.compareStatements(newAbstractActionStatements,
		    		oldAbstractActionStatements);
		}
	}
	
	/**
	 * Returns the number of similar statement.
	 */
	private int compareStatements(List<Statement> newStatements, Set<Statement> oldStatements) {
		int similarStatementsCount = 0;
		for (Statement newStatement : newStatements) {
			for (Statement oldStatement : oldStatements) {
				if (this.similarityChecker.isSimilar(newStatement, oldStatement)) {
					similarStatementsCount++;
				}
			}
		}
		return similarStatementsCount;
	}
	
	private boolean hasOldAbstractActionMatching(AbstractAction oldAbstractAction) {
		for (AbstractActionMatching matching : rdbDifference.getModifiedAbstractActions()) {
			if (oldAbstractAction == matching.getOldAbstractAction()) {
				return true;
			}
		}
		for (AbstractActionMatching matching : rdbDifference.getUnmodifiedAbstractActions()) {
			if (oldAbstractAction == matching.getOldAbstractAction()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasNewAbstractActionMatching(AbstractAction newAbstractAction) {
		for (AbstractActionMatching matching : rdbDifference.getModifiedAbstractActions()) {
			if (matching.getNewAbstractAction() == newAbstractAction) {
				return true;
			}
		}
		for (AbstractActionMatching matching : rdbDifference.getUnmodifiedAbstractActions()) {
			if (matching.getNewAbstractAction() == newAbstractAction) {
				return true;
			}
		}
		return false;
	}
	
	private void bindAbstractActionsAndStatements(
			CorrespondenceModel correspondenceModel) {
		List<SeffElementSourceCodeLink> seffElementSourceCodeLinks =  sourceCodeDecorator.getSeffElementsSourceCodeLinks();
        for(SeffElementSourceCodeLink seffElementSourceCodeLink: seffElementSourceCodeLinks) {
        	List<Statement> listStatements = seffElementSourceCodeLink.getStatement();
        	Identifier seffElement = seffElementSourceCodeLink.getSeffElement();
        	
        	if(seffElement instanceof ExternalCallAction) {
        		this.createAbstractActionStatementsCorrespondence((AbstractAction) seffElement, listStatements,
        				correspondenceModel);
        	}
        	else if(seffElement instanceof InternalAction || seffElement instanceof LoopAction) {
        		if(rdbDifference.getAddedAbstractActions().contains(seffElement)) {
        			this.createAbstractActionStatementsCorrespondence((AbstractAction) seffElement, 
        					listStatements, correspondenceModel);
        		}
        	}
        	else if(seffElement instanceof ResourceDemandingBehaviour) {
        		ResourceDemandingBehaviour seff = (ResourceDemandingBehaviour) seffElement;
            	AbstractBranchTransition abstractBranchTr = seff.getAbstractBranchTransition_ResourceDemandingBehaviour();
            	
            	BranchAction branchAction = abstractBranchTr.getBranchAction_AbstractBranchTransition();
            	
            	if(rdbDifference.getAddedAbstractActions().contains(branchAction)) {
            		this.createAbstractActionStatementsCorrespondence(branchAction, listStatements,
            				correspondenceModel);
            	}
        	}
        }

	}
	
	
	private void createAbstractActionStatementsCorrespondence(AbstractAction abstractAction, 
			List<Statement> listStatements, CorrespondenceModel correspondenceModel) {
		correspondenceModel.createAndAddCorrespondence(List.of(abstractAction), List.of(this.newMethod));
    	//

        for(Statement statement: listStatements) {
            correspondenceModel.createAndAddCorrespondence(List.of(abstractAction), List.of(statement));
        }
        
        // add ParametricResourceDemand if the abstract action is an internal action
        if(abstractAction instanceof InternalAction) {
        	InternalAction internalAction = (InternalAction) abstractAction;
        	internalAction.getResourceDemand_Action().add(
            		PCMHelper.createParametricResourceDemandCPU(PCMHelper.createPCMRandomVariable(0)));
        }
        
	}

	private void createNewCorrespondences(final CorrespondenceModel ci,
			final ResourceDemandingBehaviour newResourceDemandingBehaviourElements) {
		for (final AbstractAction abstractAction : newResourceDemandingBehaviourElements.getSteps_Behaviour()) {
			ci.createAndAddCorrespondence(List.of(abstractAction), List.of(this.newMethod));
		}
		
		//
		ci.createAndAddCorrespondence(List.of(newResourceDemandingBehaviourElements), List.of(this.newMethod));
	}
}
