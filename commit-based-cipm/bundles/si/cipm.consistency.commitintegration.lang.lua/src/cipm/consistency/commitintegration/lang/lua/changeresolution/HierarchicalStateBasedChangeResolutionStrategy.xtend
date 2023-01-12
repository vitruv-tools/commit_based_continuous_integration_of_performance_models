package cipm.consistency.commitintegration.lang.lua.changeresolution

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

import static com.google.common.base.Preconditions.checkArgument

import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil.getReferencedProxies
import cipm.consistency.commitintegration.lang.lua.changeresolution.temp.ResourceCopier

/**
 * This default strategy for diff based state changes uses EMFCompare to resolve a 
 * diff to a sequence of individual changes.
 * @author Timur Saglam
 */
class HierarchicalStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
	/** The identifier matching behavior used by this strategy */
	public val UseIdentifiers useIdentifiers
	
	MatchEngineFactoryImpl hierarchicalMatchEngineFactory;

	var EMFCompare.Builder emfCompareBuilder

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

		// Match engine factory for PCM / IMM
		val defaultMatchEngineFactory = new MatchEngineFactoryImpl(useIdentifiers)
		defaultMatchEngineFactory.ranking = 10

		// For Lua
		hierarchicalMatchEngineFactory = new LuaHierarchicalMatchEngineFactory();
		hierarchicalMatchEngineFactory.ranking = 20

		emfCompareBuilder = (EMFCompare.builder => [
			matchEngineFactoryRegistry = new MatchEngineFactoryRegistryImpl => [
				add(defaultMatchEngineFactory)
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
		val compare = getEmfCompare()
		
		if (hierarchicalMatchEngineFactory.isMatchEngineFactoryFor(scope)) {
			// TODO this condition is only needed because eclipse breakpoints don't allow me to
			// add a condition like it :/
//			var foo = 1
		}

		val comparision = compare.compare(scope)
		val differences = comparision.differences

		// Replay the EMF compare differences
		val mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance()
		val merger = new BatchMerger(mergerRegistry)
		
		// TODO is this merge direction correct?
		merger.copyAllLeftToRight(differences, new BasicMonitor)
	}
}
