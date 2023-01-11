package cipm.consistency.cpr.javapcm.additional.validation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.model.CorrespondenceModel;
import tools.vitruv.change.correspondence.view.CorrespondenceModelView;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.UserInteractionFactory;
//import tools.vitruv.dsls.reactions.runtime.helper.ReactionsCorrespondenceHelper;
//import tools.vitruv.changes.correspondence.model.CorrespondenceModel;
//import tools.vitruv.change.interaction.InternalUserInteractor;
//import tools.vitruv.change.interaction.UserInteractionFactory;

/**
 * Tries to find a target for all external calls which have no target.
 * 
 * @author Martin Armbruster
 */
public  class ExternalCallEmptyTargetFiller {
//	private CorrespondenceModel cm;
	private EditableCorrespondenceModelView<Correspondence> cModelView;
	private Repository repository;
	private Path collectionFile;

	/**
	 * Creates a new instance.
	 * 
	 * @param cModel     the current correspondence model.
	 * @param repo       the PCM repository to check.
	 * @param collection path to a collection of all external calls and their
	 *                   targets.
	 */
//	public ExternalCallEmptyTargetFiller(CorrespondenceModel cModel, Repository repo, Path collection) {
//		this.cm = cModel;
	public ExternalCallEmptyTargetFiller(EditableCorrespondenceModelView<Correspondence> cModelView, Repository repo, Path collection) {
		this.cModelView = cModelView;
		this.repository = repo;
		this.collectionFile = collection;
	}

	/**
	 * Tries to find the targets for all external calls without a target.
	 */
	public void fillExternalCalls() {
		var pairs = ExternalCallCallTargetPairCollectorReaderWriter.read(collectionFile);
		for (var comp : repository.getComponents__Repository()) {
			if (comp instanceof BasicComponent) {
				var basicComp = (BasicComponent) comp;
				for (var seff : basicComp.getServiceEffectSpecifications__BasicComponent()) {
					if (seff instanceof ResourceDemandingSEFF) {
						var rdseff = (ResourceDemandingSEFF) seff;
						for (var iter = rdseff.eAllContents(); iter.hasNext();) {
							var next = iter.next();
							if (next instanceof ExternalCallAction) {
								var extCall = (ExternalCallAction) next;
								if (extCall.getCalledService_ExternalService() == null) {
									fillExternalCall(pairs, basicComp, rdseff, extCall);
								}
							}
						}
					}
				}
			}
		}
		ExternalCallCallTargetPairCollectorReaderWriter.write(pairs, collectionFile);
		try {
			repository.eResource().save(null);
		} catch (IOException e) {
		}
	}

	/**
	 * Performs the actual finding of the external call target.
	 * 
	 * @param pairs     the collection of all stored external calls and their
	 *                  targets.
	 * @param component
	 * @param seff
	 * @param action
	 */
	private void fillExternalCall(ExternalCallCallTargetPairCollector pairs, RepositoryComponent component,
			ResourceDemandingSEFF seff, ExternalCallAction action) {
		// At first, look into the stored pairs if the external call is available.
		for (var p : pairs) {
			if (p.getComponentName().equals(component.getEntityName())
					&& p.getSeffName().equals(seff.getDescribedService__SEFF().getEntityName())
					&& p.getExternalCallEncoding().equals(encodeAbstractAction(action))) {
				// Search for the appropriate interface and service.
				for (var inter : repository.getInterfaces__Repository()) {
					if (inter.getEntityName().equals(p.getInterfaceName())
							&& inter instanceof OperationInterface) {
						var opInter = (OperationInterface) inter;
						for (var sig : opInter.getSignatures__OperationInterface()) {
							if (sig.getEntityName().equals(p.getServiceName())) {
								setCallTargetForExternalCall(component, action, sig);
								return;
							}
						}
					}
				}
			}
		}
		// Ask the developer to decide which existing service is the target of the
		// external call.
		ArrayList<OperationSignature> signatures = new ArrayList<>();
		repository.getInterfaces__Repository().stream().filter(i -> i instanceof OperationInterface)
				.map(i -> (OperationInterface) i)
				.forEach(i -> i.getSignatures__OperationInterface().forEach(signatures::add));
		ArrayList<String> signaturesRepr = new ArrayList<>();
		signatures.forEach(s -> signaturesRepr
				.add(s.getEntityName() + "(" + s.getInterface__OperationSignature().getEntityName() + ")"));
		InternalUserInteractor interactor = UserInteractionFactory.instance.createDialogUserInteractor();
		String message = "An external call (" + action.getEntityName()
			+ ") without a target was detected. Which service is the target?";
		int index = -1;
		int internalSteps = 9;
		for (int internalIdx = 0; internalIdx < signaturesRepr.size(); internalIdx += internalSteps) {
			ArrayList<String> options = new ArrayList<>();
			options.add("Next page.");
			for (int j = internalIdx; j < internalIdx + internalSteps && j < signaturesRepr.size(); j++) {
				options.add(signaturesRepr.get(j));
			}
			int potIndex = interactor.getSingleSelectionDialogBuilder()
					.message(message)
					.choices(options).startInteraction();
			if (potIndex != 0) {
				index = internalIdx + potIndex - 1;
				break;
			}
		}
		if (index != -1) {
			// Create a new pair for the external call and its target.
			var service = signatures.get(index);
			var newPair = new ExternalCallCallTargetPair();
			newPair.setComponentName(component.getEntityName());
			newPair.setSeffName(seff.getDescribedService__SEFF().getEntityName());
			newPair.setExternalCallEncoding(encodeAbstractAction(action));
			newPair.setServiceName(service.getEntityName());
			newPair.setInterfaceName(service.getInterface__OperationSignature().getEntityName());
			pairs.add(newPair);
			setCallTargetForExternalCall(component, action, service);
		}
	}

	/**
	 * Sets the target for an external call.
	 * 
	 * @param component component in which the external call is located.
	 * @param action    the external call.
	 * @param sign      the called service.
	 */
	private void setCallTargetForExternalCall(RepositoryComponent component, ExternalCallAction action,
			OperationSignature sign) {
		action.setCalledService_ExternalService(sign);
		// Check if there is a required role for the interface containing the service.
		for (var reqRole : component.getRequiredRoles_InterfaceRequiringEntity()) {
			if (reqRole instanceof OperationRequiredRole) {
				var opReqRole = (OperationRequiredRole) reqRole;
				if (opReqRole.getRequiredInterface__OperationRequiredRole() == sign
						.getInterface__OperationSignature()) {
					return;
				}
			}
		}
		// Create a new required role for the interface.
		OperationRequiredRole newRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
		newRole.setEntityName(
				component.getEntityName() + "_requires_"
				+ sign.getInterface__OperationSignature().getEntityName());
		newRole.setRequiredInterface__OperationRequiredRole(sign.getInterface__OperationSignature());
		newRole.setRequiringEntity_RequiredRole(component);
		component.getRequiredRoles_InterfaceRequiringEntity().add(newRole);
//		old and new usage:
//		ReactionsCorrespondenceHelper.addCorrespondence(cm, action, newRole, null);
		cModelView.addCorrespondenceBetween(Arrays.asList(action), Arrays.asList(newRole), null);
	}

	/**
	 * Encodes an AbstractAction based on its position.
	 * 
	 * @param action the action to encode.
	 * @return the encoding.
	 */
	private String encodeAbstractAction(AbstractAction action) {
		var parent = action.getResourceDemandingBehaviour_AbstractAction();
		int lastIndex = parent.getSteps_Behaviour().indexOf(action);
		if (!(parent.eContainer() instanceof RepositoryComponent)) {
			var container = parent.eContainer();
			while (!(container instanceof AbstractAction) && container != null) {
				container = container.eContainer();
			}
			if (container == null) {
				return Integer.toString(lastIndex);
			}
			return encodeAbstractAction((AbstractAction) container) + "-" + lastIndex;
		}
		return Integer.toString(lastIndex);
	}
}
