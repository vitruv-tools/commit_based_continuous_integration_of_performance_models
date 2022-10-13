package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory;
import tools.vitruv.change.correspondence.model.CorrespondenceModel;

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
			BasicComponentFinding componentFinding, CorrespondenceModel cm,
			BasicComponent com) {
		return new FunctionClassificationStrategyForCommitIntegration(componentFinding, cm, com);
	}
}
