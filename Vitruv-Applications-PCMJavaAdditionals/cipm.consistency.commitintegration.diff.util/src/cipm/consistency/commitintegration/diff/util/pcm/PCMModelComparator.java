package cipm.consistency.commitintegration.diff.util.pcm;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.palladiosimulator.pcm.repository.RepositoryPackage;

import cipm.consistency.commitintegration.diff.util.HierarchicalMatchEngineFactoryGenerator;
import cipm.consistency.commitintegration.diff.util.ResourceListFilteringComparisonScope;

public class PCMModelComparator {
	/**
	 * Compares PCM repository models using EMF Compare.
	 * 
	 * @param newState contains the new state: a repository or a repository resource.
	 * @param currentState contains the current or old state compared to the new state: a repository or a repository resource.
	 * @return the result of the comparison.
	 */
	public static Comparison compareRepositoryModels(Notifier newState, Notifier currentState) {
		
		var scope = new ResourceListFilteringComparisonScope(newState, currentState, null, null);
		scope.getNsURIs().add(RepositoryPackage.eNS_URI);
		
		return EMFCompare.builder()
				.setMatchEngineFactoryRegistry(
						HierarchicalMatchEngineFactoryGenerator.generateMatchEngineRegistry(
						PCMRepositoryMatchEngineFactoryGenerator.generateMatchEngineFactory()))
				.build().compare(scope);
	}
}
