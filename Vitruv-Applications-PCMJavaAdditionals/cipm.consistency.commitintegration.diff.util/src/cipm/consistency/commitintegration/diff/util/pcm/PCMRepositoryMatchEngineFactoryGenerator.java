package cipm.consistency.commitintegration.diff.util.pcm;

import org.splevo.diffing.match.HierarchicalMatchEngineFactory;

import cipm.consistency.commitintegration.diff.util.HierarchicalMatchEngineFactoryGenerator;

public final class PCMRepositoryMatchEngineFactoryGenerator {
	private PCMRepositoryMatchEngineFactoryGenerator() {
	}
	
	public static HierarchicalMatchEngineFactory generateMatchEngineFactory() {
		return HierarchicalMatchEngineFactoryGenerator.generateMatchEngineFactory(
				new PCMRepositorySimilarityChecker(), "repository");
	}
}
