package cipm.consistency.cpr.luapcm.internal;

import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.NamedChunk;

/**
 * An internal utility class for the CPRs from the PCM to the extended IM.
 * 
 * @author Lukas Burgey
 */
public class InternalUtils {
//	/**
//	 * Finds the parent ResourceDemandingSEFF for a ResourceDemandingBehaviour which is a ResourceDemandingSEFF,
//	 * not a ResourceDemandingInternalBehaviour, or not contained within a ResourceDemandingInternalBehaviour.
//	 * 
//	 * @param behaviour the ResourceDemandingBehaviour.
//	 * @return the parent ResourceDemandingSEFF or null if it cannot be found, the behaviour is a
//	 *         ResourceDemandingInternalBehaviour, or the behaviour is contained within a
//	 *         ResourceDemandingInternalBehaviour.
//	 */
//	public static ResourceDemandingSEFF getParentSEFFNotForInternalBehaviour(ResourceDemandingBehaviour behaviour) {
//		EObject parent = behaviour;
//		while (parent != null && !(parent instanceof RepositoryComponent)
//				&& !(parent instanceof ResourceDemandingInternalBehaviour)) {
//			if (parent instanceof ResourceDemandingSEFF) {
//				return (ResourceDemandingSEFF) parent;
//			}
//			parent = parent.eContainer();
//		}
//		return null;
//	}

    public static Component getComponentOfChunk(Chunk chunk) {
        var parent = chunk.eContainer();
        if (parent instanceof NamedChunk) {
            var grandparent = parent.eContainer();
            if (grandparent instanceof Component) {
                return (Component) grandparent;
            }
        }
        return null;
    }
}
