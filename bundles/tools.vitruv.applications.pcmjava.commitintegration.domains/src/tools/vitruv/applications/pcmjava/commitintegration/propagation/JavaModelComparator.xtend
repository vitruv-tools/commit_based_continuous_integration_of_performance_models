package tools.vitruv.applications.pcmjava.commitintegration.propagation

import org.eclipse.emf.common.notify.Notifier
import org.eclipse.emf.compare.EMFCompare
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.compare.diff.DiffBuilder
import org.eclipse.emf.compare.diff.DefaultDiffEngine
import org.eclipse.emf.compare.diff.FeatureFilter
import org.eclipse.emf.compare.postprocessor.BasicPostProcessorDescriptorImpl
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl
import java.util.regex.Pattern
import java.util.List
import org.eclipse.emf.compare.rcp.EMFCompareRCPPlugin
import tools.vitruv.applications.pcmjava.commitintegration.diff.util.ResourceListFilteringComparisonScope
import tools.vitruv.applications.pcmjava.commitintegration.diff.util.JavaMatchEngineFactoryGenerator
import org.emftext.language.java.JavaPackage
import org.splevo.jamopp.diffing.scope.PackageIgnoreChecker
import org.splevo.jamopp.diffing.diff.JaMoPPFeatureFilter
import org.eclipse.emf.compare.postprocessor.IPostProcessor

/**
 * This class provides methods for comparing Java models.
 * 
 * @author Timur Saglam
 * @author Ilia Chupakhin
 * @author Martin Armbruster
 */
class JavaModelComparator {
	/**
	 * Compares Java models using EMF Compare.
	 * 
	 * @param newState contains the new state.
	 * @param currentState contains the current or old state compared to the new state.
	 * @param newResources a list of Resources which represent the new state of Java models.
	 *                     If the newState is a ResourceSet, this list is used to filter the Resources in the newState.
	 * @param currentResources a list of Resources which represent the current or old state of Java models.
	 *                         If the currentState is a ResourceSet, this list is used to filter the Resources in the
	 *                         currentState.
	 * @param postProcessor an optional post processor for the comparison process.
	 */
	static def compareJavaModels(Notifier newState, Notifier currentState,
			List<Resource> newResources, List<Resource> currentResources,
			IPostProcessor postProcessor) {
		
		val scope = new ResourceListFilteringComparisonScope(newState, currentState, newResources, currentResources)
		scope.nsURIs.add(JavaPackage.eNS_URI)
		
		val jamoppFeatureFilter = new JaMoPPFeatureFilter(new PackageIgnoreChecker(List.of()))
		val diffProcessor = new DiffBuilder()
		val diffEngine = new DefaultDiffEngine(diffProcessor) {
			override protected FeatureFilter createFeatureFilter() {
				return jamoppFeatureFilter
			}
		}
		
		val matchEngineFactory = JavaMatchEngineFactoryGenerator.generateMatchEngineFactory
		matchEngineFactory.ranking = 20
		var engineRegistry = EMFCompareRCPPlugin.getDefault.getMatchEngineFactoryRegistry
		engineRegistry.add(matchEngineFactory)
		
		val builder = EMFCompare.builder
			.setMatchEngineFactoryRegistry(engineRegistry)
			.setDiffEngine(diffEngine)
		
		if (postProcessor !== null) {
			val processorDescriptor = new BasicPostProcessorDescriptorImpl(postProcessor, Pattern.compile(".*"), null)
			val processorRegistry = new PostProcessorDescriptorRegistryImpl()
			processorRegistry.put("java", processorDescriptor)
			builder.setPostProcessorRegistry(processorRegistry)
		}
		
		return builder.build.compare(scope)
	}
}
