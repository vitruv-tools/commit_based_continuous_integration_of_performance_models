package tools.vitruv.applications.pcmjava.seffstatements.ejbtransformations.java2pcm

import org.palladiosimulator.pcm.repository.BasicComponent
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFinding
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.Code2SEFFFactory
import tools.vitruv.framework.correspondence.CorrespondenceModel

class EjbJava2PcmCode2SeffFactory implements Code2SEFFFactory {
	override BasicComponentFinding createBasicComponentFinding() {
		return new EjbBasicComponentFinder()
	}

	override InterfaceOfExternalCallFinding createInterfaceOfExternalCallFinding(
		CorrespondenceModel correspondenceModel, BasicComponent basicComponent) {
		return new InterfaceOfExternalCallFinder4Ejb(correspondenceModel, basicComponent)
	}

	override ResourceDemandingBehaviourForClassMethodFinding createResourceDemandingBehaviourForClassMethodFinding(
		CorrespondenceModel correspondenceModel) {
		return new ResourceDemandingBehaviourForClassMethodFinderForEjb(correspondenceModel)
	}

	override AbstractFunctionClassificationStrategy createAbstractFunctionClassificationStrategy(
		BasicComponentFinding basicComponentFinding, CorrespondenceModel correspondenceModel,
		BasicComponent basicComponent) {
		return new Ejb2PcmFunctionClassificationStrategy(basicComponentFinding, correspondenceModel,
			basicComponent)
	}
}
