package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.Statement;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.somox.gast2seff.visitors.IFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.sourcecodedecorator.SeffElementSourceCodeLink;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.ClassMethodBodyChangedTransformation;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.correspondence.view.util.CorrespondenceModelViewUtil;
import tools.vitruv.change.interaction.UserInteractor;

/**
 * Class that keeps changes within a class method body consistent with the architecture.
 *
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
public class ExtendedClassMethodBodyChangedTransformation extends ClassMethodBodyChangedTransformation {
    public ExtendedClassMethodBodyChangedTransformation(final Method newMethod,
            final BasicComponentFinding basicComponentFinder,
            final IFunctionClassificationStrategy iFunctionClassificationStrategy,
            final InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFindingFactory,
            final ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding) {
        super(newMethod, basicComponentFinder, iFunctionClassificationStrategy, interfaceOfExternalCallFindingFactory,
                resourceDemandingBehaviourForClassMethodFinding);
    }

    /**
     * Extends the process to keep Java method bodies consistent with SEFFs by introducing the
     * following step: 5) Link the abstract actions with the corresponding statements which are
     * needed during the instrumentation process.
     */
    @Override
    public void execute(final EditableCorrespondenceModelView<Correspondence> correspondenceModelView,
            final UserInteractor userInteracting) {
        super.execute(correspondenceModelView, userInteracting);
        var decorator = super.getSourceCodeDecoratorRepository();
        if (decorator != null) {
            // 5) Link the abstract actions with their corresponding statements.
            this.bindAbstractActionsAndStatements(decorator, correspondenceModelView);
        }
    }

    private void bindAbstractActionsAndStatements(SourceCodeDecoratorRepository sourceCodeDecorator,
            EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
        List<SeffElementSourceCodeLink> seffElementSourceCodeLinks = sourceCodeDecorator
            .getSeffElementsSourceCodeLinks();

        for (SeffElementSourceCodeLink seffElementSourceCodeLink : seffElementSourceCodeLinks) {
            if (seffElementSourceCodeLink.getSeffElement() instanceof AbstractAction) {
                AbstractAction ab = (AbstractAction) seffElementSourceCodeLink.getSeffElement();
                // Actions in ResourceDemandingInternalBehaviours are ignored.
                if (this.isContainedInResourceDemandingInternalBehaviour(ab)) {
                    continue;
                }
                List<EObject> actionList = Lists.newArrayList(ab);
                for (Statement statement : seffElementSourceCodeLink.getStatement()) {
//                    CorrespondenceModelViewUtil.addCorrespondenceBetween(correspondenceModel, actionList,
//                            seffElementSourceCodeLinks, null);
                    CorrespondenceModelViewUtil.addCorrespondenceBetween(correspondenceModel, actionList,
                            List.of(statement), null);
                }
            }
        }
    }

    /**
     * Checks if an action is contained within a ResourceDemandingInternalBehaviour.
     * 
     * @param action
     *            the action to check.
     * @return true if the action is contained within a ResourceDemandingInternalBehaviour. false
     *         otherwise.
     */
    private boolean isContainedInResourceDemandingInternalBehaviour(AbstractAction action) {
        EObject obj = action.eContainer();
        while (obj != null) {
            if (obj instanceof ResourceDemandingInternalBehaviour) {
                return true;
            } else if (obj instanceof ResourceDemandingSEFF) {
                return false;
            }
            obj = obj.eContainer();
        }
        return false;
    }
}
