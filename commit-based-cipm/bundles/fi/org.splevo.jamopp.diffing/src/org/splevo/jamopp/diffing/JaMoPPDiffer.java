/*******************************************************************************
 * Copyright (c) 2014
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Benjamin Klatt - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.splevo.jamopp.diffing;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.match.resource.StrategyResourceMatcher;
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.compare.utils.IEqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emftext.commons.layout.LayoutPackage;
import org.emftext.language.java.JavaPackage;
import org.emftext.language.java.commons.Commentable;
import org.splevo.commons.emf.SPLevoResourceSet;
import org.splevo.diffing.Differ;
import org.splevo.diffing.DiffingException;
import org.splevo.diffing.DiffingNotSupportedException;
import org.splevo.diffing.match.HierarchicalMatchEngine.EqualityStrategy;
import org.splevo.diffing.match.HierarchicalMatchEngine.IgnoreStrategy;
import org.splevo.diffing.match.HierarchicalMatchEngineFactory;
import org.splevo.diffing.match.HierarchicalStrategyResourceMatcher;
import org.splevo.diffing.util.NormalizationUtil;
import org.splevo.extraction.SoftwareModelExtractionException;
import org.splevo.jamopp.diffing.diff.JaMoPPDiffBuilder;
import org.splevo.jamopp.diffing.diff.JaMoPPFeatureFilter;
import org.splevo.jamopp.diffing.match.JaMoPPEqualityHelper;
import org.splevo.jamopp.diffing.match.JaMoPPEqualityStrategy;
import org.splevo.jamopp.diffing.match.JaMoPPIgnoreStrategy;
import org.splevo.jamopp.diffing.postprocessor.JaMoPPPostProcessor;
import org.splevo.jamopp.diffing.scope.JavaModelMatchScope;
import org.splevo.jamopp.diffing.scope.PackageIgnoreChecker;
import org.splevo.jamopp.diffing.similarity.JavaSimilarityChecker;
import org.splevo.jamopp.diffing.similarity.JavaSimilarityToolboxBuilder;
import org.splevo.jamopp.diffing.similarity.base.ISimilarityChecker;
import org.splevo.jamopp.diffing.similarity.base.MapSimilarityToolboxFactory;
import org.splevo.jamopp.extraction.JaMoPPSoftwareModelExtractor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Differ for JaMoPP software models.
 *
 * <p>
 * <strong>Ignored files</strong><br>
 * By default, the differ ignores all package-info.java.xmi model files.<br>
 * The option {@link JaMoPPDiffer#OPTION_JAMOPP_IGNORE_FILES} can be provided as diffing option to
 * the doDiff() method to change this default behavior.
 * </p>
 */
public class JaMoPPDiffer implements Differ {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /** Option key for java packages to ignore. */
    public static final String OPTION_JAMOPP_IGNORE_FILES = "JaMoPP.Files.to.ignore";

    /**
     * Option key for java package patterns to ignore.<br>
     * The purpose of this option is to exclude source code packages from being considered during
     * the diff analysis.
     *
     */
    public static final String OPTION_JAVA_IGNORE_PACKAGES = "JaMoPP.Java.Packages.to.ignore";

    /**
     * Option key for package normalization.<br>
     * The purpose of this option is to normalize the package of the integrated variant.<br>
     * <p>
     * Example:<br>
     * If the original code contains a package named:<br>
     * <tt>org.example.client</tt><br>
     * and the customized code contains a package named:<br>
     * <tt>org.example.customer.client</tt><br>
     * the mapping can be used to normalize the latter package to the former.
     * </p>
     *
     * <p>
     * A rule is specified per line.<br>
     * Each line contains a pair of search and replace separated with a pipe ("|").<br>
     * For example:<br>
     * org.example.customer.client|org.example.client<br>
     * org.example.customer.server|org.example.server
     * </p>
     */
    public static final String OPTION_JAVA_PACKAGE_NORMALIZATION = "JaMoPP.Java.Package.Normalization.Pattern";

    /**
     * Option key for classifier normalization.<br>
     * The purpose of this option is to normalize the classifier of the integration variant.<br>
     * <p>
     * Example:<br>
     * If the original code contains a classifier named:<br>
     * <tt>MyClass</tt><br>
     * and the customized code contains a classifier named:<br>
     * <tt>MyClassCust</tt><br>
     * the normalization pattern can be used to remove the suffix from the classifier.
     * </p>
     *
     * <p>
     * One rule is specified per line.<br>
     * Each line specifies a prefix or suffix to replace.<br>
     * The arbitrary part of the classifiers name to keep is identified with an astrix '*'.
     * </p>
     * <p>
     * <b>Examples:</b><br>
     * To remove the suffix "Custom" from the name "MyClassCustom" the pattern must be specified as
     * "*Custom".<br>
     * To remove the prefix "My" from the name "MyBaseClass" the pattern must be specified as "My*".
     * </p>
     */
    public static final String OPTION_JAVA_CLASSIFIER_NORMALIZATION = "JaMoPP.Java.Classifier.Normalization.Pattern";

    private static final String LABEL = "JaMoPP Java Differ";
    private static final String ID = "org.splevo.jamopp.differ";
    private static Logger logger = Logger.getLogger(JaMoPPDiffer.class);
    private final JaMoPPSoftwareModelExtractor extractor;

    /**
     * Constructs a new JaMoPPDiffer.
     */
    public JaMoPPDiffer() {
        this(new JaMoPPSoftwareModelExtractor());
    }

    /**
     * Constructs a new JaMoPPDiffer with the given software model extractor.
     * 
     * @param extractor
     *            The extractor to be used during the diffing.
     */
    public JaMoPPDiffer(JaMoPPSoftwareModelExtractor extractor) {
        this.extractor = extractor;
    }
    
    /**
     * Load the source models from the according directories and perform the difference analysis of
     * the loaded {@link ResourceSet}s. <br>
     * {@inheritDoc}
     *
     * @return null if no supported source models available.
     * @throws DiffingNotSupportedException
     *             Thrown if a diffing cannot be done for the provided models.
     */
    @Override
    public Comparison doDiff(java.net.URI leadingModelDirectory, java.net.URI integrationModelDirectory,
            Map<String, String> diffingOptions) throws DiffingException, DiffingNotSupportedException {

        final List<String> ignoreFiles = loadIgnoreFileConfiguration(diffingOptions);

        logger.info("Load source models");
        ResourceSet resourceSetLeading = loadResourceSetRecursively(leadingModelDirectory, ignoreFiles);
        ResourceSet resourceSetIntegration = loadResourceSetRecursively(integrationModelDirectory, ignoreFiles);

        return doDiff(resourceSetLeading, resourceSetIntegration, diffingOptions);
    }

    /**
     * Get the ignore file configuration for the provided options.
     *
     * @param diffingOptions
     *            The options map.
     * @return The list of file names to ignore.
     */
    private List<String> loadIgnoreFileConfiguration(Map<String, String> diffingOptions) {
        final List<String> ignoreFiles;
        if (diffingOptions.containsKey(OPTION_JAMOPP_IGNORE_FILES)) {
            final String diffingRuleRaw = diffingOptions.get(OPTION_JAMOPP_IGNORE_FILES);
            final String[] parts = diffingRuleRaw.split(LINE_SEPARATOR);
            ignoreFiles = Lists.newArrayList();
            for (final String rule : parts) {
                ignoreFiles.add(rule);
            }
        } else {
            ignoreFiles = Lists.asList("package-info.java", new String[] {});
        }
        return ignoreFiles;
    }

    /**
     * Diffing the models contained in the provided resource sets.<br>
     *
     * {@inheritDoc}
     *
     * @return null if no supported source models available.
     * @throws DiffingNotSupportedException
     *             Thrown if no reasonable JaMoPP model is contained in the resource sets.
     */
    @Override
    public Comparison doDiff(ResourceSet resourceSetLeading, ResourceSet resourceSetIntegration,
            Map<String, String> diffingOptions) throws DiffingException, DiffingNotSupportedException {

        List<String> ignorePackages = buildIgnorePackageList(diffingOptions);
        PackageIgnoreChecker packageIgnoreChecker = new PackageIgnoreChecker(ignorePackages);

        EMFCompare comparator = initCompare(packageIgnoreChecker, diffingOptions);

        // Compare the two models
        // In comparison, the left side is always the changed one.
        // push in the integration model first
        IComparisonScope scope = new JavaModelMatchScope(resourceSetIntegration, resourceSetLeading,
                packageIgnoreChecker);

        Comparison comparisonModel = comparator.compare(scope);

        return comparisonModel;

    }

    /**
     * Diffing JaMoPP elements directly.<br>
     *
     * Note: This method does not take any differ specific configurations (e.g. ignore packages)
     * into account.
     *
     * {@inheritDoc}
     *
     * @return null if no supported source models available.
     */
    public Comparison doDiff(Commentable rightElement, Commentable leftElement, Map<String, String> diffingOptions) {

        List<String> ignorePackages = Lists.newArrayList();
        PackageIgnoreChecker packageIgnoreChecker = new PackageIgnoreChecker(ignorePackages);

        EMFCompare comparator = initCompare(packageIgnoreChecker, diffingOptions);

        // Compare the two models
        // In comparison, the left side is always the changed one.
        // push in the integration model first
        IComparisonScope scope = new JavaModelMatchScope(leftElement, rightElement, packageIgnoreChecker);

        Comparison comparisonModel = comparator.compare(scope);

        return comparisonModel;

    }

    /**
     * Build the list of package ignore patterns from the provided diffing options.
     *
     * @param diffingOptions
     *            Diffing options to process.
     * @return The list of patterns, maybe empty but never null.
     */
    private List<String> buildIgnorePackageList(Map<String, String> diffingOptions) {
        String diffingRuleRaw = diffingOptions.get(OPTION_JAVA_IGNORE_PACKAGES);
        List<String> ignorePackages = Lists.newArrayList();
        if (diffingRuleRaw != null) {
            final String[] parts = diffingRuleRaw.split(LINE_SEPARATOR);
            for (final String rule : parts) {
                ignorePackages.add(rule);
            }
        }
        return ignorePackages;
    }

    /**
     * Initialize the compare engine.
     *
     * @param packageIgnoreChecker
     *            The checker to decide if an element is within a package to ignore.
     * @param diffingOptions
     *            The options configuring the comparison.
     * @return The prepared emf compare engine.
     */
    private EMFCompare initCompare(PackageIgnoreChecker packageIgnoreChecker, Map<String, String> diffingOptions) {

        IMatchEngine.Factory.Registry matchEngineRegistry = initMatchEngine(packageIgnoreChecker, diffingOptions);
        IPostProcessor.Descriptor.Registry<?> postProcessorRegistry = initPostProcessors(packageIgnoreChecker,
                diffingOptions);
        IDiffEngine diffEngine = initDiffEngine(packageIgnoreChecker);
        EMFCompare comparator = initComparator(matchEngineRegistry, postProcessorRegistry, diffEngine);
        return comparator;
    }

    /**
     * Initialize a cache to be used by the equality helper.
     *
     * @return The ready to use cache.
     */
    private LoadingCache<EObject, URI> initEqualityCache() {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(
                DefaultMatchEngine.DEFAULT_EOBJECT_URI_CACHE_MAX_SIZE);
        final LoadingCache<EObject, URI> cache = EqualityHelper.createDefaultCache(cacheBuilder);
        return cache;
    }

    /**
     * Initialize the post processors and build an according registry.
     *
     * @param packageIgnoreChecker
     *            The checker if an element belongs to an ignored package.
     * @param diffingOptions
     *            The options to configure the post processor.
     * @return The prepared registry with references to the post processors.
     */
    private IPostProcessor.Descriptor.Registry<String> initPostProcessors(PackageIgnoreChecker packageIgnoreChecker,
            Map<String, String> diffingOptions) {
        IPostProcessor customPostProcessor = new JaMoPPPostProcessor(diffingOptions);
        Pattern any = Pattern.compile(".*");
        IPostProcessor.Descriptor descriptor = new BasicPostProcessorDescriptorImpl(customPostProcessor, any, any);
        IPostProcessor.Descriptor.Registry<String> postProcessorRegistry = new PostProcessorDescriptorRegistryImpl<String>();
        postProcessorRegistry.put(JaMoPPPostProcessor.class.getName(), descriptor);
        return postProcessorRegistry;
    }

    /**
     * Init the comparator instance to be used for comparison.
     *
     * @param matchEngineRegistry
     *            The registry containing the match engines to be used.
     * @param postProcessorRegistry
     *            Registry for post processors to be executed.
     * @param diffEngine
     *            The diff engine to run.
     * @return The prepared comparator instance.
     */
    private EMFCompare initComparator(IMatchEngine.Factory.Registry matchEngineRegistry,
            IPostProcessor.Descriptor.Registry<?> postProcessorRegistry, IDiffEngine diffEngine) {
        EMFCompare.Builder builder = EMFCompare.builder();
        builder.setDiffEngine(diffEngine);
        builder.setMatchEngineFactoryRegistry(matchEngineRegistry);
        builder.setPostProcessorRegistry(postProcessorRegistry);
        EMFCompare comparator = builder.build();
        return comparator;
    }

    /**
     * Initialize the diff engine with the diff processor and feature filters to be used.
     *
     * @param packageIgnoreChecker
     *            Checker to decide if an element is in a package to ignore.
     * @return The ready-to-use diff engine.
     */
    private IDiffEngine initDiffEngine(final PackageIgnoreChecker packageIgnoreChecker) {
        final JaMoPPDiffBuilder diffProcessor = new JaMoPPDiffBuilder(packageIgnoreChecker);
        IDiffEngine diffEngine = new DefaultDiffEngine(diffProcessor) {
            @Override
            protected FeatureFilter createFeatureFilter() {
                return new JaMoPPFeatureFilter(packageIgnoreChecker);
            }

            @Override
            public void diff(Comparison comparison, Monitor monitor) {
                for (Match rootMatch : comparison.getMatches()) {
                    if (isSingleSideRootMatch(rootMatch)) {
                        diffProcessor.createRootDiff(rootMatch);
                    } else {
                        checkForDifferences(rootMatch, monitor);
                    }
                }
            }

            private boolean isSingleSideRootMatch(Match rootMatch) {
                return rootMatch.getSubmatches().size() == 0
                        && (rootMatch.getLeft() == null || rootMatch.getRight() == null);
            }
        };
        return diffEngine;
    }

    /**
     * Initialize and configure the match engines to be used.
     *
     * @param packageIgnoreChecker
     *            The package ignore checker to use in the match engine.
     * @param diffingOptions
     *            The options configuring the comparison.
     *
     * @return The registry containing all prepared match engines
     */
    private IMatchEngine.Factory.Registry initMatchEngine(PackageIgnoreChecker packageIgnoreChecker,
            Map<String, String> diffingOptions) {

        ISimilarityChecker similarityChecker = initSimilarityChecker(diffingOptions);
        IEqualityHelper equalityHelper = initEqualityHelper(similarityChecker);
        EqualityStrategy equalityStrategy = new JaMoPPEqualityStrategy(similarityChecker);
        IgnoreStrategy ignoreStrategy = new JaMoPPIgnoreStrategy(packageIgnoreChecker);
        StrategyResourceMatcher resourceMatcher = initResourceMatcher(diffingOptions);

        IMatchEngine.Factory matchEngineFactory = new HierarchicalMatchEngineFactory(equalityHelper, equalityStrategy,
                ignoreStrategy, resourceMatcher);
        matchEngineFactory.setRanking(20);

        IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
        matchEngineRegistry.add(matchEngineFactory);

        return matchEngineRegistry;
    }

    /**
     * Initialize the similarity checker with the according configurations.
     *
     * @param diffingOptions
     *            The map of configurations.
     * @return The prepared checker.
     */
    private ISimilarityChecker initSimilarityChecker(Map<String, String> diffingOptions) {
        String configString = diffingOptions.get(OPTION_JAVA_CLASSIFIER_NORMALIZATION);
        LinkedHashMap<Pattern, String> classifierNorms = NormalizationUtil.loadRemoveNormalizations(configString, null);
        LinkedHashMap<Pattern, String> compUnitNorms = NormalizationUtil
                .loadRemoveNormalizations(configString, ".java");

        String configStringPackage = diffingOptions.get(OPTION_JAVA_PACKAGE_NORMALIZATION);
        LinkedHashMap<Pattern, String> packageNorms = NormalizationUtil.loadReplaceNormalizations(configStringPackage);

        var builder = new JavaSimilarityToolboxBuilder();
        builder.setSimilarityToolboxFactory(new MapSimilarityToolboxFactory());
        
        var toolbox = builder.instantiate()
        	.buildNewSimilaritySwitchHandler()
        	.buildNormalizationHandlers(classifierNorms, compUnitNorms, packageNorms)
        	.buildComparisonHandlers()
        	.build();
        
        return new JavaSimilarityChecker(toolbox);
    }

    /**
     * Init the equality helper to decide about element similarity.
     *
     * @param similarityChecker
     *            The similarity checker to use.
     * @return The prepared equality helper.
     */
    private IEqualityHelper initEqualityHelper(ISimilarityChecker similarityChecker) {
        final LoadingCache<EObject, org.eclipse.emf.common.util.URI> cache = initEqualityCache();
        IEqualityHelper equalityHelper = new JaMoPPEqualityHelper(cache, similarityChecker);
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

        String packageNormConfig = diffingOptions.get(OPTION_JAVA_PACKAGE_NORMALIZATION);
        LinkedHashMap<Pattern, String> uriNormalizations = NormalizationUtil
                .loadReplaceNormalizations(packageNormConfig);

        String classNormConfig = diffingOptions.get(OPTION_JAVA_CLASSIFIER_NORMALIZATION);
        LinkedHashMap<Pattern, String> fileNormalizations = NormalizationUtil.loadRemoveNormalizations(classNormConfig,
                ".java");

        HierarchicalStrategyResourceMatcher resourceMatcher = new HierarchicalStrategyResourceMatcher(
                uriNormalizations, fileNormalizations);

        return resourceMatcher;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public void init() {
        JavaPackage.eINSTANCE.eClass();
        LayoutPackage.eINSTANCE.eClass();
    }

    /**
     * Recursively load all files within a directory into a resource set.
     *
     * @param baseDirectory
     *            The root directory to search files in.
     * @param ignoreFiles
     *            A list of filenames to ignore during load.
     * @return The prepared resource set.
     */
    private ResourceSet loadResourceSetRecursively(java.net.URI baseDirectory, List<String> ignoreFiles) {

        List<String> projectPaths = new ArrayList<String>();
        projectPaths.add((new File(baseDirectory).getAbsolutePath()));
        try {
            return extractor.extractSoftwareModel(projectPaths, new NullProgressMonitor());
        } catch (SoftwareModelExtractionException e) {
            logger.error("Failed to load resource set", e);
        }

        return new SPLevoResourceSet();
    }

    @Override
    public Map<String, String> getAvailableConfigurations() {
        Map<String, String> options = Maps.newHashMap();
        options.put(OPTION_JAVA_IGNORE_PACKAGES, "java.*\njavax.*");
        options.put(OPTION_JAMOPP_IGNORE_FILES, "package-info.java");
        options.put(OPTION_JAVA_CLASSIFIER_NORMALIZATION, "");
        options.put(OPTION_JAVA_PACKAGE_NORMALIZATION, "");
        options.put(JaMoPPPostProcessor.OPTION_DIFF_CLEANUP_DERIVED_COPIES, "");
        options.put(JaMoPPPostProcessor.OPTION_DIFF_STATISTICS_LOG_DIR, "");
        return options;
    }

    @Override
    public int getOrderId() {
        return 0;
    }

    @Override
    public Set<String> getRequiredExtractorIds() {
        LinkedHashSet<String> ids = new LinkedHashSet<String>();
        ids.add(JaMoPPSoftwareModelExtractor.EXTRACTOR_ID);
        return ids;
    }
}
