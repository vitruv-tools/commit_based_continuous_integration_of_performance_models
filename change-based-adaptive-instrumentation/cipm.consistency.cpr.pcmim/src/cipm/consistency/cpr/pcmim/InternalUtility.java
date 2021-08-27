package cipm.consistency.cpr.pcmim;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingInternalBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

/**
 * An internal utility class for the CPRs from the PCM to the extended IM.
 * 
 * @author Martin Armbruster
 */
public class InternalUtility {
	/**
	 * Finds the parent ResourceDemandingSEFF for a ResourceDemandingBehaviour which is a ResourceDemandingSEFF,
	 * not a ResourceDemandingInternalBehaviour, or not contained within a ResourceDemandingInternalBehaviour.
	 * 
	 * @param behaviour the ResourceDemandingBehaviour.
	 * @return the parent ResourceDemandingSEFF or null if it cannot be found, the behaviour is a
	 *         ResourceDemandingInternalBehaviour, or the behaviour is contained within a
	 *         ResourceDemandingInternalBehaviour.
	 */
	public static ResourceDemandingSEFF getParentSEFFNotForInternalBehaviour(ResourceDemandingBehaviour behaviour) {
		EObject parent = behaviour;
		while (parent != null && !(parent instanceof RepositoryComponent)
				&& !(parent instanceof ResourceDemandingInternalBehaviour)) {
			if (parent instanceof ResourceDemandingSEFF) {
				return (ResourceDemandingSEFF) parent;
			}
			parent = parent.eContainer();
		}
		return null;
	}
}
