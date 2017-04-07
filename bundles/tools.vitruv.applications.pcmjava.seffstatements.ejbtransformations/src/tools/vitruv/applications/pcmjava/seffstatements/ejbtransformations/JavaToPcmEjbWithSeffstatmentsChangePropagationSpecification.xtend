package tools.vitruv.applications.pcmjava.seffstatements.ejbtransformations

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Java2PcmMethodBodyChangePreprocessor
import tools.vitruv.applications.pcmjava.ejbtransformations.java2pcm.change2commandtransforming.EjbJavaToPcmChangePropagationSpecification
import tools.vitruv.applications.pcmjava.seffstatements.ejbtransformations.java2pcm.EjbJava2PcmCode2SeffFactory

class JavaToPcmEjbWithSeffstatmentsChangePropagationSpecification extends EjbJavaToPcmChangePropagationSpecification {
	public override setup() {
		super.setup();
		addChangePreprocessor(new Java2PcmMethodBodyChangePreprocessor(userInteracting, new EjbJava2PcmCode2SeffFactory));
	}
}
