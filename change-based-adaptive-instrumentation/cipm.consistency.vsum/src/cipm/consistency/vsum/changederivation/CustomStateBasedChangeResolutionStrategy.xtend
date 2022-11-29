package cipm.consistency.vsum.changederivation

import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.BasicMonitor
import org.eclipse.emf.compare.EMFCompare
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl
import org.eclipse.emf.compare.merge.BatchMerger
import org.eclipse.emf.compare.merge.IMerger
import org.eclipse.emf.compare.scope.DefaultComparisonScope
import org.eclipse.emf.compare.utils.UseIdentifiers
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.change.composite.recording.ChangeRecorder
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy
import tools.vitruv.framework.views.util.ResourceCopier

import static com.google.common.base.Preconditions.checkArgument

import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil.getReferencedProxies

/**
 * This default strategy for diff based state changes uses EMFCompare to resolve a 
 * diff to a sequence of individual changes.
 * @author Timur Saglam
 */
class CustomStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
	/** The identifier matching behavior used by this strategy */
	public val UseIdentifiers useIdentifiers

	var EMFCompare.Builder emfCompareBuilder
//	var LuaDiffer differ

	/**
	 * Creates a new instance with the default identifier matching behavior 
	 * which is match by id
	 */
	new() {
		this(UseIdentifiers.WHEN_AVAILABLE)
	}

	/**
	 * Creates a new instance with the provided identifier matching behavior.
	 * @param useIdentifiers The identifier matching behavior to use.
	 */
	new(UseIdentifiers useIdentifiers) {
		this.useIdentifiers = useIdentifiers
		// we don't use the differ directly, but just use it to initialize the factory
//		differ = new LuaDiffer()
		// ignore no packages
//		val packageIgnoreChecker = new PackageIgnoreChecker(List.of())
		// get the factory from the differ
//		val hierarchicalMatchEngineFactory = differ.getMatchEngineFactory(packageIgnoreChecker, Map.of());

		val hierarchicalMatchEngineFactory = new LuaHierarchicalMatchEngineFactory();
		hierarchicalMatchEngineFactory.ranking = 20
		
		emfCompareBuilder = (EMFCompare.builder => [
			matchEngineFactoryRegistry = new MatchEngineFactoryRegistryImpl => [
				add(new MatchEngineFactoryImpl(useIdentifiers))
				add(hierarchicalMatchEngineFactory)
			]
		])
	}

	private def checkNoProxies(Resource resource, String stateNotice) {
		val proxies = resource.referencedProxies
		checkArgument(proxies.empty, "%s '%s' should not contain proxies, but contains the following: %s", stateNotice,
			resource.URI, String.join(", ", proxies.map[toString]))
	}

	override getChangeSequenceBetween(Resource newState, Resource oldState) {
		checkArgument(oldState !== null && newState !== null, "old state or new state must not be null!")
		newState.checkNoProxies("new state")
		oldState.checkNoProxies("old state")
		val monitoredResourceSet = new ResourceSetImpl()
		val currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet)
		return currentStateCopy.record [
			if (oldState.URI != newState.URI) {
				currentStateCopy.URI = newState.URI
			}
			compareStatesAndReplayChanges(newState, currentStateCopy)
		]
	}

	override getChangeSequenceForCreated(Resource newState) {
		checkArgument(newState !== null, "new state must not be null!")
		newState.checkNoProxies("new state")
		// It is possible that root elements are automatically generated during resource creation (e.g., Java packages).
		// Thus, we create the resource and then monitor the re-insertion of the elements
		val monitoredResourceSet = new ResourceSetImpl()
		val newResource = monitoredResourceSet.createResource(newState.URI)
		newResource.contents.clear()
		return newResource.record [
			newResource.contents += EcoreUtil.copyAll(newState.contents)
		]
	}

	override getChangeSequenceForDeleted(Resource oldState) {
		checkArgument(oldState !== null, "old state must not be null!")
		oldState.checkNoProxies("old state")
		// Setup resolver and copy state:
		val monitoredResourceSet = new ResourceSetImpl()
		val currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet)
		return currentStateCopy.record [
			currentStateCopy.contents.clear()
		]
	}

	private def <T extends Notifier> record(Resource resource, ()=>void function) {
		try (val changeRecorder = new ChangeRecorder(resource.resourceSet)) {
			changeRecorder.beginRecording
			changeRecorder.addToRecording(resource)
			function.apply()
			return changeRecorder.endRecording
		}
	}
	
	private def EMFCompare getEmfCompare() {
		emfCompareBuilder.build();
	}

	/**
	 * Compares states using EMFCompare and replays the changes to the current state.
	 */
	private def compareStatesAndReplayChanges(Notifier newState, Notifier currentState) {
		val scope = new DefaultComparisonScope(newState, currentState, null)
		
		// I tried using the differ directly but it did not work
//		val comparision = differ.doDiff(scope);
//		val differences = comparision.differences;
		val differences = getEmfCompare().compare(scope).differences
		// Replay the EMF compare differences
		val mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance()
		val merger = new BatchMerger(mergerRegistry)
		merger.copyAllLeftToRight(differences, new BasicMonitor)
	}
}
