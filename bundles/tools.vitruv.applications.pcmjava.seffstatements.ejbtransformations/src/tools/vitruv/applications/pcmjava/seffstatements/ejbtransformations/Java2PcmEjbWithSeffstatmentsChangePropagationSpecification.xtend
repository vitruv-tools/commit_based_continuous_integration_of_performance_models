package tools.vitruv.applications.pcmjava.seffstatements.ejbtransformations

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Java2PcmMethodBodyChangePreprocessor
import tools.vitruv.applications.pcmjava.seffstatements.ejbtransformations.java2pcm.EjbJava2PcmCode2SeffFactory
import tools.vitruv.applications.pcmjava.ejbtransformations.java2pcm.change2commandtransforming.EjbJava2PcmChangePropagationSpecification

class Java2PcmEjbWithSeffstatmentsChangePropagationSpecification extends EjbJava2PcmChangePropagationSpecification {
	public override setup() {
		super.setup();
		addChangePreprocessor(new Java2PcmMethodBodyChangePreprocessor(new EjbJava2PcmCode2SeffFactory)); 
	}
}
