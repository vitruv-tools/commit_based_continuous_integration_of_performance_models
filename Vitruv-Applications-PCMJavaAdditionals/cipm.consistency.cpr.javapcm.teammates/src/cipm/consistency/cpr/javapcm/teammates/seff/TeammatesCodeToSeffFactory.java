package cipm.consistency.cpr.javapcm.teammates.seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.CommitIntegrationCodeToSeffFactory;
import tools.vitruv.change.correspondence.model.CorrespondenceModel;

public class TeammatesCodeToSeffFactory extends CommitIntegrationCodeToSeffFactory {
	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			BasicComponentFinding componentFinding, CorrespondenceModel cm,
			BasicComponent com) {
		return new TeammatesFunctionClassificationStrategy(componentFinding, cm, com);
	}
}
