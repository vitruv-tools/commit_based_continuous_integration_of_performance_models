package cipm.consistency.base.vsum.domains;

import org.springframework.stereotype.Service;

import tools.vitruv.framework.domains.VitruvDomainProvider;

@Service
public class InstrumentationModelDomainProvider implements VitruvDomainProvider<InstrumentationModelDomain> {
	private static InstrumentationModelDomain instance;

	@Override
	public InstrumentationModelDomain getDomain() {
		if (instance == null) {
			instance = new InstrumentationModelDomain();
		}
		return instance;
	}

}
