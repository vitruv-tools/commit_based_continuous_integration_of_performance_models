package mir.reactions.pcmToREM;

import org.springframework.stereotype.Service;

import cipm.consistency.base.vsum.domains.ExtendedPcmDomainProvider;
import cipm.consistency.base.vsum.domains.RuntimeEnvironmentDomainProvider;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;

@SuppressWarnings("all")
@Service
public class PcmToREMChangePropagationSpecification extends AbstractReactionsChangePropagationSpecification
		implements ChangePropagationSpecification {
	public PcmToREMChangePropagationSpecification() {
		super(new ExtendedPcmDomainProvider().getDomain(), new RuntimeEnvironmentDomainProvider().getDomain());
	}

	protected void setup() {
		this.addChangeMainprocessor(new mir.reactions.pcmToREM.ReactionsExecutor());
	}
}
