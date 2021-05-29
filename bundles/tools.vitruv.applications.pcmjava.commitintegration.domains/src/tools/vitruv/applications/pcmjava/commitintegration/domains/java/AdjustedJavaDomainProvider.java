package tools.vitruv.applications.pcmjava.commitintegration.domains.java;

import tools.vitruv.framework.domains.VitruvDomainProvider;

public class AdjustedJavaDomainProvider implements VitruvDomainProvider<AdjustedJavaDomain> {
	private static AdjustedJavaDomain dom;
	
	@Override
	public AdjustedJavaDomain getDomain() {
		if(dom==null) {
			dom = new AdjustedJavaDomain();
		}
		return dom;
	}
}
