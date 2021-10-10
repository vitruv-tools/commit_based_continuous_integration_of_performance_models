package cipm.consistency.commitintegration.diff.util.pcm;

import org.splevo.diffing.match.HierarchicalMatchEngineFactory;

import cipm.consistency.commitintegration.diff.util.HierarchicalMatchEngineFactoryGenerator;

/**
 * A generator for HierarchicalMatchEngineFactories specific for PCM repository
 * models.
 * 
 * @author Martin Armbruster
 */
public final class PCMRepositoryMatchEngineFactoryGenerator {
	private PCMRepositoryMatchEngineFactoryGenerator() {
	}

	/**
	 * Generates a HierarchicalMatchEngineFactory specific for PCM repository
	 * models.
	 * 
	 * @return the generated factories.
	 */
	public static HierarchicalMatchEngineFactory generateMatchEngineFactory() {
		return HierarchicalMatchEngineFactoryGenerator.generateMatchEngineFactory(new PCMRepositorySimilarityChecker(),
				"repository");
	}
}
