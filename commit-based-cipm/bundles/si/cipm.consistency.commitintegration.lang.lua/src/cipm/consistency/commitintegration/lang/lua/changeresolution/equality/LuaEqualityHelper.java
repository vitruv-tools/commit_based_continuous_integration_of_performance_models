package cipm.consistency.commitintegration.lang.lua.changeresolution.equality;

import org.apache.log4j.Logger;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.ecore.EObject;

import com.google.common.cache.LoadingCache;

public class LuaEqualityHelper extends EqualityHelper {

    private static final Logger LOGGER = Logger.getLogger(LuaEqualityHelper.class);

    private EditionDistanceEqualityChecker editionDistanceEqualityChecker;

    /**
     * Constructor to initialize the required cache.
     *
     * @param uriCache
     *            The cache to use during the equality checks.
     */
    public LuaEqualityHelper(LoadingCache<EObject, org.eclipse.emf.common.util.URI> uriCache) {
        super(uriCache);

        editionDistanceEqualityChecker = new EditionDistanceEqualityChecker();
    }

    @Override
    protected boolean matchingEObjects(EObject left, EObject right) {
        if (left == null || right == null) {
            LOGGER.warn("Equality helper invoked for null EObject");
            return false;
        }

        var match = LuaEqualityChecker.match(left, right);
        if (match != null) {
            return match;
        }
        if (editionDistanceEqualityChecker.match(left, right)) {
            return true;
        }
        return false;
    }

}
