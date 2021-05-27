package mir.routines.remToPCM;

import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainer;
import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class SyncHostConnectionCreationRoutine extends AbstractRepairRoutineRealization {
	private SyncHostConnectionCreationRoutine.ActionUserExecution userExecution;

	private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
		public ActionUserExecution(final ReactionExecutionState reactionExecutionState,
				final CallHierarchyHaving calledBy) {
			super(reactionExecutionState);
		}

		public EObject getCorrepondenceSourceContainerFrom(final RuntimeResourceContainerConnection affectedObject) {
			RuntimeResourceContainer _containerFrom = affectedObject.getContainerFrom();
			return _containerFrom;
		}

		public EObject getCorrepondenceSource1(final RuntimeResourceContainerConnection affectedObject) {
			return affectedObject;
		}

		public EObject getCorrepondenceSourceContainerTo(final RuntimeResourceContainerConnection affectedObject,
				final ResourceContainer containerFrom) {
			RuntimeResourceContainer _containerTo = affectedObject.getContainerTo();
			return _containerTo;
		}

		public EObject getCorrepondenceSourceParent(final RuntimeResourceContainerConnection affectedObject,
				final ResourceContainer containerFrom, final ResourceContainer containerTo) {
			EObject _eContainer = affectedObject.eContainer();
			return _eContainer;
		}

		public void callRoutine1(final RuntimeResourceContainerConnection affectedObject,
				final ResourceContainer containerFrom, final ResourceContainer containerTo,
				final ResourceEnvironment parent, @Extension final RoutinesFacade _routinesFacade) {
			if ((((containerFrom != null) && (containerTo != null)) && (parent != null))) {
				final Function1<LinkingResource, Boolean> _function = new Function1<LinkingResource, Boolean>() {
					public Boolean apply(final LinkingResource it) {
						return Boolean
								.valueOf((it.getConnectedResourceContainers_LinkingResource().contains(containerFrom)
										&& it.getConnectedResourceContainers_LinkingResource().contains(containerTo)));
					}
				};
				final LinkingResource corrLink = IterableExtensions
						.<LinkingResource>findFirst(parent.getLinkingResources__ResourceEnvironment(), _function);
				if ((corrLink == null)) {
					_routinesFacade.buildLinkAndMap(affectedObject, parent, containerFrom, containerTo);
				} else {
					_routinesFacade.createCorrespondenceLinks(affectedObject, corrLink);
				}
			}
		}
	}

	public SyncHostConnectionCreationRoutine(final RoutinesFacade routinesFacade,
			final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy,
			final RuntimeResourceContainerConnection affectedObject) {
		super(routinesFacade, reactionExecutionState, calledBy);
		this.userExecution = new mir.routines.remToPCM.SyncHostConnectionCreationRoutine.ActionUserExecution(
				getExecutionState(), this);
		this.affectedObject = affectedObject;
	}

	private RuntimeResourceContainerConnection affectedObject;

	protected boolean executeRoutine() throws IOException {
		getLogger().debug("Called routine SyncHostConnectionCreationRoutine with input:");
		getLogger().debug("   affectedObject: " + this.affectedObject);

		if (!getCorrespondingElements(userExecution.getCorrepondenceSource1(affectedObject), // correspondence source
																								// supplier
				org.palladiosimulator.pcm.resourceenvironment.LinkingResource.class,
				(org.palladiosimulator.pcm.resourceenvironment.LinkingResource _element) -> true, // correspondence
																									// precondition
																									// checker
				null).isEmpty()) {
			return false;
		}
		org.palladiosimulator.pcm.resourceenvironment.ResourceContainer containerFrom = getCorrespondingElement(
				userExecution.getCorrepondenceSourceContainerFrom(affectedObject), // correspondence source supplier
				org.palladiosimulator.pcm.resourceenvironment.ResourceContainer.class,
				(org.palladiosimulator.pcm.resourceenvironment.ResourceContainer _element) -> true, // correspondence
																									// precondition
																									// checker
				null, false // asserted
		);
		if (containerFrom == null) {
			return false;
		}
		registerObjectUnderModification(containerFrom);
		org.palladiosimulator.pcm.resourceenvironment.ResourceContainer containerTo = getCorrespondingElement(
				userExecution.getCorrepondenceSourceContainerTo(affectedObject, containerFrom), // correspondence source
																								// supplier
				org.palladiosimulator.pcm.resourceenvironment.ResourceContainer.class,
				(org.palladiosimulator.pcm.resourceenvironment.ResourceContainer _element) -> true, // correspondence
																									// precondition
																									// checker
				null, false // asserted
		);
		if (containerTo == null) {
			return false;
		}

		registerObjectUnderModification(containerTo);
		org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment parent = getCorrespondingElement(
				userExecution.getCorrepondenceSourceParent(affectedObject, containerFrom, containerTo), // correspondence
																										// source
																										// supplier
				org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment.class,
				(org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment _element) -> true, // correspondence
																										// precondition
																										// checker
				null, false // asserted
		);
		if (parent == null) {
			return false;
		}
		registerObjectUnderModification(parent);

		userExecution.callRoutine1(affectedObject, containerFrom, containerTo, parent, this.getRoutinesFacade());

		postprocessElements();

		return true;
	}
}
