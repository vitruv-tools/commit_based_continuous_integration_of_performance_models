package cipm.consistency.base.vsum.domains;

import org.springframework.stereotype.Service;

import tools.vitruv.framework.domains.VitruvDomainProvider;

@Service
public class RuntimeEnvironmentDomainProvider implements VitruvDomainProvider<RuntimeEnvironmentDomain> {

	private static RuntimeEnvironmentDomain instance;

	@Override
	public RuntimeEnvironmentDomain getDomain() {
		if (instance == null) {
			instance = new RuntimeEnvironmentDomain();
		}
		return instance;
	}

}
