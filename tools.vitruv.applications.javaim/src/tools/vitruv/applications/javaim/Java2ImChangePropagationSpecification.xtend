package tools.vitruv.applications.javaim

import org.emftext.language.java.members.Method
import tools.vitruv.framework.correspondence.CorrespondenceModel
import tools.vitruv.framework.userinteraction.UserInteractor
import tools.vitruv.framework.propagation.impl.AbstractChangePropagationSpecification
import tools.vitruv.applications.pcmjava.commitintegration.domains.java.AdjustedJavaDomainProvider
import tools.vitruv.framework.change.echange.EChange
import tools.vitruv.framework.propagation.ResourceAccess
import cipm.consistency.base.vsum.domains.InstrumentationModelDomainProvider
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel
import cipm.consistency.base.models.inmodel.InstrumentationModelUtil
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage

/**
 * Propagates changes in method bodies to the instrumentation model.
 * 
 * @author Noureddine Dahmane
 * @author Martin Armbruster
 */
class Java2ImChangePropagationSpecification extends AbstractChangePropagationSpecification {
	
	new() {
		super(new AdjustedJavaDomainProvider().domain, new InstrumentationModelDomainProvider().domain)
	}
	
	override propagateChange(EChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		if (doesHandleChange(change, correspondenceModel)) {
			val insertionChange = change as InsertEReference<?, ?>;
			executeJava2ImTransformation(correspondenceModel, userInteractor, insertionChange)
		}
	}
	
	override doesHandleChange(EChange change, CorrespondenceModel correspondenceModel) {
		if (!(change instanceof InsertEReference)) {
			return false;
		}
		val insertion = change as InsertEReference<?, ?>
		return insertion.newValue instanceof Method
	}
	
	private def executeJava2ImTransformation(CorrespondenceModel correspondenceModel,
		UserInteractor userInteracting, InsertEReference<?, ?> insertionChange) {
		
		val newMethod = insertionChange.newValue as Method;
		val correspondingSEFFs = CorrespondenceModelUtil.getCorrespondingEObjects(correspondenceModel, newMethod, ResourceDemandingSEFF)
		val im = CorrespondenceModelUtil.getCorrespondingEObjects(correspondenceModel,
			InstrumentationModelPackage.Literals.INSTRUMENTATION_MODEL, InstrumentationModel).last
		
		if (!correspondingSEFFs.empty) {
			val seff = correspondingSEFFs.last
			var sip = im.points.filter[it.service == seff].last
			if (sip !== null) {
				sip.actionInstrumentationPoints.clear
				InstrumentationModelUtil.recursiveBuildImm(seff, sip)
			} else {
				sip = InstrumentationModelUtil.recursiveBuildImm(seff)
				im.points.add(sip)
			}
		}
	}
}
