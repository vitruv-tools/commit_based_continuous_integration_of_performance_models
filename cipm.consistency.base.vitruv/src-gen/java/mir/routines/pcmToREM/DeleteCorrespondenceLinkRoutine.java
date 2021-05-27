package mir.routines.pcmToREM;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeEnvironmentModel;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class DeleteCorrespondenceLinkRoutine extends AbstractRepairRoutineRealization {
	private DeleteCorrespondenceLinkRoutine.ActionUserExecution userExecution;

	private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
		public ActionUserExecution(final ReactionExecutionState reactionExecutionState,
				final CallHierarchyHaving calledBy) {
			super(reactionExecutionState);
		}

		public EObject getElement1(final LinkingResource link, final RuntimeResourceContainerConnection corr,
				final RuntimeEnvironmentModel parent) {
			return link;
		}

		public EObject getElement2(final LinkingResource link, final RuntimeResourceContainerConnection corr,
				final RuntimeEnvironmentModel parent) {
			return corr;
		}

		public EObject getCorrepondenceSourceParent(final LinkingResource link,
				final RuntimeResourceContainerConnection corr) {
			EObject _eContainer = link.eContainer();
			return _eContainer;
		}

		public EObject getCorrepondenceSourceCorr(final LinkingResource link) {
			return link;
		}

		public void callRoutine1(final LinkingResource link, final RuntimeResourceContainerConnection corr,
				final RuntimeEnvironmentModel parent, @Extension final RoutinesFacade _routinesFacade) {
			EList<RuntimeResourceContainerConnection> _connections = parent.getConnections();
			_connections.remove(corr);
		}
	}

	public DeleteCorrespondenceLinkRoutine(final RoutinesFacade routinesFacade,
			final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy,
			final LinkingResource link) {
		super(routinesFacade, reactionExecutionState, calledBy);
		this.userExecution = new mir.routines.pcmToREM.DeleteCorrespondenceLinkRoutine.ActionUserExecution(
				getExecutionState(), this);
		this.link = link;
	}

	private LinkingResource link;

	protected boolean executeRoutine() throws IOException {
		getLogger().debug("Called routine DeleteCorrespondenceLinkRoutine with input:");
		getLogger().debug("   link: " + this.link);

		RuntimeResourceContainerConnection corr = getCorrespondingElement(
				userExecution.getCorrepondenceSourceCorr(link), // correspondence source supplier
				RuntimeResourceContainerConnection.class, (RuntimeResourceContainerConnection _element) -> true, // correspondence
																													// precondition
																													// checker
				null, false // asserted
		);
		if (corr == null) {
			return false;
		}
		registerObjectUnderModification(corr);
		RuntimeEnvironmentModel parent = getCorrespondingElement(userExecution.getCorrepondenceSourceParent(link, corr), // correspondence
																															// source
																															// supplier
				RuntimeEnvironmentModel.class, (RuntimeEnvironmentModel _element) -> true, // correspondence
																							// precondition
																							// checker
				null, false // asserted
		);
		if (parent == null) {
			return false;
		}
		registerObjectUnderModification(parent);
		removeCorrespondenceBetween(userExecution.getElement1(link, corr, parent),
				userExecution.getElement2(link, corr, parent), "");

		userExecution.callRoutine1(link, corr, parent, this.getRoutinesFacade());

		postprocessElements();

		return true;
	}
}
