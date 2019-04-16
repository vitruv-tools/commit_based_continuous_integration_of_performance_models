package tools.vitruv.domains.im

import tools.vitruv.framework.domains.VitruvDomainProvider

class ImDomainProvider implements VitruvDomainProvider<ImDomain> {
	private static ImDomain instance;
	
	override ImDomain getDomain() {
		if (instance === null) {
			instance = new ImDomain();
		}
		return instance;
	}

	
}