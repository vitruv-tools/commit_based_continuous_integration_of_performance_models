package cipm.consistency.cpr.javaim

import org.emftext.language.java.members.Method
import tools.vitruv.framework.correspondence.CorrespondenceModel
import tools.vitruv.framework.userinteraction.UserInteractor
import tools.vitruv.framework.propagation.impl.AbstractChangePropagationSpecification
import tools.vitruv.framework.change.echange.EChange
import tools.vitruv.framework.propagation.ResourceAccess
import cipm.consistency.base.vsum.domains.InstrumentationModelDomainProvider
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel
import cipm.consistency.base.models.inmodel.InstrumentationModelUtil
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelPackage
import tools.vitruv.framework.change.echange.feature.attribute.ReplaceSingleValuedEAttribute
import org.emftext.language.java.commons.CommonsPackage
import cipm.consistency.domains.java.AdjustedJavaDomainProvider

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
			val attrChange = change as ReplaceSingleValuedEAttribute<?, ?>;
			val meth = attrChange.affectedEObject as Method;
			executeJava2ImTransformation(correspondenceModel, userInteractor, meth)
		}
	}
	
	override doesHandleChange(EChange change, CorrespondenceModel correspondenceModel) {
		if (!(change instanceof ReplaceSingleValuedEAttribute)) {
			return false;
		}
		val attrChange = change as ReplaceSingleValuedEAttribute<?, ?>;
		return attrChange.affectedEObject instanceof Method
			&& attrChange.affectedFeature == CommonsPackage.Literals.NAMED_ELEMENT__NAME
			&& !attrChange.newValue.equals("")
	}
	
	private def executeJava2ImTransformation(CorrespondenceModel correspondenceModel,
		UserInteractor userInteracting, Method newMethod) {
		
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
