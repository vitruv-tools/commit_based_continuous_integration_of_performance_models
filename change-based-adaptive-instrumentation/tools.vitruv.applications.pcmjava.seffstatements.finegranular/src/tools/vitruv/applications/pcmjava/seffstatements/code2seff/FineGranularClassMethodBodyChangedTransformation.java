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
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ResourceDemandingBehaviourMatching.MatchinType;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ResourceDemandingBehaviourMatching.Matching;
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
	private SourceCodeDecoratorRepository sourceCodeDecorator;
	private SimilarityChecker similarityChecker;
	private final BasicComponentFinding basicComponentFinder;
	
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
		
		// 4) get Matching of olfSEFF and newSEFF
		ResourceDemandingBehaviourMatching rsdMatching = 
				getNewOldSeffMatching(oldSEFF, newSEFF, correspondenceModel);
		

		// 5) get oldSEFF newSEFF DIFF
		ResourceDemandingBehaviourDiff seffDiff = 
				this.getResourceDemandingBehaviourDiff(rsdMatching, oldSEFF, newSEFF);
		
		
		// 6) update old resource Demanding Behaviour
		this.updateOldResourceDemandingBehaviour(seffDiff, oldSEFF, newSEFF, rsdMatching);
		
		// 6) create new correspondences
		this.createNewCorrespondences(correspondenceModel, oldSEFF);
		
		// 7) create correspondence between Seff elements and statements
		this.bindAbstractActionsAndStatements(seffDiff, correspondenceModel);
		
		return;
	}
	
	private void updateOldResourceDemandingBehaviour(ResourceDemandingBehaviourDiff seffDiff,
			ResourceDemandingBehaviour rdBehavior, ResourceDemandingBehaviour newSeff,
			ResourceDemandingBehaviourMatching rsdMatching) {
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
		
		this.addNewAbstractActions(seffDiff, steps, newSeff, rsdMatching);
		
		VisitorUtils.connectActions(rdBehavior);
	}
	
	
	private void addNewAbstractActions(ResourceDemandingBehaviourDiff seffDiff,
			List<AbstractAction> steps, ResourceDemandingBehaviour newSeff,
			ResourceDemandingBehaviourMatching rsdMatching) {
		
		List<AbstractAction> newSeffAbstractActions = new ArrayList<AbstractAction>();
		for(AbstractAction aa: newSeff.getSteps_Behaviour()) {
			newSeffAbstractActions.add(aa);
		}
		
		AbstractAction newAbstractActionPredecessor = null;
		for(AbstractAction newAbstractAction: Lists.reverse(newSeffAbstractActions)) {
			AbstractAction matching = this.getNewAbstractActionMatching(newAbstractAction, rsdMatching);
			
			if(matching == null) {
				if(newAbstractActionPredecessor == null) {
					// case 1: the current element in the new SEFF has no Matching and has no Predecessor
					// ==> add it in the first position in the old SEFF
					int oldSeffFirstPositionLocation = steps.size() - 1; 
					steps.add(oldSeffFirstPositionLocation, newAbstractAction);
					
				}
				else {
					AbstractAction precessorMatching = this.getNewAbstractActionMatching(newAbstractActionPredecessor, rsdMatching);
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
	
	
	private ResourceDemandingBehaviourDiff getResourceDemandingBehaviourDiff(
			ResourceDemandingBehaviourMatching newOldSeffMatching, ResourceDemandingBehaviour oldSEFF,
			ResourceDemandingBehaviour newSEFF) {
		
		ResourceDemandingBehaviourDiff seffDiff = new ResourceDemandingBehaviourDiff();
		
		List<AbstractAction> listOldAbstractActions = this.getResourceDemandingBehaviourAbstractActions(oldSEFF);
		List<AbstractAction> listNewAbstractActions = this.getResourceDemandingBehaviourAbstractActions(newSEFF);
		
		// if the old seff is empty, all the new Abstract actions are added 
		if(newOldSeffMatching.getMatchingList().size() == 0 && listOldAbstractActions.size() == 0) {
			for(AbstractAction newAbstractAction: listNewAbstractActions) {
				seffDiff.getAddedAbstractActions().add(newAbstractAction);
			}
			
			return seffDiff;
		}
		
		// find modified and deleted abstract actions
		for(AbstractAction oldAbstractAction: listOldAbstractActions) {
			Matching matching = this.hasOldAbstractActionMatching(oldAbstractAction, newOldSeffMatching);
			if(matching == null) {
				seffDiff.getDeletedAbstractActions().add(oldAbstractAction);
			}
			else if(matching.getMatchinType().equals(MatchinType.MODIFIED)) {
				seffDiff.getModifiedAbstractActions().add(oldAbstractAction);
			}
		}
		
		// find add abstract actions
		for(AbstractAction newAbstractAction: listNewAbstractActions) {
			if(this.getNewAbstractActionMatching(newAbstractAction, newOldSeffMatching) == null) {
				seffDiff.getAddedAbstractActions().add(newAbstractAction);
			}
		}
		
		return seffDiff;
	}
	
	private Matching hasOldAbstractActionMatching(AbstractAction oldAbstractAction,
			ResourceDemandingBehaviourMatching rsdMatching) {
		for(Matching matching: rsdMatching.getMatchingList()) {
			if(oldAbstractAction.equals(matching.getOldAbstractAction())) {
				return matching;
			}
		}
		return null;
	}
	
	
	private AbstractAction getNewAbstractActionMatching(AbstractAction newAbstractAction,
			ResourceDemandingBehaviourMatching rsdMatching) {
		for(Matching matching: rsdMatching.getMatchingList()) {
			if(matching.getNewAbstractAction().equals(newAbstractAction)) {
				return matching.getOldAbstractAction();
			}
		}
		return null;
	}

	private ResourceDemandingBehaviour createNewResourceDemandingBehaviour(final CorrespondenceModel correspondenceModel) {
		ResourceDemandingBehaviour newSEFF = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
		final BasicComponent basicComponent = this.basicComponentFinder.findBasicComponentForMethod(this.newMethod,
				correspondenceModel);

		this.executeSoMoXForMethod(basicComponent, newSEFF);

		return newSEFF;
	}
	
	private ResourceDemandingBehaviourMatching getNewOldSeffMatching(ResourceDemandingBehaviour oldSEFF,
			ResourceDemandingBehaviour newSEFF, CorrespondenceModel ci) {
		ResourceDemandingBehaviourMatching rsdMachting = new ResourceDemandingBehaviourMatching();
		
		List<AbstractAction> oldAbstractActions = this.getResourceDemandingBehaviourAbstractActions(oldSEFF);
		Map<AbstractAction, List<Statement>> newSeffStatements = this.newSeffElementsStatements(newSEFF);
				
		for (Map.Entry<AbstractAction, List<Statement>> entry : newSeffStatements.entrySet())
		{			
			AbstractAction newAbstractAction = entry.getKey();
			List<Statement> newAbstractActionStatements = entry.getValue();

			
			for(AbstractAction oldAbstractAction: oldAbstractActions) {
				// get old AbstractActions corresponding statements
				Set<Statement> oldAbstractActionStatements = CorrespondenceModelUtil
						.getCorrespondingEObjects(ci, oldAbstractAction, Statement.class);
				
				
				int similarStatementsCount = this.compareAbstractActions(oldAbstractAction, 
						newAbstractActionStatements, 
						newAbstractAction, 
						oldAbstractActionStatements);
				
				if(newAbstractActionStatements.size() == similarStatementsCount) {
					rsdMachting.addMatching(newAbstractAction, oldAbstractAction, MatchinType.TOTAL_EQUAL);
					break;
				}
				else if(similarStatementsCount != 0){
					// make sure the new and the old abstract actions have the same location
					rsdMachting.addMatching(newAbstractAction, oldAbstractAction, MatchinType.MODIFIED);
					break;
				}
				
			}
						
		}
		
		return rsdMachting;
	}
	
	
	private int compareAbstractActions(AbstractAction oldAbstractAction,
			 List<Statement> newAbstractActionStatements, AbstractAction newAbstractAction, 
			 Set<Statement> oldAbstractActionStatements) {
		if(!oldAbstractAction.getClass().getSimpleName().equals(newAbstractAction.getClass().getSimpleName())) {
			return 0;
		}
		else {
			// compare statements
		    return this.compareListOfStatements(newAbstractActionStatements,
		    		oldAbstractActionStatements);
		}
	}
	
	/*
	 * return the number of the similar statement
	 */
	private int compareListOfStatements(List<Statement> newStatements, Set<Statement> oldStatements) {
		int similarStatementsCount = 0;
		for(Statement newStatement: newStatements) {
			for(Statement oldStatement: oldStatements) {
				if(this.similarityChecker.isSimilar(newStatement, oldStatement)) {
					similarStatementsCount++;
				}
			}
		}
		return similarStatementsCount;
	}
	
	
	private Map<AbstractAction, List<Statement>> newSeffElementsStatements(ResourceDemandingBehaviour newSEFF){
		    Map<AbstractAction, List<Statement>> newSeffStatements  = 
					new HashMap<AbstractAction, List<Statement>>();
		
			List<SeffElementSourceCodeLink> seffStatementLinks = sourceCodeDecorator.getSeffElementsSourceCodeLinks();
			
			for(SeffElementSourceCodeLink seffStatementLink :seffStatementLinks) {
				if(seffStatementLink.getSeffElement() instanceof ResourceDemandingBehaviour) {
					ResourceDemandingBehaviour seff = (ResourceDemandingBehaviour) seffStatementLink.getSeffElement();
	            	AbstractBranchTransition abstractBranchTr = seff.getAbstractBranchTransition_ResourceDemandingBehaviour();
	            	
	            	BranchAction branchAction = abstractBranchTr.getBranchAction_AbstractBranchTransition();
	            	
	            	List<Statement> listStatement = seffStatementLink.getStatement();
					newSeffStatements.put((AbstractAction) branchAction, listStatement);
				}
				else {
					List<Statement> listStatement = seffStatementLink.getStatement();
					newSeffStatements.put((AbstractAction) seffStatementLink.getSeffElement(), listStatement);
				}
				
			}
			
			return newSeffStatements;
	}
		
	
	private List<AbstractAction> getResourceDemandingBehaviourAbstractActions(ResourceDemandingBehaviour seff){
		List<AbstractAction> listAbstractActions =  new ArrayList<AbstractAction>();
		for(AbstractAction aa: seff.getSteps_Behaviour()) {
			if(!(aa instanceof StartAction || aa instanceof StopAction)) {
				listAbstractActions.add(aa);
			}
		}
		return listAbstractActions;
	}
	
	
	
	private void bindAbstractActionsAndStatements(ResourceDemandingBehaviourDiff seffDiff,
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
        		if(seffDiff.getAddedAbstractActions().contains(seffElement)) {
        			this.createAbstractActionStatementsCorrespondence((AbstractAction) seffElement, 
        					listStatements, correspondenceModel);
        		}
        	}
        	else if(seffElement instanceof ResourceDemandingBehaviour) {
        		ResourceDemandingBehaviour seff = (ResourceDemandingBehaviour) seffElement;
            	AbstractBranchTransition abstractBranchTr = seff.getAbstractBranchTransition_ResourceDemandingBehaviour();
            	
            	BranchAction branchAction = abstractBranchTr.getBranchAction_AbstractBranchTransition();
            	
            	if(seffDiff.getAddedAbstractActions().contains(branchAction)) {
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
