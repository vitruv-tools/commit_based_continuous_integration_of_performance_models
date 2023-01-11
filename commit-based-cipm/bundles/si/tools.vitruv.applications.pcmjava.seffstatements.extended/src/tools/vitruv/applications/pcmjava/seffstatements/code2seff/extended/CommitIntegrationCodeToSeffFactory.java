package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

/**
 * Provides a CodeToSeffFactory implementation for the commit-based integration.
 * 
 * @author Martin Armbruster
 */
public class CommitIntegrationCodeToSeffFactory extends PojoJava2PcmCodeToSeffFactory {
	@Override
	public BasicComponentFinding createBasicComponentFinding() {
		return new BasicComponentForCommitIntegrationFinder();
	}
	
	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			BasicComponentFinding componentFinding, EditableCorrespondenceModelView<Correspondence> cmv,
			BasicComponent com) {
		return new FunctionClassificationStrategyForCommitIntegration(componentFinding, cmv, com);
	}
}
