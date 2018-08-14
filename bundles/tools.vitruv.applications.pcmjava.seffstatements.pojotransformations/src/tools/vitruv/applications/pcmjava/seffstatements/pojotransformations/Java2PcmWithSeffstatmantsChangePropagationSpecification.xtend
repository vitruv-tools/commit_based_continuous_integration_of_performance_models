package tools.vitruv.applications.pcmjava.seffstatements.pojotransformations

import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmPackageMappingMethodBodyChangePreprocessor
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.Java2PcmChangePropagationSpecification

class Java2PcmWithSeffstatmantsChangePropagationSpecification extends Java2PcmChangePropagationSpecification {
	protected override setup() {
		super.setup();
		addChangePreprocessor(new Java2PcmPackageMappingMethodBodyChangePreprocessor(userInteractor)); 
	}
}
