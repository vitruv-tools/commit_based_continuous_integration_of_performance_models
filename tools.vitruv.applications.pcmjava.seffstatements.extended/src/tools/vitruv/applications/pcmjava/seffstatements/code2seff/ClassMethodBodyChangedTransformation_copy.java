package tools.vitruv.applications.pcmjava.seffstatements.code2seff;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.somox.gast2seff.visitors.FunctionCallClassificationVisitor;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.MethodCallFinder;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.gast2seff.visitors.VisitorUtils;
import org.somox.sourcecodedecorator.AbstractActionClassMethodLink;
import org.somox.sourcecodedecorator.ControlFlowLevelSourceCodeLink;
import org.somox.sourcecodedecorator.MethodLevelSourceCodeLink;
import org.somox.sourcecodedecorator.SEFF2MethodMapping;
import org.somox.sourcecodedecorator.SeffElementSourceCodeLink;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;
import org.somox.sourcecodedecorator.SourcecodedecoratorFactory;

import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.framework.userinteraction.UserInteractor;
import tools.vitruv.framework.util.bridges.CollectionBridge;





/**
 * Class that keeps changes within a class method body consistent with the
 * architecture. Has dependencies to SoMoX as well as to Vitruvius. This is the 
 * reason that the class is in its own plugin. The class is written in Java and
 * not in Xtend (we do not need the cool features of Xtend within the class).
 *
 * @author langhamm
 *
 */
public class ClassMethodBodyChangedTransformation_copy {

	private final static Logger logger = Logger.getLogger(ClassMethodBodyChangedTransformation_copy.class.getSimpleName());

	private SourceCodeDecoratorRepository newSourceDecorator = null;
	private final Method oldMethod;
	private final Method newMethod;
	private final BasicComponentFinding basicComponentFinder;
	private final IFunctionClassificationStrategy iFunctionClassificationStrategy;

	private final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory;

	private final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding;

	public ClassMethodBodyChangedTransformation_copy(final Method oldMethod, final Method newMethod,
			final BasicComponentFinding basicComponentFinder,
			final IFunctionClassificationStrategy iFunctionClassificationStrategy,
			final InterfaceOfExternalCallFindingFactory InterfaceOfExternalCallFindingFactory,
			final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		this.oldMethod = oldMethod;
		this.newMethod = newMethod;
		this.basicComponentFinder = basicComponentFinder;
		this.iFunctionClassificationStrategy = iFunctionClassificationStrategy;
		this.interfaceOfExternalCallFinderFactory = InterfaceOfExternalCallFindingFactory;
		this.resourceDemandingBehaviourForClassMethodFinding = resourceDemandingBehaviourForClassMethodFinding;
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
			logger.debug("Change with oldMethod " + this.oldMethod + " and newMethod: " + this.newMethod
					+ " is not an architecture relevant change");
			return;
		}
		
		// verify if the Seff structure has changed, if yes then update abstract actions statements 
	    // correspondence
////		if(!this.doesSeffStructureChanged(correspondenceModel)) {
////			
////			return;
////		}
//				
		// 1)
		this.removeCorrespondingAbstractActions(correspondenceModel);

		// 2)
		final ResourceDemandingBehaviour resourceDemandingBehaviour = this
				.findRdBehaviorToInsertElements(correspondenceModel);
		
		final BasicComponent basicComponent = this.basicComponentFinder.findBasicComponentForMethod(this.newMethod,
				correspondenceModel);
		
		SourceCodeDecoratorRepository sourceCodeDecorator = this.executeSoMoXForMethod(basicComponent, resourceDemandingBehaviour);

		// 3)
		this.connectCreatedResourceDemandingBehaviour(resourceDemandingBehaviour);

		// 4)
		this.createNewCorrespondences(correspondenceModel, resourceDemandingBehaviour, basicComponent);
		
		// 5)
		this.bindAbstractActionsAndStatements(sourceCodeDecorator, correspondenceModel);
				
		
		return;
	}
	
	
	//
	private boolean doesSeffStructureChanged(final CorrespondenceModel correspondenceModel) {
		ResourceDemandingBehaviour newBehaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
		final BasicComponent basicComponent = this.basicComponentFinder.findBasicComponentForMethod(this.newMethod,
				correspondenceModel);
		
		// 1- run somox to return the ResourceDemandingBehaviour
		SourceCodeDecoratorRepository sourceCodeDecorator =
				this.executeSoMoXForMethod(basicComponent, newBehaviour);
		
		//FIXME: remove
		this.newSourceDecorator = sourceCodeDecorator;
		
		// 2- return the old ResourceDemandingBehaviour
		final Set<ResourceDemandingBehaviour> oldBehaviour = CorrespondenceModelUtil
				.getCorrespondingEObjectsByType(correspondenceModel, this.oldMethod, ResourceDemandingBehaviour.class);
		
		// 3-1- if the old list of abstract action is empty we consider the Seff changed
		if(oldBehaviour.iterator().next().getSteps_Behaviour().isEmpty()) {
			return true;
		}
	
		// 3-2- compare the list of Abstract Action of both ResourceDemading Behaviour 
		for(AbstractAction oldAbstractAction: oldBehaviour.iterator().next().getSteps_Behaviour()) {
			if(!(oldAbstractAction instanceof StartAction || oldAbstractAction instanceof StopAction)) {
				for(AbstractAction newAbstractAction: newBehaviour.getSteps_Behaviour()) {
					if(!oldAbstractAction.getClass().getSimpleName().equals(newAbstractAction.getClass().getSimpleName())) {
						return true;
					}
				}
			}
		}
			
		return false;
	}
	
	//
	
	
	private void bindAbstractActionsAndStatements(SourceCodeDecoratorRepository sourceCodeDecorator,
			CorrespondenceModel correspondenceModel) {
		List<SeffElementSourceCodeLink> seffElementSourceCodeLinks =  sourceCodeDecorator.getSeffElementsSourceCodeLinks();
        for(SeffElementSourceCodeLink seffElementSourceCodeLink: seffElementSourceCodeLinks) {
            if(seffElementSourceCodeLink.getSeffElement() instanceof AbstractAction) {
            	AbstractAction ab = (AbstractAction) seffElementSourceCodeLink.getSeffElement();
            	//
            	CorrespondenceModelUtil.createAndAddCorrespondence(correspondenceModel, ab, this.newMethod);
            	//
            	
	            for(Statement statement: seffElementSourceCodeLink.getStatement()) {
                    CorrespondenceModelUtil.createAndAddCorrespondence(correspondenceModel, ab, statement);
	            }
            } 
            else if(seffElementSourceCodeLink.getSeffElement() instanceof ResourceDemandingBehaviour) {
            	ResourceDemandingBehaviour seff = (ResourceDemandingBehaviour) seffElementSourceCodeLink.getSeffElement();
            	AbstractBranchTransition abstractBranchTr = seff.getAbstractBranchTransition_ResourceDemandingBehaviour();
            	
            	BranchAction branchAction = abstractBranchTr.getBranchAction_AbstractBranchTransition();
            	
            	//
            	CorrespondenceModelUtil.createAndAddCorrespondence(correspondenceModel, branchAction, this.newMethod);
            	//
            	
            	for(Statement statement: seffElementSourceCodeLink.getStatement()) {
            		CorrespondenceModelUtil.createAndAddCorrespondence(correspondenceModel, branchAction, statement);
            	}
            }
        }

	}
	
	
	/**
	 * checks whether the change is considered architecture relevant. This is
	 * the case if either the new or the old method does have a corresponding
	 * ResourceDemandingBehaviour
	 *
	 * @param ci
	 * @return
	 */
	private boolean isArchitectureRelevantChange(final CorrespondenceModel ci) {
		return this.isMethodArchitectureRelevant(this.oldMethod, ci)
				|| this.isMethodArchitectureRelevant(this.newMethod, ci);
	}

	private boolean isMethodArchitectureRelevant(final Method method, final CorrespondenceModel ci) {
		if (null != method) {
			final Set<ResourceDemandingBehaviour> correspondingEObjectsByType = CorrespondenceModelUtil
					.getCorrespondingEObjectsByType(ci, method, ResourceDemandingBehaviour.class);
			if (null != correspondingEObjectsByType && !correspondingEObjectsByType.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private SourceCodeDecoratorRepository executeSoMoXForMethod(final BasicComponent basicComponent,
			final ResourceDemandingBehaviour targetResourceDemandingBehaviour) {
		
		SourceCodeDecoratorRepository sourceCodeDecorator =
				SourcecodedecoratorFactory.eINSTANCE.createSourceCodeDecoratorRepository();
		
		final MethodCallFinder methodCallFinder = new MethodCallFinder();
		final FunctionCallClassificationVisitor functionCallClassificationVisitor = new FunctionCallClassificationVisitor(
				this.iFunctionClassificationStrategy, methodCallFinder);
		if (this.newMethod instanceof ClassMethod) {
			// check whether the newMethod is a class method is done here. Could
			// be done eariler,
			// i.e. the class could only deal with ClassMethods, but this caused
			// problems when
			// changing an abstract method to a ClassMethod
			VisitorUtils.visitJaMoPPMethod(targetResourceDemandingBehaviour, basicComponent,
					(StatementListContainer) this.newMethod, sourceCodeDecorator, functionCallClassificationVisitor,
					this.interfaceOfExternalCallFinderFactory, this.resourceDemandingBehaviourForClassMethodFinding,
					methodCallFinder);
		} else {
			logger.info("No SEFF recreated for method " + this.newMethod.getName()
					+ " because it is not a class method. Method " + this.newMethod);
		}
		
		return sourceCodeDecorator;

	}

	private void createNewCorrespondences(final CorrespondenceModel ci,
			final ResourceDemandingBehaviour newResourceDemandingBehaviourElements,
			final BasicComponent basicComponent) {
		for (final AbstractAction abstractAction : newResourceDemandingBehaviourElements.getSteps_Behaviour()) {
			CorrespondenceModelUtil.createAndAddCorrespondence(ci, abstractAction, this.newMethod);
		}
		
		//
		CorrespondenceModelUtil.createAndAddCorrespondence(ci,newResourceDemandingBehaviourElements, this.newMethod);
	}

	private void connectCreatedResourceDemandingBehaviour(final ResourceDemandingBehaviour rdBehavior) {
		final EList<AbstractAction> steps = rdBehavior.getSteps_Behaviour();
		final boolean addStartAction = 0 == steps.size() || !(steps.get(0) instanceof StartAction);
		final boolean addStopAction = 0 == steps.size() || !(steps.get(steps.size() - 1) instanceof StopAction);

		if (addStartAction) {
			rdBehavior.getSteps_Behaviour().add(0, SeffFactory.eINSTANCE.createStartAction());
		}
		if (addStopAction) {
			final AbstractAction stopAction = SeffFactory.eINSTANCE.createStopAction();
			rdBehavior.getSteps_Behaviour().add(stopAction);
		}
		VisitorUtils.connectActions(rdBehavior);
	}

	private void removeCorrespondingAbstractActions(final CorrespondenceModel ci) {
		final Set<AbstractAction> correspondingAbstractActions = CorrespondenceModelUtil
				.getCorrespondingEObjectsByType(ci, this.oldMethod, AbstractAction.class);
		if (null == correspondingAbstractActions) {
			return;
		}
		final ResourceDemandingBehaviour resourceDemandingBehaviour = this
				.getAndValidateResourceDemandingBehavior(correspondingAbstractActions);
		if (null == resourceDemandingBehaviour) {
			return;
		}
		for (final AbstractAction correspondingAbstractAction : correspondingAbstractActions) {
			ci.removeCorrespondencesThatInvolveAtLeastAndDependend(CollectionBridge.toSet(correspondingAbstractAction));
			EcoreUtil.remove(correspondingAbstractAction);
		}

		for (final AbstractAction abstractAction : resourceDemandingBehaviour.getSteps_Behaviour()) {
			if (!(abstractAction instanceof StartAction || abstractAction instanceof StopAction)) {
				logger.warn(
						"The resource demanding behavior should be empty, but it contains at least following AbstractAction "
								+ abstractAction);
			}
		}
	}

	private ResourceDemandingBehaviour getAndValidateResourceDemandingBehavior(
			final Set<AbstractAction> correspondingAbstractActions) {
		ResourceDemandingBehaviour resourceDemandingBehaviour = null;
		for (final AbstractAction abstractAction : correspondingAbstractActions) {
			if (null == abstractAction.getResourceDemandingBehaviour_AbstractAction()) {
				logger.warn("AbstractAction " + abstractAction
						+ " does not have a parent ResourceDemandingBehaviour - this should not happen.");
				continue;
			}
			if (null == resourceDemandingBehaviour) {
				// set resourceDemandingBehaviour in first cycle
				resourceDemandingBehaviour = abstractAction.getResourceDemandingBehaviour_AbstractAction();
				continue;
			}
			if (resourceDemandingBehaviour != abstractAction.getResourceDemandingBehaviour_AbstractAction()) {
				logger.warn("resourceDemandingBehaviour " + resourceDemandingBehaviour
						+ " is different that current resourceDemandingBehaviour: "
						+ abstractAction.getResourceDemandingBehaviour_AbstractAction());
			}

		}
		return resourceDemandingBehaviour;
	}

	private ResourceDemandingBehaviour findRdBehaviorToInsertElements(final CorrespondenceModel ci) {
		final Set<ResourceDemandingBehaviour> correspondingResourceDemandingBehaviours = CorrespondenceModelUtil
				.getCorrespondingEObjectsByType(ci, this.oldMethod, ResourceDemandingBehaviour.class);
		if (null == correspondingResourceDemandingBehaviours || correspondingResourceDemandingBehaviours.isEmpty()) {
			logger.warn("No ResourceDemandingBehaviours found for method " + this.oldMethod
					+ ". Could not create ResourceDemandingBehavoir to insert SEFF elements");
			return null;
		}
		return correspondingResourceDemandingBehaviours.iterator().next();
	}
}
