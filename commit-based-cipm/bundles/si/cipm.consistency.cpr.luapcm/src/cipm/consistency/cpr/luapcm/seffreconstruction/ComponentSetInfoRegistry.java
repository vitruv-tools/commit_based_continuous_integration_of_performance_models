package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.xtext.lua.lua.ComponentSet;

/**
 * This singleton class is used to store and retrieve infos about component sets.
 * 
 * @author Lukas Burgey
 *
 */
public final class ComponentSetInfoRegistry {

    // singleton info map
    private static Map<ComponentSet, ComponentSetInfo> setToInfos;
    
    private static Map<ComponentSet, ComponentSetInfo> getInfos() {
       if (setToInfos == null) {
           setToInfos = new HashMap<>();
       }
       return setToInfos;
    }
    
    public static ComponentSetInfo getInfosForComponentSet(ComponentSet componentSet) {
        if (getInfos().containsKey(componentSet)) {
            return getInfos().get(componentSet);
        }
        var newInfos = new ComponentSetInfo(componentSet);
        getInfos().put(componentSet, newInfos);
        return newInfos;
    }

    public static ComponentSetInfo getInfosForComponentSet(EObject eObj) {
        var componentSet = EcoreUtil2.getContainerOfType(eObj, ComponentSet.class);
        return getInfosForComponentSet(componentSet);
    }
}
