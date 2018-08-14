package tools.vitruv.applications.pcmjava.linkingintegration.change2command

import tools.vitruv.framework.correspondence.CorrespondenceModel
import java.util.ArrayList
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.internal.IntegrationChange2CommandTransformer
import tools.vitruv.framework.change.description.TransactionalChange
import tools.vitruv.framework.change.description.CompositeTransactionalChange
import tools.vitruv.framework.change.description.ConcreteChange
import tools.vitruv.framework.change.processing.impl.AbstractChangePropagationSpecification
import tools.vitruv.domains.java.JavaDomainProvider
import tools.vitruv.domains.pcm.PcmDomainProvider
import tools.vitruv.framework.util.command.ResourceAccess

class CodeIntegrationChangeProcessor extends AbstractChangePropagationSpecification {
	private val IntegrationChange2CommandTransformer integrationTransformer;
	
	new() {
		super(new JavaDomainProvider().domain, new PcmDomainProvider().domain);
		this.integrationTransformer = new IntegrationChange2CommandTransformer(userInteractor);
	}
	
	override doesHandleChange(TransactionalChange change, CorrespondenceModel correspondenceModel) {
		return true;
	}
	
	override propagateChange(TransactionalChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		change.performIntegration(correspondenceModel, resourceAccess);
	}
	
	def dispatch boolean performIntegration(CompositeTransactionalChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		val integratedChanges = new ArrayList<TransactionalChange>();
		var performedIntegration = true; 
		for (innerChange : change.changes) {
			val integrationResult = innerChange.performIntegration(correspondenceModel, resourceAccess);
			if (integrationResult) {
				integratedChanges += innerChange;
			} else {
				performedIntegration = false;
			}
		}
		for (integratedChange : integratedChanges) {
			change.removeChange(integratedChange);
		}
		return performedIntegration;
	}
	
	def dispatch boolean performIntegration(ConcreteChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		// Special behavior for changes to integrated elements
		return integrationTransformer.compute(change, correspondenceModel, resourceAccess);
//		} else {
//			nonIntegratedEChanges += eChange;
//		}
//		val resultingChange = if (nonIntegratedEChanges.isEmpty) {
//			VitruviusChangeFactory.instance.createEmptyChange(change.getURI);
//		} else if (nonIntegratedEChanges.size == 1) {
//			VitruviusChangeFactory.instance.createConcreteChange(nonIntegratedEChanges.get(0), change.getURI);
//		} else {
//			val transactionalChange = VitruviusChangeFactory.instance.createCompositeTransactionalChange();
//			for (eChange : nonIntegratedEChanges) {
//				transactionalChange.addChange(VitruviusChangeFactory.instance.createConcreteChange(eChange, change.getURI));
//			}
//			transactionalChange;
//		}
//		
//		return new ChangeProcessorResult(resultingChange, commands);
	}
	
}
