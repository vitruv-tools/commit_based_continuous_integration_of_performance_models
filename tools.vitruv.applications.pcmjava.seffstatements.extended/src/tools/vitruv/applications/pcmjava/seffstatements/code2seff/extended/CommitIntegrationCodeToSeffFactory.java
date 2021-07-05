package tools.vitruv.applications.pcmjava.seffstatements.code2seff.extended;

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory;

public class CommitIntegrationCodeToSeffFactory extends PojoJava2PcmCodeToSeffFactory {
	@Override
	public BasicComponentFinding createBasicComponentFinding() {
		return new BasicComponentForCommitIntegrationFinder();
	}
}
