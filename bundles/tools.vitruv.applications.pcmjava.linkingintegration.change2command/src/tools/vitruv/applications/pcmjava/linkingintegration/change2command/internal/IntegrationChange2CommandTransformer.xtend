package tools.vitruv.applications.pcmjava.linkingintegration.change2command.internal

import tools.vitruv.framework.tuid.Tuid
import tools.vitruv.framework.correspondence.Correspondence
import java.util.ArrayList
import java.util.concurrent.Callable
import org.eclipse.emf.ecore.EObject
import org.emftext.language.java.classifiers.Class
import org.emftext.language.java.classifiers.Interface
import org.emftext.language.java.containers.CompilationUnit

import org.palladiosimulator.pcm.core.entity.NamedElement
import tools.vitruv.framework.change.echange.root.InsertRootEObject
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference
import tools.vitruv.framework.change.echange.root.RemoveRootEObject
import tools.vitruv.framework.change.echange.feature.FeatureEChange
import tools.vitruv.framework.correspondence.CorrespondenceModel
import tools.vitruv.extensions.integration.correspondence.integration.IntegrationCorrespondence
import tools.vitruv.framework.util.command.ResourceAccess
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.Java2PcmIntegrationChangePropagationSpecification
import tools.vitruv.framework.change.description.TransactionalChange
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.Java2PcmChangePropagationSpecification
import tools.vitruv.framework.userinteraction.UserInteractor
import tools.vitruv.framework.userinteraction.UserInteractionOptions.WindowModality
import tools.vitruv.extensions.integration.correspondence.util.IntegrationCorrespondenceModelViewFactory

class IntegrationChange2CommandTransformer {
	
	private UserInteractor userInteracting
	
	new(UserInteractor userInteracting) {
		this.userInteracting = userInteracting
	}
	
	def compute(TransactionalChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		return executeIntegration(change, correspondenceModel, resourceAccess)
	}
	
	private def boolean executeIntegration(TransactionalChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		// Since all correspondences are considered (not only IntegrationCorrespondences),
		// only return reaction commands, if one of the other 2 checks are successful
		val existsReaction = doesReactionHandleChange(change, correspondenceModel); 
		val newClassOrInterfaceInIntegratedAreaCommand = createNewClassOrInterfaceInIntegratedAreaCommand(
			change, correspondenceModel)
    	if (newClassOrInterfaceInIntegratedAreaCommand !== null) {
    		if (existsReaction) {
				executeReactions(change, correspondenceModel, resourceAccess);
				return true;
			}
			newClassOrInterfaceInIntegratedAreaCommand.call()
    		return true;
    	}
    	val defaultIntegrationChangeCommand = getDefaultIntegrationChangeCommand(change, correspondenceModel)
    	if (defaultIntegrationChangeCommand !== null) {
    		if (existsReaction) {
				executeReactions(change, correspondenceModel, resourceAccess);
				return true;
			}
    		defaultIntegrationChangeCommand.call()
    		return true
    	}
    	return false
	}
	
	def doesReactionHandleChange(TransactionalChange change, CorrespondenceModel correspondenceModel) {
		val changePropagationSpecifications = new Java2PcmIntegrationChangePropagationSpecification()
		changePropagationSpecifications.userInteractor = userInteracting
		return changePropagationSpecifications.doesHandleChange(change, correspondenceModel);
	}
	
	def executeReactions(TransactionalChange change, CorrespondenceModel correspondenceModel, ResourceAccess resourceAccess) {
		val changePropagationSpecifications= new Java2PcmChangePropagationSpecification();
		
		changePropagationSpecifications.userInteractor = userInteracting
		changePropagationSpecifications.propagateChange(change, correspondenceModel, resourceAccess)
	}
	
	private def createNewClassOrInterfaceInIntegratedAreaCommand(TransactionalChange eChange, CorrespondenceModel correspondenceModel) {
        if (eChange instanceof InsertEReference<?,?> && (eChange as InsertEReference<?,?>).isContainment()) { 
        	//Check if this is a creation of a class or interface on file level.
        	//In this case we need to check if any siblings in the package have been integrated
        	val change = eChange as InsertEReference<?,?>
        	val classOfAffected = change.getAffectedEObject().eClass().getInstanceClass()
        	val classOfCreated = change.getNewValue().eClass().getInstanceClass()
        	if (classOfAffected == typeof(CompilationUnit) && 
        			(classOfCreated.equals(typeof(Class)) || 
        			 classOfCreated.equals(typeof(Interface)))) {
        		val cu = change.getAffectedEObject() as CompilationUnit
        		//TODO use IntegrationCorrespondence view of InternalCorrespondenceModel which is
        		//statically typed to Correspondence right now and needs to support views like 
        		//CorrespondenceModel
                val ci = correspondenceModel
                val newCompilationUnitTuid = ci.calculateTuidFromEObject(cu)
                val packagePartOfNewTuid = getPackagePart(newCompilationUnitTuid)
    			for (Correspondence corr : ci.getAllCorrespondences()) {
    				if (corr instanceof IntegrationCorrespondence) {
    					val integrationCorr = corr as IntegrationCorrespondence
	    				if (integrationCorr.isCreatedByIntegration()) {
	    					val allTuids = new ArrayList<Tuid>()
	    					allTuids.addAll(corr.getATuids())
	    					allTuids.addAll(corr.getBTuids())
	    					for (Tuid tuid : allTuids) {
	    						val packagePart = getPackagePart(tuid)
	    						if (packagePartOfNewTuid.startsWith(packagePart)) {
	    							val command = new Callable<Void>() {
										override call() throws Exception {
											showNewTypeInIntegratedAreaDialog()
											return null
										}
									}
	    							return command
	    						}
	    					}
	    				}
    				}
    			}
        	}
    	} 
        return null
	}
	
	private def String getPackagePart(Tuid newCompilationUnitTuid) {
		val originalTuidAsString = newCompilationUnitTuid.toString()
		val lastSlashIndex = originalTuidAsString.lastIndexOf("/")
		return originalTuidAsString.substring(0, lastSlashIndex)
	}
	
	private def showNewTypeInIntegratedAreaDialog() {
		val buffer = new StringBuffer()
		buffer.append("Created class or interface in area with integrated objects.\n")
		buffer.append("Please handle consistency manually.")
		userInteracting.notificationDialogBuilder.message(buffer.toString()).windowModality(WindowModality.MODAL).startInteraction;
	}
	
	private def getDefaultIntegrationChangeCommand(TransactionalChange eChange, CorrespondenceModel correspondenceModel) {
        val correspondingIntegratedEObjects = getCorrespondingEObjectsIfIntegrated(eChange, correspondenceModel)
        if (correspondingIntegratedEObjects !== null) {
	    	val buffer = new StringBuffer()
	    	buffer.append("Elements in change were integrated into Vitruvius.\n")
	    	buffer.append("Please fix manually. Corresponding object(s):\n")
	    	for (EObject obj : correspondingIntegratedEObjects) {
	    		val name = getReadableName(obj)
	    		buffer.append("\n")
	    		buffer.append(name)
	    	}
			val command = new Callable<Void>() {
					override call() throws Exception {
						userInteracting.notificationDialogBuilder.message(buffer.toString()).windowModality(WindowModality.MODAL).startInteraction;
						return null
					}
				}
	    	return command
        } 
        return null
	}
	
	private def getReadableName(EObject obj) {
    	var name = getDirectNameIfNamed(obj)
		val className = obj.eClass().getName()
    	var container = obj.eContainer()
    	while (name === null) {
    		if (container === null) {
    			name = className
    		} else {
    			val containerName = getDirectNameIfNamed(container)
    			if (containerName !== null) {
    				val containerClassName = container.eClass().getName()
					name = className + " in " + containerClassName + ": " + containerName
    			} else {
    				container = container.eContainer()
    			}
    		}
    	}
		return name
	}

	private def getDirectNameIfNamed(EObject obj) {
		var String name = null
		val className = obj.eClass().getName()
		if (obj instanceof NamedElement) {
			val named = obj as NamedElement
			name =  className + ": " + named.getEntityName()
		} else if (obj instanceof org.emftext.language.java.commons.NamedElement) {
			val named = obj as org.emftext.language.java.commons.NamedElement
			name =  className + ": " + named.getName()
		}
		return name
	}

	/**
     * 
     * @return set of corresponding EObjects if integrated, else null
     */
    private def getCorrespondingEObjectsIfIntegrated(TransactionalChange eChange,
            CorrespondenceModel correspondenceModel) {
        val ci = correspondenceModel
        var EObject eObj = null
        if (eChange instanceof FeatureEChange<?,?>) {
            eObj = eChange.getAffectedEObject() as EObject
        } else if (eChange instanceof InsertRootEObject<?>) {
            eObj = eChange.getNewValue() as EObject
        } else if (eChange instanceof RemoveRootEObject<?>) {
            eObj = eChange.getOldValue() as EObject
        }

        val integrationView = ci.getView(IntegrationCorrespondenceModelViewFactory.instance)
        if (eObj !== null) {
            return integrationView.getCorrespondingIntegratedEObjects(#[eObj], null).flatten
        }
        return null
    }
	
}
