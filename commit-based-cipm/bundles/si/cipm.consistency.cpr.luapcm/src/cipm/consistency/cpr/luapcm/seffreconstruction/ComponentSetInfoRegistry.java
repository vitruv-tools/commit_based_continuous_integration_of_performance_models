package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.xtext.lua.lua.ComponentSet;

import cipm.consistency.commitintegration.lang.lua.runtimedata.ChangedResources;

/**
 * This singleton class is used to store and retrieve infos about component sets.
 * 
 * @author Lukas Burgey
 *
 */
public final class ComponentSetInfoRegistry {

    // singleton info map
    private static Map<ComponentSet, ComponentSetInfo> uriToInfos = new HashMap<>();
    
    public static ComponentSetInfo getInfosForComponentSet(ComponentSet componentSet) {
        if (ChangedResources.getAndResetResourcesWereChanged()) {
            uriToInfos = new HashMap<>();
        }
        
        if (uriToInfos.containsKey(componentSet)) {
            return uriToInfos.get(componentSet);
        }

        var newInfos = new ComponentSetInfo(componentSet);
        uriToInfos.put(componentSet, newInfos);
        return newInfos;
    }

    public static ComponentSetInfo getInfosForComponentSet(EObject eObj) {
        var componentSet = EcoreUtil2.getContainerOfType(eObj, ComponentSet.class);
        return getInfosForComponentSet(componentSet);
    }
}
