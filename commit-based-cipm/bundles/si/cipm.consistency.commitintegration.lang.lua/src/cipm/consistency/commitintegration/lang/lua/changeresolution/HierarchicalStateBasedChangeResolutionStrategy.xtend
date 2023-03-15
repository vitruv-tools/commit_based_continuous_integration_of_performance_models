package cipm.consistency.commitintegration.lang.lua.changeresolution

import cipm.consistency.commitintegration.lang.lua.changeresolution.temp.ResourceCopier
import org.apache.log4j.Logger
import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.common.util.BasicMonitor
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.compare.Diff
import org.eclipse.emf.compare.DifferenceKind
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
import tools.vitruv.change.composite.description.TransactionalChange
import tools.vitruv.change.composite.recording.ChangeRecorder
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy

import static com.google.common.base.Preconditions.checkArgument

import static extension edu.kit.ipd.sdq.commons.util.org.eclipse.emf.ecore.resource.ResourceUtil.getReferencedProxies

/**
 * This change resolution strategy tries to uses multiple match engines to resolve
 * changes in PCM, IMM and Lua-code models.
 * 
 * @author Timur Saglam
 * @author Lukas Burgey
 */
class HierarchicalStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {

	Logger LOGGER = Logger.getLogger(HierarchicalStateBasedChangeResolutionStrategy)

	val EMFCompare.Builder emfCompareBuilder

	new() {
		// Match engine factory for PCM / IMM
		val defaultMatchEngineFactory = new MatchEngineFactoryImpl(UseIdentifiers.WHEN_AVAILABLE)
		defaultMatchEngineFactory.ranking = 10

		// Lua match engine with higher ranking
		val hierarchicalMatchEngineFactory = new LuaHierarchicalMatchEngineFactory();
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
		checkArgument(proxies.empty,
			"%s '%s' should not contain proxies, butools.vitruv.change.compositet contains the following: %s",
			stateNotice, resource.URI, String.join(", ", proxies.map[toString]))
	}

	private def <T extends Notifier> record(Resource resource, ()=>void function) {
		try (val changeRecorder = new ChangeRecorder(resource.resourceSet)) {
			changeRecorder.beginRecording
			changeRecorder.addToRecording(resource)
			function.apply()
			val change = changeRecorder.endRecording
			logChange(change)
			return change
		}
	}

	private def logChange(TransactionalChange change) {
		if (change.EChanges.size > 0) {
			LOGGER.info("Change recorder recorded changes: " + change.EChanges.size)
		}
	}

	private def getEmfCompare() {
		return emfCompareBuilder.build
	}

	static def isCodeModel(Resource res) {
		res.getURI.lastSegment.endsWith(".code.xmi")
	}

	/*
	 * print some infos about the differences we found in models
	 */
	private def printModelChanges(Resource res, EList<Diff> differences) {
		var changeText = "EMFcompare found changes in model " + res.URI.lastSegment + ":"
		for (kind : DifferenceKind.VALUES) {
			changeText += "  " + kind + ": " + differences.stream.filter [
				it.kind == kind
			].count
		}
		LOGGER.info(changeText)
	}

	/**
	 * Compares states using EMFCompare and replays the changes to the current state.
	 */
	private def compareStatesAndReplayChanges(Resource newState, Resource currentState) {
		// need to have the same URI for comparison
		if (currentState.URI != newState.URI) {
			currentState.URI = newState.URI
		}

		// left: newState,  right: currentState
		val scope = new DefaultComparisonScope(newState, currentState, null)
		val compare = getEmfCompare()
		val comparision = compare.compare(scope)
		val differences = comparision.differences

		if (isCodeModel(newState)) {
			// print the changes
			printModelChanges(newState, differences)
		}

		// Replay the EMF compare differences
		val mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance()
		val merger = new BatchMerger(mergerRegistry)
		val monitor = new BasicMonitor

		// left: newState,  right: currentState
		// by merging the new state onto the old we record the change sequence from old to new
		merger.copyAllLeftToRight(differences, monitor)
	}

	override getChangeSequenceBetween(Resource newState, Resource currentState) {
		// preliminary checks
		checkArgument(currentState !== null && newState !== null, "current and new state must not be null!")
		newState.checkNoProxies("new state")
		currentState.checkNoProxies("old state")

		// preparer resource set for recording
		val monitoredResourceSet = new ResourceSetImpl()
		val currentStateCopy = ResourceCopier.copyViewResource(currentState, monitoredResourceSet)

		// record the replaying of the changes between the resource sets
		return currentStateCopy.record [
			compareStatesAndReplayChanges(newState, currentStateCopy)
		]
	}

	override getChangeSequenceForCreated(Resource newState) {
		// preliminary checks
		checkArgument(newState !== null, "new state must not be null!")
		newState.checkNoProxies("new state")

		// prepare resource set for recording
		// It is possible that root elements are automatically generated during resource creation (e.g., Java packages).
		// Thus, we create the resource and then monitor the re-insertion of the elements
		val monitoredResourceSet = new ResourceSetImpl()
		val newResource = monitoredResourceSet.createResource(newState.URI)
		newResource.contents.clear()

		// record adding all contents to the empty resource set
		return newResource.record [
			newResource.contents += EcoreUtil.copyAll(newState.contents)
		]
	}

	override getChangeSequenceForDeleted(Resource oldState) {
		// preliminary checks
		checkArgument(oldState !== null, "old state must not be null!")
		oldState.checkNoProxies("old state")

		// prepare resource set for recording
		val monitoredResourceSet = new ResourceSetImpl()
		val currentStateCopy = ResourceCopier.copyViewResource(oldState, monitoredResourceSet)

		// record the deletion of all contents of the old resource set
		return currentStateCopy.record [
			currentStateCopy.contents.clear()
		]
	}
}
