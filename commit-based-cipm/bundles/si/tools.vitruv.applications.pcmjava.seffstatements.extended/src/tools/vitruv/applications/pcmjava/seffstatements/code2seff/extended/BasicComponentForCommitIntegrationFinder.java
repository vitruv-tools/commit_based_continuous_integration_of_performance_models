package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;

/**
 * Finds the component for a method in the case of a commit-based integration.
 *
 * @author Martin Armbruster
 */
public class BasicComponentForCommitIntegrationFinder implements BasicComponentFinding {
    @Override
    public BasicComponent findBasicComponentForMethod(final Method newMethod,
            final CorrespondenceModelView<Correspondence> cmv) {
        var correspondences = cmv.getCorrespondingEObjects(newMethod.getContainingConcreteClassifier(), null);
        if (correspondences != null && !correspondences.isEmpty()) {
            return (BasicComponent) correspondences.iterator()
                .next();
        }
        return null;
    }
}
