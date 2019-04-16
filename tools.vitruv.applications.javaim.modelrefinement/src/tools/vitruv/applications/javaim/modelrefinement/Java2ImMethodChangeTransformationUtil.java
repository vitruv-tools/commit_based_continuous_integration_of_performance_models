package tools.vitruv.applications.javaim.modelrefinement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.InternalCallAction;
import org.palladiosimulator.pcm.seff.LoopAction;

import tools.vitruv.extensions.dslsruntime.reactions.helper.PersistenceHelper;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
import tools.vitruv.models.im.AppProbes;
import tools.vitruv.models.im.ImFactory;
import tools.vitruv.models.im.ImPackage;
import tools.vitruv.models.im.Probe;

public class Java2ImMethodChangeTransformationUtil {
    
	/**
	 * @param correspondenceModel
	 * @param oldMethod
	 * @param newMethod
	 * @throws InterruptedException 
	 */
	public static void execute(CorrespondenceModel correspondenceModel,
			Method oldMethod, Method newMethod) {
		
		URI modelUri = PersistenceHelper.getURIFromSourceProjectFolder(newMethod, "model/instrumentationmodel.im");
		 
		final Set<AbstractAction> oldCorrespondingAbstractActions = CorrespondenceModelUtil
				.getCorrespondingEObjectsByType(correspondenceModel, oldMethod, AbstractAction.class);	
		
		final Set<AbstractAction> newCorrespondingAbstractActions = CorrespondenceModelUtil
				.getCorrespondingEObjectsByType(correspondenceModel, newMethod, AbstractAction.class);
		

	    deleteOldProbes(oldCorrespondingAbstractActions, modelUri);
	    	    
	    saveIM(newCorrespondingAbstractActions, modelUri);

	}
	
	
	private static void saveIM(Set<AbstractAction> listAbstractActions, URI modelUri) {
		AppProbes appProbes = getAppProbes(modelUri);
		if(appProbes == null) {
			appProbes = ImFactory.eINSTANCE.createAppProbes();
		}
		
		for(AbstractAction aa: listAbstractActions) {
			if(isRelevante(aa)) {
				Probe probe = ImFactory.eINSTANCE.createProbe();
				probe.setAbstractActionID(aa.getId());
				probe.setIsActive(true);
				appProbes.getProbes().add(probe);				
			}
		}
		
		persistImElement(modelUri, appProbes);
	}
	
	
	private static void persistImElement(URI uri, EObject probe) {
		ImPackage.eINSTANCE.eClass();
						
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("im", new XMIResourceFactoryImpl());
		
		ResourceSet resSet = new ResourceSetImpl();
		Resource resource = resSet.createResource(uri);
	       
	    resource.getContents().add(probe);
	   
	    try {
	       resource.save(Collections.EMPTY_MAP);
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
	}
	
	
	public static AppProbes getAppProbes(URI modelUri) {		
		ImPackage.eINSTANCE.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("im", new XMIResourceFactoryImpl());
        
        ResourceSet resSet = new ResourceSetImpl();
        // Get the resource
        try {
            Resource resource = resSet.getResource(modelUri, true);
            return (AppProbes) resource.getContents().get(0);
        }catch (Exception e) {
			return null;
		}
       		
	}
	
	
	private static void deleteOldProbes(Set<AbstractAction> oldCorrespondingAbstractActions, URI modelUri) {
		AppProbes appProbes = getAppProbes(modelUri);
		if(appProbes == null) {
			return;
		}
		for(AbstractAction aa: oldCorrespondingAbstractActions) {
			List<Probe> listProbes = new ArrayList<Probe>();
			for(Probe probe: appProbes.getProbes()) {
				listProbes.add(probe);
			}
			for(Probe probe: listProbes) {
				if(aa.getId().equals(probe.getAbstractActionID())) {
					Resource eResource = probe.eResource();
					EcoreUtil.delete(probe);
					try {
						eResource.save(Collections.EMPTY_MAP);
					} catch (IOException e) {
					}
				}
			}
		}
				
	}
	
	
	public static boolean isRelevante(AbstractAction abstractAction) {	
		if(abstractAction instanceof InternalAction) {
			return true;
		}
		else if(abstractAction instanceof BranchAction){
			return true;
		}
		else if(abstractAction instanceof LoopAction){
			return true;
		}
		else if(abstractAction instanceof InternalCallAction) {
			return true;
		}
		else if(abstractAction instanceof ExternalCallAction) {
			return true;
		}
		
		return false;	
	}
	
	
}
