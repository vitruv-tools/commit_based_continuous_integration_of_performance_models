package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.sourcecodedecorator.SeffElementSourceCodeLink;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;

import com.google.common.collect.Lists;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ClassMethodBodyChangedTransformation;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.userinteraction.UserInteractor;

/**
 * Class that keeps changes within a class method body consistent with the
 * architecture.
 *
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
public class ExtendedClassMethodBodyChangedTransformation extends ClassMethodBodyChangedTransformation {

	private final static Logger logger = Logger.getLogger(ExtendedClassMethodBodyChangedTransformation.class.getSimpleName());
	
	private final Method newMethod;

	public ExtendedClassMethodBodyChangedTransformation(final Method oldMethod, final Method newMethod,
			final BasicComponentFinding basicComponentFinder,
			final IFunctionClassificationStrategy iFunctionClassificationStrategy,
			final InterfaceOfExternalCallFindingFactory InterfaceOfExternalCallFindingFactory,
			final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
		super(oldMethod, newMethod, basicComponentFinder, iFunctionClassificationStrategy, InterfaceOfExternalCallFindingFactory,
				resourceDemandingBehaviourForClassMethodFinding);
		this.newMethod = newMethod;
	}

	/**
	 * Extends the process to keep Java method bodies consistent with SEFFs by introducing the following step:
	 * 5) Link the abstract actions with the corresponding statements which is needed
	 * during the instrumentation process.
	 */
	@Override
	public void execute(final CorrespondenceModel correspondenceModel,
			final UserInteractor userInteracting) {
		super.execute(correspondenceModel, userInteracting);
		// 5) Link the abstract actions with their corresponding statements.
		this.bindAbstractActionsAndStatements(super.getSourceCodeDecoratorRepository(), correspondenceModel);
	}
	
	private void bindAbstractActionsAndStatements(SourceCodeDecoratorRepository sourceCodeDecorator,
			CorrespondenceModel correspondenceModel) {
		List<SeffElementSourceCodeLink> seffElementSourceCodeLinks =  sourceCodeDecorator.getSeffElementsSourceCodeLinks();
		List<EObject> newMethodList = Lists.newArrayList(newMethod);
		
        for (SeffElementSourceCodeLink seffElementSourceCodeLink : seffElementSourceCodeLinks) {
            if (seffElementSourceCodeLink.getSeffElement() instanceof AbstractAction) {
            	AbstractAction ab = (AbstractAction) seffElementSourceCodeLink.getSeffElement();
            	List<EObject> actionList = Lists.newArrayList(ab);
            	
            	correspondenceModel.createAndAddCorrespondence(actionList, newMethodList);
            	
	            for (Statement statement : seffElementSourceCodeLink.getStatement()) {
                    correspondenceModel.createAndAddCorrespondence(actionList, Lists.newArrayList(statement));
	            }
            } else if (seffElementSourceCodeLink.getSeffElement() instanceof ResourceDemandingBehaviour) {
            	ResourceDemandingBehaviour seff = (ResourceDemandingBehaviour) seffElementSourceCodeLink.getSeffElement();
            	AbstractBranchTransition abstractBranchTr = seff.getAbstractBranchTransition_ResourceDemandingBehaviour();
            	BranchAction branchAction = abstractBranchTr.getBranchAction_AbstractBranchTransition();
            	List<EObject> actionList = Lists.newArrayList(branchAction);
            	
            	correspondenceModel.createAndAddCorrespondence(actionList, newMethodList);
            	
            	for(Statement statement: seffElementSourceCodeLink.getStatement()) {
            		correspondenceModel.createAndAddCorrespondence(actionList, Lists.newArrayList(statement));
            	}
            }
        }
	}
}
