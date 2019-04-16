package tools.vitruv.applications.javaim.modelrefinement

import org.emftext.language.java.members.Method
import tools.vitruv.domains.im.ImDomainProvider
import tools.vitruv.domains.java.JavaDomainProvider
import tools.vitruv.domains.java.echange.feature.JavaFeatureEChange
import tools.vitruv.framework.change.description.CompositeTransactionalChange
import tools.vitruv.framework.change.description.ConcreteChange
import tools.vitruv.framework.change.description.TransactionalChange
import tools.vitruv.framework.change.processing.impl.AbstractChangePropagationSpecification
import tools.vitruv.framework.correspondence.CorrespondenceModel
import tools.vitruv.framework.userinteraction.UserInteractor
import tools.vitruv.framework.util.command.ResourceAccess

class Java2ImMethodBodyChangeTransformation extends AbstractChangePropagationSpecification {
	new() {
		super(new JavaDomainProvider().domain, new ImDomainProvider().domain)
	}
	
	
	override propagateChange(TransactionalChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		if (doesHandleChange(change, correspondenceModel)) {
			val compositeChange = change as CompositeTransactionalChange;
			executeJava2ImTransformation(correspondenceModel, userInteractor, compositeChange)
		}
		
	}
	
	
	override doesHandleChange(TransactionalChange change, CorrespondenceModel correspondenceModel) {
		if (!(change instanceof CompositeTransactionalChange)) {
			return false;
		}
		else{
			return true;
		} 
		
	}
	
	
	def executeJava2ImTransformation(CorrespondenceModel correspondenceModel,
		UserInteractor userInteracting, CompositeTransactionalChange compositeChange){
			
		val ConcreteChange emfChange = compositeChange.getChanges().get(0) as ConcreteChange;
		val JavaFeatureEChange<?, ?> eFeatureChange = emfChange.getEChanges().get(0) as JavaFeatureEChange<?, ?>;
		
		val oldMethod = eFeatureChange.getOldAffectedEObject() as Method;
		val newMethod = eFeatureChange.getAffectedEObject() as Method;
		
		Java2ImMethodChangeTransformationUtil.execute(correspondenceModel, oldMethod, newMethod)	

	}
	
	
}