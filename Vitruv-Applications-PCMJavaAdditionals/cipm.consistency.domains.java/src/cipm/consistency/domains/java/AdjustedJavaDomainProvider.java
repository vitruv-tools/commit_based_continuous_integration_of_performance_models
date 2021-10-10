package cipm.consistency.domains.java;

import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

/**
 * Provider for the AdjustedJavaDomain.
 * 
 * @author Martin Armbruster
 */
public class AdjustedJavaDomainProvider implements VitruvDomainProvider<VitruvDomain> {
	private static AdjustedJavaDomain dom;

	@Override
	public AdjustedJavaDomain getDomain() {
		if (dom == null) {
			dom = new AdjustedJavaDomain();
		}
		return dom;
	}
}
