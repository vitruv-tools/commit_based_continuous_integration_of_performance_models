package tools.vitruv.applications.pcmjava.linkingintegration.seffstatments

import tools.vitruv.framework.correspondence.Correspondence
import tools.vitruv.framework.util.bridges.CollectionBridge
import org.apache.log4j.Logger
import org.emftext.language.java.commons.Commentable
import org.palladiosimulator.pcm.repository.BasicComponent
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour
import org.palladiosimulator.pcm.seff.StartAction
import org.palladiosimulator.pcm.seff.StopAction
import org.somox.sourcecodedecorator.AbstractActionClassMethodLink
import org.somox.sourcecodedecorator.MethodLevelResourceDemandingInternalBehaviorLink
import org.somox.sourcecodedecorator.SourceCodeDecoratorRepository
import org.somox.sourcecodedecorator.SEFF2MethodMapping
import org.emftext.language.java.statements.StatementListContainer
import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaCorrespondenceModelTransformation
import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaIntegrationExtending

/**
 * this class extends the PCMJaMoPPCorrespondenceModelTransformation to enable the integration of SEFFs and
 *  ResourceDemandingInternalBehaviour as well as the integration of components, interfaces etc.
 */
class PcmJavaCorrespondenceModelForSeffTransformation implements PcmJavaIntegrationExtending{

	private static final Logger logger = Logger.getLogger(PcmJavaCorrespondenceModelForSeffTransformation.simpleName)
	
	override afterBasicTransformations(PcmJavaCorrespondenceModelTransformation currentCorrespondences) {
		createBehaviourCorrespondences(currentCorrespondences)
	}

	private def createBehaviourCorrespondences(PcmJavaCorrespondenceModelTransformation currentCorrespondences) {
		var scdmRepo = currentCorrespondences.scdm.contents.get(0) as SourceCodeDecoratorRepository
		// SEFF
		scdmRepo.seff2MethodMappings.forEach[createSeff2MethodMappings(currentCorrespondences)]
		// RDIB
		scdmRepo.methodLevelResourceDemandingInternalBehaviorLink.forEach [
			createMethod2ResourceDemandingInternalBehaviorLink(currentCorrespondences)
		]
		// AbstractAction
		scdmRepo.abstractActionClassMethodLink.forEach[createAbstractAction2ClassMethodLink(currentCorrespondences)]
	}

	def private createSeff2MethodMappings(SEFF2MethodMapping seff2MethodMapping,
		PcmJavaCorrespondenceModelTransformation currentCorrespondences) {
		val seff = seff2MethodMapping.seff
		var StatementListContainer statementListContainer = currentCorrespondences.resolveJaMoppProxy(seff2MethodMapping.statementListContainer)
		statementListContainer = currentCorrespondences.deresolveIfNesessary(statementListContainer)
		val parentCorrespondence = findParentCorrespondenceWithType(statementListContainer.containingConcreteClassifier, currentCorrespondences,
			BasicComponent)
		currentCorrespondences.addCorrespondence(seff, statementListContainer, parentCorrespondence)
	}

	private def createMethod2ResourceDemandingInternalBehaviorLink(
		MethodLevelResourceDemandingInternalBehaviorLink method2InternalBehavior,
		PcmJavaCorrespondenceModelTransformation currentCorrespondences) {
		val method = currentCorrespondences.resolveJaMoppProxy(method2InternalBehavior.function)
		val jaMoPPClass = method.containingConcreteClassifier
		val parentCorrespondence = findParentCorrespondenceWithType(jaMoPPClass, currentCorrespondences, BasicComponent)
		currentCorrespondences.addCorrespondence(method,
			method2InternalBehavior.resourceDemandingInternalBehaviour, parentCorrespondence)
	}

	def private createAbstractAction2ClassMethodLink(AbstractActionClassMethodLink abstractAction2classMethod,
		PcmJavaCorrespondenceModelTransformation currentCorrespondences) {
		val abstractAction = abstractAction2classMethod.abstractAction
		if (abstractAction instanceof StartAction || abstractAction instanceof StopAction) {
			// we do not create a correspondence for StartAction and StopAction 
			return
		}
		val classMethod = currentCorrespondences.resolveJaMoppProxy(abstractAction2classMethod.classMethod)
		val parentCorrespondence = findParentCorrespondenceWithType(classMethod, currentCorrespondences,
			ResourceDemandingBehaviour)
		currentCorrespondences.addCorrespondence(abstractAction, classMethod, parentCorrespondence)
	}

	def private <T> findParentCorrespondenceWithType(Commentable jaMoPPClass,
		PcmJavaCorrespondenceModelTransformation currentCorrespondences, Class<T> type) {

		val correspondences = currentCorrespondences.CInstance.getCorrespondences(CollectionBridge.toList(jaMoPPClass))
		val correspondencesContainingBc = correspondences.filter [ 
			!currentCorrespondences.CInstance.getCorrespondingEObjectsInCorrespondence(it, CollectionBridge.toList(jaMoPPClass)).
			filter[type.isInstance(it)].nullOrEmpty
		]
		var Correspondence parentCorrespondence = null;
		if (!correspondencesContainingBc.nullOrEmpty) {
			parentCorrespondence = correspondencesContainingBc.get(0)
		} else if (!correspondences.nullOrEmpty) {
			parentCorrespondence = correspondences.get(0)
		} else {
			logger.warn("Could not find a parent correspondence for object: " + jaMoPPClass)
		}
		return parentCorrespondence
	}
	
}
