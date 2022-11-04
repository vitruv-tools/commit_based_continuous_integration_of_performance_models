package tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff;

import java.util.Set;
import org.apache.log4j.Logger;
import org.emftext.language.java.members.ClassMethod;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.correspondence.view.util.CorrespondenceModelViewUtil;

public class ResourceDemandingBehaviourForClassMethodFinderForPackageMapping
        implements ResourceDemandingBehaviourForClassMethodFinding {

    private static final Logger LOGGER = Logger
            .getLogger(ResourceDemandingBehaviourForClassMethodFinderForPackageMapping.class.getSimpleName());

    private final EditableCorrespondenceModelView<Correspondence> correspondenceModelView;

    public ResourceDemandingBehaviourForClassMethodFinderForPackageMapping(
            final EditableCorrespondenceModelView<Correspondence> correspondenceModelView) {
        this.correspondenceModelView = correspondenceModelView;
    }

    @Override
    public ResourceDemandingSEFF getCorrespondingRDSEFForClassMethod(final ClassMethod classMethod) {
        return this.getFirstCorrespondingEObjectIfAny(classMethod, ResourceDemandingSEFF.class);
    }

    private <T> T getFirstCorrespondingEObjectIfAny(final ClassMethod classMethod, final Class<T> correspondingClass) {
        final Set<T> correspondingObjects = CorrespondenceModelViewUtil
                .getCorrespondingEObjects(this.correspondenceModelView, classMethod, correspondingClass);
        if (correspondingObjects == null || correspondingObjects.isEmpty()) {
            return null;
        }
        if (1 < correspondingObjects.size()) {
            LOGGER.warn("Found " + correspondingObjects.size() + " corresponding Objects from Type "
                    + correspondingClass + " for ClassMethod " + classMethod + " Returning the first.");
        }
        return correspondingObjects.iterator().next();
    }

    @Override
    public ResourceDemandingInternalBehaviour getCorrespondingResourceDemandingInternalBehaviour(
            final ClassMethod classMethod) {
        return this.getFirstCorrespondingEObjectIfAny(classMethod, ResourceDemandingInternalBehaviour.class);
    }

}
