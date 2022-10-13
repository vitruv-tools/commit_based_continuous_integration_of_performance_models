package tools.vitruv.applications.pcmjava.seffstatements.pojotransformations

import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Java2PcmMethodBodyChangePreprocessor
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory
import tools.vitruv.change.interaction.UserInteractor

class Java2PcmPackageMappingMethodBodyChangePreprocessor extends Java2PcmMethodBodyChangePreprocessor {
	new(UserInteractor userInteracting) {
		super(new PojoJava2PcmCodeToSeffFactory());
	}
}
