package cipm.consistency.cpr.javapcm.teammates.seff;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended.CommitIntegrationCodeToSeffFactory;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;

public class TeammatesCodeToSeffFactory extends CommitIntegrationCodeToSeffFactory {
	@Override
	public AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
			BasicComponentFinding componentFinding, EditableCorrespondenceModelView<Correspondence> cmv,
			BasicComponent com) {
		return new TeammatesFunctionClassificationStrategy(componentFinding, cmv, com);
	}
}
