package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;

import tools.vitruv.change.correspondence.model.CorrespondenceModelUtil;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.change.correspondence.model.CorrespondenceModel;

/**
 * Finds the component for a method in the case of a commit-based integration.
 *
 * @author Martin Armbruster
 */
public class BasicComponentForCommitIntegrationFinder implements BasicComponentFinding {
    @Override
    public BasicComponent findBasicComponentForMethod(final Method newMethod, final CorrespondenceModel ci) {
    	var correspondences = CorrespondenceModelUtil.getCorrespondingEObjects(ci,
    			newMethod.getContainingConcreteClassifier(), BasicComponent.class);
    	if (correspondences != null && !correspondences.isEmpty()) {
    		return correspondences.iterator().next();
    	}
    	return null;
    }
}
