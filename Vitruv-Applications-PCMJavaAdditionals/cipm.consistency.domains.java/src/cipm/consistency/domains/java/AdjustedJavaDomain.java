package cipm.consistency.domains.java;

import cipm.consistency.domains.java.propagation.JavaStateBasedChangeResolutionStrategy;
import tools.vitruv.domains.java.JavaDomain;
import tools.vitruv.framework.domains.StateBasedChangeResolutionStrategy;

/**
 * An adjusted Java domain that uses the JavaStateBasedChangeResolutionStrategy.
 * 
 * @author Martin Armbruster
 */
public class AdjustedJavaDomain extends JavaDomain {
	private StateBasedChangeResolutionStrategy internalStrategy =
			new JavaStateBasedChangeResolutionStrategy();
	
	public AdjustedJavaDomain() {
		super("AdjustedJava");
	}
	
	@Override
	public StateBasedChangeResolutionStrategy getStateChangePropagationStrategy() {
		return internalStrategy;
	}
}
