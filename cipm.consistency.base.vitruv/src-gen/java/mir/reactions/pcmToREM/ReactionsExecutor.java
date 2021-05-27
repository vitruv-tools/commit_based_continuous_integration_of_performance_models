package mir.reactions.pcmToREM;

import cipm.consistency.base.vsum.domains.ExtendedPcmDomainProvider;
import cipm.consistency.base.vsum.domains.RuntimeEnvironmentDomainProvider;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsExecutor;
import tools.vitruv.extensions.dslsruntime.reactions.RoutinesFacadesProvider;
import tools.vitruv.extensions.dslsruntime.reactions.structure.ReactionsImportPath;

@SuppressWarnings("all")
class ReactionsExecutor extends AbstractReactionsExecutor {
	public ReactionsExecutor() {
		super(new ExtendedPcmDomainProvider().getDomain(), new RuntimeEnvironmentDomainProvider().getDomain());
	}

	protected RoutinesFacadesProvider createRoutinesFacadesProvider() {
		return new mir.routines.pcmToREM.RoutinesFacadesProvider();
	}

	protected void setup() {
		this.addReaction(new mir.reactions.pcmToREM.DeleteResourceContainerCorrespondenceReaction(
				this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("pcmToREM"))));
		this.addReaction(new mir.reactions.pcmToREM.DeleteLinkingResourceCorrespondenceReaction(
				this.getRoutinesFacadesProvider().getRoutinesFacade(ReactionsImportPath.fromPathString("pcmToREM"))));
	}
}
