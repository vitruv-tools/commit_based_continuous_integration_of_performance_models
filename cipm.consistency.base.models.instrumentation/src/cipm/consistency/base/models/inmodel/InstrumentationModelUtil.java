package cipm.consistency.base.models.inmodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationType;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;

public class InstrumentationModelUtil {

	public static void enrichInitialInstrumentationModel(InstrumentationModel imm, Repository repository) {
		for (ResourceDemandingSEFF service : getObjects(repository, ResourceDemandingSEFF.class)) {
			imm.getPoints().add(recursiveBuildImm(service));
		}
	}
	
	/**
	 * Gets all objects of a given type within a model.
	 * 
	 * @param <T>      type of the objects
	 * @param pcmModel the model
	 * @param type     the class corresponding to the type of the objects
	 * @return a list of all objects within the given model which conform to the
	 *         given type
	 */
	@SuppressWarnings("unchecked")
	private static <T extends EObject> List<T> getObjects(final EObject pcmModel, final Class<T> type) {
		List<T> results = new ArrayList<>();
		TreeIterator<EObject> it = pcmModel.eAllContents();
		while (it.hasNext()) {
			EObject eo = it.next();
			if (type.isInstance(eo)) {
				results.add((T) eo);
			}
		}
		return results;
	}
	
	public static ServiceInstrumentationPoint recursiveBuildImm(ResourceDemandingSEFF service) {
		ServiceInstrumentationPoint sip = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
		sip.setActive(true);
		sip.setService(service);

		recursiveBuildImm(service, sip);
		
		return sip;
	}

	public static void recursiveBuildImm(ResourceDemandingBehaviour service, ServiceInstrumentationPoint sip) {
		for (AbstractAction action : service.getSteps_Behaviour()) {
			if (action instanceof LoopAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.LOOP);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);

				recursiveBuildImm(((LoopAction) action).getBodyBehaviour_Loop(), sip);
			} else if (action instanceof BranchAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.BRANCH);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);

				((BranchAction) action).getBranches_Branch().stream().forEach(branch -> {
					recursiveBuildImm(branch.getBranchBehaviour_BranchTransition(), sip);
				});
			} else if (action instanceof InternalAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.INTERNAL);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);
			} else if (action instanceof ExternalCallAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.EXTERNAL_CALL);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);
			} else if (action instanceof InternalCallAction) {
				ActionInstrumentationPoint inner = InstrumentationModelFactory.eINSTANCE
						.createActionInstrumentationPoint();
				inner.setType(InstrumentationType.INTERNAL_CALL);
				inner.setAction(action);
				inner.setActive(true);
				sip.getActionInstrumentationPoints().add(inner);
			}
		}
	}
}
