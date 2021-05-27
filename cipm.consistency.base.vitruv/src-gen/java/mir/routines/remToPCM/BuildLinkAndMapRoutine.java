package mir.routines.remToPCM;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.CommunicationLinkResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

import cipm.consistency.base.models.runtimeenvironment.REModel.RuntimeResourceContainerConnection;
import cipm.consistency.base.vsum.util.ReactionHelper;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class BuildLinkAndMapRoutine extends AbstractRepairRoutineRealization {
	private BuildLinkAndMapRoutine.ActionUserExecution userExecution;

	private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
		public ActionUserExecution(final ReactionExecutionState reactionExecutionState,
				final CallHierarchyHaving calledBy) {
			super(reactionExecutionState);
		}

		public void updateThroughputStoexElement(final RuntimeResourceContainerConnection conn,
				final ResourceEnvironment parent, final ResourceContainer containerFrom,
				final ResourceContainer containerTo, final PCMRandomVariable latencyStoex,
				final PCMRandomVariable throughputStoex) {
			throughputStoex.setSpecification(String.valueOf(Math.pow(10, 6)));
		}

		public void updateLatencyStoexElement(final RuntimeResourceContainerConnection conn,
				final ResourceEnvironment parent, final ResourceContainer containerFrom,
				final ResourceContainer containerTo, final PCMRandomVariable latencyStoex) {
			latencyStoex.setSpecification("0");
		}

		public void updateNCommunicationSpecElement(final RuntimeResourceContainerConnection conn,
				final ResourceEnvironment parent, final ResourceContainer containerFrom,
				final ResourceContainer containerTo, final PCMRandomVariable latencyStoex,
				final PCMRandomVariable throughputStoex,
				final CommunicationLinkResourceSpecification nCommunicationSpec) {
			nCommunicationSpec.setFailureProbability(0);
			nCommunicationSpec.setLatency_CommunicationLinkResourceSpecification(latencyStoex);
			nCommunicationSpec.setThroughput_CommunicationLinkResourceSpecification(throughputStoex);
			nCommunicationSpec.setCommunicationLinkResourceType_CommunicationLinkResourceSpecification(
					ReactionHelper.getLANCommunicationResourceType());
		}

		public void updateNLinkElement(final RuntimeResourceContainerConnection conn, final ResourceEnvironment parent,
				final ResourceContainer containerFrom, final ResourceContainer containerTo,
				final PCMRandomVariable latencyStoex, final PCMRandomVariable throughputStoex,
				final CommunicationLinkResourceSpecification nCommunicationSpec, final LinkingResource nLink) {
			nLink.getConnectedResourceContainers_LinkingResource().add(containerFrom);
			nLink.getConnectedResourceContainers_LinkingResource().add(containerTo);
			String _entityName = containerFrom.getEntityName();
			String _plus = (_entityName + "<->");
			String _entityName_1 = containerTo.getEntityName();
			String _plus_1 = (_plus + _entityName_1);
			nLink.setEntityName(_plus_1);
			nLink.setCommunicationLinkResourceSpecifications_LinkingResource(nCommunicationSpec);
		}

		public void callRoutine1(final RuntimeResourceContainerConnection conn, final ResourceEnvironment parent,
				final ResourceContainer containerFrom, final ResourceContainer containerTo,
				final PCMRandomVariable latencyStoex, final PCMRandomVariable throughputStoex,
				final CommunicationLinkResourceSpecification nCommunicationSpec, final LinkingResource nLink,
				@Extension final RoutinesFacade _routinesFacade) {
			EList<LinkingResource> _linkingResources__ResourceEnvironment = parent
					.getLinkingResources__ResourceEnvironment();
			_linkingResources__ResourceEnvironment.add(nLink);
			_routinesFacade.createCorrespondenceLinks(conn, nLink);
		}
	}

	public BuildLinkAndMapRoutine(final RoutinesFacade routinesFacade,
			final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy,
			final RuntimeResourceContainerConnection conn, final ResourceEnvironment parent,
			final ResourceContainer containerFrom, final ResourceContainer containerTo) {
		super(routinesFacade, reactionExecutionState, calledBy);
		this.userExecution = new mir.routines.remToPCM.BuildLinkAndMapRoutine.ActionUserExecution(getExecutionState(),
				this);
		this.conn = conn;
		this.parent = parent;
		this.containerFrom = containerFrom;
		this.containerTo = containerTo;
	}

	private RuntimeResourceContainerConnection conn;

	private ResourceEnvironment parent;

	private ResourceContainer containerFrom;

	private ResourceContainer containerTo;

	protected boolean executeRoutine() throws IOException {
		getLogger().debug("Called routine BuildLinkAndMapRoutine with input:");
		getLogger().debug("   conn: " + this.conn);
		getLogger().debug("   parent: " + this.parent);
		getLogger().debug("   containerFrom: " + this.containerFrom);
		getLogger().debug("   containerTo: " + this.containerTo);

		org.palladiosimulator.pcm.core.PCMRandomVariable latencyStoex = org.palladiosimulator.pcm.core.impl.CoreFactoryImpl.eINSTANCE
				.createPCMRandomVariable();
		notifyObjectCreated(latencyStoex);
		userExecution.updateLatencyStoexElement(conn, parent, containerFrom, containerTo, latencyStoex);

		org.palladiosimulator.pcm.core.PCMRandomVariable throughputStoex = org.palladiosimulator.pcm.core.impl.CoreFactoryImpl.eINSTANCE
				.createPCMRandomVariable();
		notifyObjectCreated(throughputStoex);
		userExecution.updateThroughputStoexElement(conn, parent, containerFrom, containerTo, latencyStoex,
				throughputStoex);

		org.palladiosimulator.pcm.resourceenvironment.CommunicationLinkResourceSpecification nCommunicationSpec = org.palladiosimulator.pcm.resourceenvironment.impl.ResourceenvironmentFactoryImpl.eINSTANCE
				.createCommunicationLinkResourceSpecification();
		notifyObjectCreated(nCommunicationSpec);
		userExecution.updateNCommunicationSpecElement(conn, parent, containerFrom, containerTo, latencyStoex,
				throughputStoex, nCommunicationSpec);

		org.palladiosimulator.pcm.resourceenvironment.LinkingResource nLink = org.palladiosimulator.pcm.resourceenvironment.impl.ResourceenvironmentFactoryImpl.eINSTANCE
				.createLinkingResource();
		notifyObjectCreated(nLink);
		userExecution.updateNLinkElement(conn, parent, containerFrom, containerTo, latencyStoex, throughputStoex,
				nCommunicationSpec, nLink);

		userExecution.callRoutine1(conn, parent, containerFrom, containerTo, latencyStoex, throughputStoex,
				nCommunicationSpec, nLink, this.getRoutinesFacade());

		postprocessElements();

		return true;
	}
}
