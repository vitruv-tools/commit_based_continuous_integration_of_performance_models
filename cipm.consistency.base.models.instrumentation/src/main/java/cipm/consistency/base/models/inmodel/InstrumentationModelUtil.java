package cipm.consistency.base.models.inmodel;

import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModel;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationModelFactory;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.InstrumentationType;
import cipm.consistency.base.models.instrumentation.InstrumentationModel.ServiceInstrumentationPoint;
import cipm.consistency.base.shared.ModelUtil;

public class InstrumentationModelUtil {

	public static void enrichInitialInstrumentationModel(InstrumentationModel imm, Repository repository) {
		for (ResourceDemandingSEFF service : ModelUtil.getObjects(repository, ResourceDemandingSEFF.class)) {
			ServiceInstrumentationPoint sip = InstrumentationModelFactory.eINSTANCE.createServiceInstrumentationPoint();
			sip.setActive(true);
			sip.setService(service);
			imm.getPoints().add(sip);

			recursiveBuildImm(service, sip);
		}
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
			}
		}
	}

}
