package cipm.consistency.commitintegration.diff.util;

import java.util.List;

import org.eclipse.emf.compare.match.eobject.EqualityHelperExtensionProvider;
import org.eclipse.emf.compare.match.eobject.EqualityHelperExtensionProviderDescriptorRegistryImpl;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.splevo.diffing.match.HierarchicalMatchEngineFactory;
import org.splevo.diffing.match.HierarchicalStrategyResourceMatcher;
import org.splevo.jamopp.diffing.match.JaMoPPEqualityStrategy;
import org.splevo.jamopp.diffing.match.JaMoPPIgnoreStrategy;
import org.splevo.jamopp.diffing.scope.PackageIgnoreChecker;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

import com.google.common.cache.CacheBuilder;

public final class JavaMatchEngineFactoryGenerator {
	private JavaMatchEngineFactoryGenerator() {
	}
	
	public static HierarchicalMatchEngineFactory generateMatchEngineFactory() {
		SimilarityChecker checker = new SimilarityChecker();
		EqualityHelperExtensionProvider.Descriptor.Registry descRegistryImpl =
				EqualityHelperExtensionProviderDescriptorRegistryImpl.createStandaloneInstance();
		descRegistryImpl.put("javaxmi", new SimilarityCheckerBasedEqualityHelperExtensionProviderDescriptor(checker));
		return new HierarchicalMatchEngineFactory(
				new EqualityHelper(EqualityHelper.createDefaultCache(CacheBuilder.newBuilder()), descRegistryImpl),
				new JaMoPPEqualityStrategy(checker),
				new JaMoPPIgnoreStrategy(new PackageIgnoreChecker(List.of())),
				new HierarchicalStrategyResourceMatcher());
	}
}
