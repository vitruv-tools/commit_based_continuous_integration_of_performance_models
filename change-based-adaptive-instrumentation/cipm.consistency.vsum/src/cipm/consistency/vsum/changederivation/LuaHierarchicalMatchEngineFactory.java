package cipm.consistency.vsum.changederivation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.resource.StrategyResourceMatcher;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.splevo.diffing.match.HierarchicalMatchEngine;
import org.splevo.diffing.match.HierarchicalMatchEngine.EqualityStrategy;
import org.splevo.diffing.match.HierarchicalMatchEngine.IgnoreStrategy;
import org.splevo.diffing.match.HierarchicalStrategyResourceMatcher;
import org.splevo.diffing.util.NormalizationUtil;
import org.splevo.jamopp.diffing.match.JaMoPPEqualityStrategy;
import org.splevo.jamopp.diffing.match.JaMoPPIgnoreStrategy;
import org.splevo.jamopp.diffing.scope.PackageIgnoreChecker;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

public class LuaHierarchicalMatchEngineFactory extends MatchEngineFactoryImpl {

    public LuaHierarchicalMatchEngineFactory() {
        super();
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
     * Initialize the similarity checker with the according configurations.
     *
     * @param diffingOptions
     *            The map of configurations.
     * @return The prepared checker.
     */
//    private SimilarityChecker initSimilarityChecker(Map<String, String> diffingOptions) {
////        String configString = diffingOptions.get(OPTION_JAVA_CLASSIFIER_NORMALIZATION);
//        String configString = null;
//        LinkedHashMap<Pattern, String> classifierNorms = NormalizationUtil.loadRemoveNormalizations(configString, null);
//        LinkedHashMap<Pattern, String> compUnitNorms = NormalizationUtil.loadRemoveNormalizations(configString, ".lua");
//
////        String configStringPackage = diffingOptions.get(OPTION_JAVA_PACKAGE_NORMALIZATION);
//        String configStringPackage = null;
//        LinkedHashMap<Pattern, String> packageNorms = NormalizationUtil.loadReplaceNormalizations(configStringPackage);
//
//        return new SimilarityChecker(classifierNorms, compUnitNorms, packageNorms);
    private SimilarityChecker initSimilarityChecker(Map<String, String> diffingOptions) {
        return new LuaSimilarityChecker();
    }

    /**
     * Init the equality helper to decide about element similarity.
     *
     * @param similarityChecker
     *            The similarity checker to use.
     * @return The prepared equality helper.
     */
    private IEqualityHelper initEqualityHelper(SimilarityChecker similarityChecker) {
        final LoadingCache<EObject, org.eclipse.emf.common.util.URI> cache = initEqualityCache();
        IEqualityHelper equalityHelper = new EqualityHelper(cache);
        return equalityHelper;
    }

    /**
     * Initialize the resource matcher to be used by the MatchEngine.
     *
     * @param diffingOptions
     *            The configuration map to init based on.
     * @return The prepared resource matcher.
     */
    private HierarchicalStrategyResourceMatcher initResourceMatcher(Map<String, String> diffingOptions) {

//        String packageNormConfig = diffingOptions.get(OPTION_JAVA_PACKAGE_NORMALIZATION);
        String packageNormConfig = null;
        LinkedHashMap<Pattern, String> uriNormalizations = NormalizationUtil
            .loadReplaceNormalizations(packageNormConfig);

//        String classNormConfig = diffingOptions.get(OPTION_JAVA_CLASSIFIER_NORMALIZATION);
        String classNormConfig = null;
        LinkedHashMap<Pattern, String> fileNormalizations = NormalizationUtil.loadRemoveNormalizations(classNormConfig,
                ".lua");

        HierarchicalStrategyResourceMatcher resourceMatcher = new HierarchicalStrategyResourceMatcher(uriNormalizations,
                fileNormalizations);

        return resourceMatcher;
    }

    @Override
    public IMatchEngine getMatchEngine() {
        var packageIgnoreChecker = new PackageIgnoreChecker(List.of());
        Map<String, String> diffingOptions = Map.of();

        SimilarityChecker similarityChecker = initSimilarityChecker(diffingOptions);

        IEqualityHelper equalityHelper = initEqualityHelper(similarityChecker);

        EqualityStrategy equalityStrategy = new JaMoPPEqualityStrategy(similarityChecker);
//        EqualityStrategy equalityStrategy = new LuaEqualityStrategy(similarityChecker);

        IgnoreStrategy ignoreStrategy = new JaMoPPIgnoreStrategy(packageIgnoreChecker);

        StrategyResourceMatcher resourceMatcher = initResourceMatcher(diffingOptions);

        return new HierarchicalMatchEngine(equalityHelper, equalityStrategy, ignoreStrategy, resourceMatcher);
    }

    private boolean containsCodeModel(Notifier not) {
        if (not instanceof XMIResourceImpl) {
            var xmi = (XMIResourceImpl) not;
            if (xmi.getURI()
                .lastSegment()
                .endsWith(".code.xmi")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMatchEngineFactoryFor(IComparisonScope scope) {
        if (containsCodeModel(scope.getLeft()) || containsCodeModel(scope.getRight())) {
            return true;
        }
        return false;
    }

}
