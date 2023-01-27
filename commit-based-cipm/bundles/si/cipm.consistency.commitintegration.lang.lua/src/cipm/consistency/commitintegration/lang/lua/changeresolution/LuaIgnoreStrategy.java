package cipm.consistency.commitintegration.lang.lua.changeresolution;

import org.eclipse.emf.ecore.EObject;
import org.splevo.diffing.match.HierarchicalMatchEngine.IgnoreStrategy;

/**
 * For now we don't ignore anything 
 * 
 * @author Lukas Burgey
 *
 */
public class LuaIgnoreStrategy implements IgnoreStrategy {

    @Override
    public boolean ignore(EObject element) {
        return false;
    }

}
