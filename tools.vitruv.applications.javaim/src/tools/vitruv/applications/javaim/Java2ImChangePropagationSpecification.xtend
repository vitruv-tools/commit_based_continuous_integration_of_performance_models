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
import tools.vitruv.applications.pcmjava.commitintegration.propagation.OldMethodAdapter

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
		val oldMethod = newMethod.eAdapters.filter(OldMethodAdapter).last?.oldMethod
		
		Java2ImMethodChangeTransformationUtil.execute(correspondenceModel, oldMethod, newMethod)
	}
}
