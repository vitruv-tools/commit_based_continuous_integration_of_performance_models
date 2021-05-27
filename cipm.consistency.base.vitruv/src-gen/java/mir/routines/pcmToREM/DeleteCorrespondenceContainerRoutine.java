package mir.routines.pcmToREM;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class DeleteCorrespondenceContainerRoutine extends AbstractRepairRoutineRealization {
	private DeleteCorrespondenceContainerRoutine.ActionUserExecution userExecution;

	private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
		public ActionUserExecution(final ReactionExecutionState reactionExecutionState,
				final CallHierarchyHaving calledBy) {
			super(reactionExecutionState);
		}

		public EObject getElement1(final ResourceContainer container, final RuntimeResourceContainer corr,
				final RuntimeEnvironmentModel parent) {
			return container;
		}

		public EObject getElement2(final ResourceContainer container, final RuntimeResourceContainer corr,
				final RuntimeEnvironmentModel parent) {
			return corr;
		}

		public EObject getCorrepondenceSourceParent(final ResourceContainer container,
				final RuntimeResourceContainer corr) {
			EObject _eContainer = container.eContainer();
			return _eContainer;
		}

		public EObject getCorrepondenceSourceCorr(final ResourceContainer container) {
			return container;
		}

		public void callRoutine1(final ResourceContainer container, final RuntimeResourceContainer corr,
				final RuntimeEnvironmentModel parent, @Extension final RoutinesFacade _routinesFacade) {
			EList<RuntimeResourceContainer> _containers = parent.getContainers();
			_containers.remove(corr);
		}
	}

	public DeleteCorrespondenceContainerRoutine(final RoutinesFacade routinesFacade,
			final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy,
			final ResourceContainer container) {
		super(routinesFacade, reactionExecutionState, calledBy);
		this.userExecution = new mir.routines.pcmToREM.DeleteCorrespondenceContainerRoutine.ActionUserExecution(
				getExecutionState(), this);
		this.container = container;
	}

	private ResourceContainer container;

	protected boolean executeRoutine() throws IOException {
		getLogger().debug("Called routine DeleteCorrespondenceContainerRoutine with input:");
		getLogger().debug("   container: " + this.container);

		RuntimeResourceContainer corr = getCorrespondingElement(userExecution.getCorrepondenceSourceCorr(container), // correspondence
																														// source
																														// supplier
				RuntimeResourceContainer.class, (RuntimeResourceContainer _element) -> true, // correspondence
																								// precondition
																								// checker
				null, false // asserted
		);
		if (corr == null) {
			return false;
		}
		registerObjectUnderModification(corr);
		RuntimeEnvironmentModel parent = getCorrespondingElement(
				userExecution.getCorrepondenceSourceParent(container, corr), // correspondence source supplier
				RuntimeEnvironmentModel.class, (RuntimeEnvironmentModel _element) -> true, // correspondence
																							// precondition
																							// checker
				null, false // asserted
		);
		if (parent == null) {
			return false;
		}
		registerObjectUnderModification(parent);
		removeCorrespondenceBetween(userExecution.getElement1(container, corr, parent),
				userExecution.getElement2(container, corr, parent), "");

		userExecution.callRoutine1(container, corr, parent, this.getRoutinesFacade());

		postprocessElements();

		return true;
	}
}
