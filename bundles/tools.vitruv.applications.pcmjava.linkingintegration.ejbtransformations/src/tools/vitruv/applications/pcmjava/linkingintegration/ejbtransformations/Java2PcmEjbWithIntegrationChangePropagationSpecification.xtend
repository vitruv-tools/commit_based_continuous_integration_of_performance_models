package tools.vitruv.applications.pcmjava.linkingintegration.ejbtransformations

import tools.vitruv.applications.pcmjava.linkingintegration.change2command.CodeIntegrationChangeProcessor
import tools.vitruv.applications.pcmjava.ejbtransformations.java2pcm.change2commandtransforming.EjbJava2PcmChangePropagationSpecification

class Java2PcmEjbWithIntegrationChangePropagationSpecification extends EjbJava2PcmChangePropagationSpecification {
	override setup() {
		super.setup();
		addChangePreprocessor(new CodeIntegrationChangeProcessor(userInteracting));
	}
}