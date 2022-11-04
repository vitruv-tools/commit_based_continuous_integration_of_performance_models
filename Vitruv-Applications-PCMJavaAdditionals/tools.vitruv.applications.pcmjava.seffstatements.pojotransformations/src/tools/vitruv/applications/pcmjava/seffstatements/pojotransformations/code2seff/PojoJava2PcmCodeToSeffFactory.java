package tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFinding;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Code2SeffFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public class PojoJava2PcmCodeToSeffFactory implements Code2SeffFactory {

    @Override
    public BasicComponentFinding createBasicComponentFinding() {
        return new BasicComponentForPackageMappingFinder();
    }

    @Override
    public InterfaceOfExternalCallFindingFactory createInterfaceOfExternalCallFindingFactory(
            final EditableCorrespondenceModelView<Correspondence> correspondenceModelView,
            final BasicComponent basicComponent) {
        return new InterfaceOfExternalCallFindingFactory() {
            public InterfaceOfExternalCallFinding createInterfaceOfExternalCallFinding(
                    SourceCodeDecoratorRepository sourceCodeDecoratorRepository, BasicComponent basicComponent) {
                return new InterfaceOfExternalCallFinderForPackageMapping(correspondenceModelView, basicComponent);
            }
        };
    }

    @Override
    public ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
            final EditableCorrespondenceModelView<Correspondence> correspondenceModelView) {
        return new ResourceDemandingBehaviourForClassMethodFinderForPackageMapping(correspondenceModelView);
    }

    @Override
    public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
            final BasicComponentFinding basicComponentFinding,
            final EditableCorrespondenceModelView<Correspondence> correspondenceModelView,
            final BasicComponent basicComponent) {
        return new FunctionClassificationStrategyForPackageMapping(basicComponentFinding, correspondenceModelView,
                basicComponent);
    }

}
