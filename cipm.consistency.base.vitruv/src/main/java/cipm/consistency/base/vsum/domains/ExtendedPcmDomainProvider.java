package cipm.consistency.base.vsum.domains;

import org.springframework.stereotype.Service;

import tools.vitruv.framework.domains.VitruvDomainProvider;

@Service
public class ExtendedPcmDomainProvider implements VitruvDomainProvider<ExtendedPcmDomain> {
	private static ExtendedPcmDomain domain;

	@Override
	public ExtendedPcmDomain getDomain() {
		if (domain == null) {
			domain = new ExtendedPcmDomain();
		}
		return domain;
	}

}
