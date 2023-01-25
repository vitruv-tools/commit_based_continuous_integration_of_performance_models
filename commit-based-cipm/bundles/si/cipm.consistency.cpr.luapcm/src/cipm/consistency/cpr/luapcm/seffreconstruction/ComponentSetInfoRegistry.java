package cipm.consistency.cpr.luapcm.seffreconstruction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.xtext.lua.lua.ComponentSet;

/**
 * This class is used to store and retrieve infos about component sets.
 * 
 * @author Lukas Burgey
 *
 */
public class ComponentSetInfoRegistry {
    
    private Map<ComponentSet, ComponentSetInfo> setToInfos;
    
    public ComponentSetInfoRegistry() {
        setToInfos = new HashMap<>();
    }
    
    public ComponentSetInfo getInfosForComponentSet(ComponentSet componentSet) {
        if (setToInfos.containsKey(componentSet)) {
            return setToInfos.get(componentSet);
        }
        var newInfos = new ComponentSetInfo(componentSet);
        setToInfos.put(componentSet, newInfos);
        return newInfos;
    }

    public ComponentSetInfo getInfosForComponentSet(EObject eObj) {
        var componentSet = EcoreUtil2.getContainerOfType(eObj, ComponentSet.class);
        return getInfosForComponentSet(componentSet);
    }
}
