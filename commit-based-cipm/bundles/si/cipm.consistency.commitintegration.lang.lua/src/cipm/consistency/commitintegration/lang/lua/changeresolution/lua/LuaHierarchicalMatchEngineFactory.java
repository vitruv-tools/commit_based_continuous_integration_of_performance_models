package cipm.consistency.commitintegration.lang.lua.changeresolution.lua;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.resource.StrategyResourceMatcher;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.splevo.diffing.match.HierarchicalMatchEngine;
import org.splevo.diffing.match.HierarchicalMatchEngine.EqualityStrategy;
import org.splevo.diffing.match.HierarchicalMatchEngine.IgnoreStrategy;
import org.splevo.diffing.match.HierarchicalStrategyResourceMatcher;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class LuaHierarchicalMatchEngineFactory extends MatchEngineFactoryImpl {

    public LuaHierarchicalMatchEngineFactory() {
        super(UseIdentifiers.NEVER);
    }

    /**
     * Initialize a cache to be used by the equality helper.
     *
     * @return The ready to use cache.
     */
    private LoadingCache<EObject, URI> initEqualityCache() {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
            .maximumSize(DefaultMatchEngine.DEFAULT_EOBJECT_URI_CACHE_MAX_SIZE);
        final LoadingCache<EObject, URI> cache = EqualityHelper.createDefaultCache(cacheBuilder);
        return cache;
    }

    /**
     * Init the equality helper to decide about element similarity.
     *
     * @param similarityChecker
     *            The similarity checker to use.
     * @return The prepared equality helper.
     */
    private IEqualityHelper initEqualityHelper() {
        final LoadingCache<EObject, org.eclipse.emf.common.util.URI> cache = initEqualityCache();
        IEqualityHelper equalityHelper = new LuaEqualityHelper(cache);
        return equalityHelper;
    }

    @Override
    public IMatchEngine getMatchEngine() {
        IEqualityHelper equalityHelper = initEqualityHelper();

        EqualityStrategy equalityStrategy = new LuaEqualityStrategy(equalityHelper);

        IgnoreStrategy ignoreStrategy = new LuaIgnoreStrategy();

        StrategyResourceMatcher resourceMatcher = new HierarchicalStrategyResourceMatcher();

        return new HierarchicalMatchEngine(equalityHelper, equalityStrategy, ignoreStrategy, resourceMatcher);
    }

    private boolean containsCodeModel(Notifier not) {
        if (not instanceof XMIResourceImpl xmi) {
            return xmi.getURI()
                .lastSegment()
                .endsWith(".code.xmi");
        }
        return false;
    }

    @Override
    public boolean isMatchEngineFactoryFor(IComparisonScope scope) {
        return containsCodeModel(scope.getLeft()) && containsCodeModel(scope.getRight());
    }
    
    
    /**
     * We don't ignore anything currently
     */
    public class LuaIgnoreStrategy implements IgnoreStrategy {
        @Override
        public boolean ignore(EObject element) {
            return false;
        }
    }

}
