package tools.vitruv.applications.pcmjava.seffstatements.code2seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public interface Code2SeffFactory {

    BasicComponentFinding createBasicComponentFinding();

    InterfaceOfExternalCallFindingFactory createInterfaceOfExternalCallFindingFactory(
    		EditableCorrespondenceModelView<Correspondence> correspondenceModelView, BasicComponent basicComponent);

    ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
    		EditableCorrespondenceModelView<Correspondence> correspondenceModelView);

    AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
            BasicComponentFinding basicComponentFinding, EditableCorrespondenceModelView<Correspondence> correspondenceModelView,
            BasicComponent basicComponent);
}
