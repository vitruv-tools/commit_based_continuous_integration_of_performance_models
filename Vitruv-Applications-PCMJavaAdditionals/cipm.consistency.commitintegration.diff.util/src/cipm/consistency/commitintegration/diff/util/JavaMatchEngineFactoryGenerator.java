package cipm.consistency.commitintegration.diff.util;

import org.splevo.diffing.match.HierarchicalMatchEngineFactory;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

public final class JavaMatchEngineFactoryGenerator {
	private JavaMatchEngineFactoryGenerator() {
	}
	
	public static HierarchicalMatchEngineFactory generateMatchEngineFactory() {
		return HierarchicalMatchEngineFactoryGenerator.generateMatchEngineFactory(
				new SimilarityChecker(), "javaxmi");
	}
}
