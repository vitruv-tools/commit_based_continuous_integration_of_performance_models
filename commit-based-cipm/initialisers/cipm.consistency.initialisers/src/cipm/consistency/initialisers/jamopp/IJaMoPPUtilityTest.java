package cipm.consistency.initialisers.jamopp;

import java.util.Collection;
import java.util.Set;

import cipm.consistency.initialisers.eobject.IEObjectUtilityTest;

/**
 * An interface that extends {@link IEObjectUtilityTest} with further utility
 * methods, focusing on EObject types used within JaMoPP.
 * 
 * @author Alp Torac Genc
 */
public interface IJaMoPPUtilityTest extends IEObjectUtilityTest {
	/**
	 * @return The {@link JaMoPPHelper} instance that will be used in other methods.
	 */
	public default JaMoPPHelper getJaMoPPHelper() {
		return new JaMoPPHelper();
	}

	/**
	 * {@link JaMoPPHelper#getAllPossibleTypes()}
	 */
	public default Set<Class<?>> getAllPossibleJaMoPPEObjectTypes() {
		return this.getJaMoPPHelper().getAllPossibleTypes();
	}

	/**
	 * {@link JaMoPPHelper#getAllInitialiserCandidates()}
	 */
	public default Collection<Class<?>> getAllInitialiserCandidates() {
		return this.getJaMoPPHelper().getAllInitialiserCandidates();
	}

	/**
	 * {@link JaMoPPHelper#getAllConcreteInitialiserCandidates()}
	 */
	public default Collection<Class<?>> getAllConcreteInitialiserCandidates() {
		return this.getJaMoPPHelper().getAllConcreteInitialiserCandidates();
	}
}
