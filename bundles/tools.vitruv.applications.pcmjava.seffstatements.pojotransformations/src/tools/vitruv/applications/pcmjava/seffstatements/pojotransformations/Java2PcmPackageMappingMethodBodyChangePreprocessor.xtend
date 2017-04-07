package tools.vitruv.applications.pcmjava.seffstatements.pojotransformations

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Java2PcmMethodBodyChangePreprocessor
import tools.vitruv.framework.userinteraction.UserInteracting
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.PojoJavaToPcmCodeToSeffFactory

class Java2PcmPackageMappingMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {
	new(UserInteracting userInteracting) {
		super(userInteracting, new PojoJavaToPcmCodeToSeffFactory());
	}
}
