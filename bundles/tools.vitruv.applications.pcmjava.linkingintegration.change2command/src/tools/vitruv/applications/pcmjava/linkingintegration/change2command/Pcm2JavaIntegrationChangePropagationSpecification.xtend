package tools.vitruv.applications.pcmjava.linkingintegration.change2command

import tools.vitruv.applications.pcmjava.pojotransformations.pcm2java.Pcm2JavaChangePropagationSpecification

class Pcm2JavaIntegrationChangePropagationSpecification extends Pcm2JavaChangePropagationSpecification {
	
	override protected setup() {
		super.setup();
		addChangePreprocessor(new CodeIntegrationChangeProcessor(userInteracting));
	}
	
}