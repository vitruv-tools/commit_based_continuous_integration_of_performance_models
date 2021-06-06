package tools.vitruv.applications.pcmjava.instrumentation.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.core.IJavaProject;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.resource.JaMoPPUtil;
import org.emftext.language.java.resource.java.IJavaLocationMap;
import org.emftext.language.java.resource.java.mopp.JavaResource;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.impl.BlockImpl;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.splevo.jamopp.extraction.JaMoPPSoftwareModelExtractor;

public class CodeInstrumentationUtil {
	
	public static String getInternalActionResourceId(InternalAction internalAction) {
		if(internalAction.getResourceDemand_Action().size() != 0) {
			ParametricResourceDemand parametricResourceDemand = 
					internalAction.getResourceDemand_Action().get(0);
			ProcessingResourceType processingResourceType = 
					parametricResourceDemand.getRequiredResource_ParametricResourceDemand();
			return processingResourceType.getId();
		}
		else {
			// FIXME: we may should throw an exception if the resource demand was not found
			return null;
		}
		
	}
	
	
	public static String getExecutedBranchId(BranchAction branchAction) {
		EList<AbstractBranchTransition> branchTransitions = branchAction.getBranches_Branch();
		if(branchTransitions.size() != 0) {
			return branchTransitions.get(0).getId();
		}
		else {
			return null;
		}
	}
	
	
	public static List<Resource> loadJaMoPPResourceSet(final List<File> paths) throws IOException {
    	final JaMoPPSoftwareModelExtractor jamoppreader = new JaMoPPSoftwareModelExtractor();
        final boolean extractLayoutInformation = true;
        jamoppreader.extractSoftwareModelFromFolders(paths, new NullProgressMonitor(), null,
                extractLayoutInformation);
        return jamoppreader.getSourceResources();
    }
	
	public static List<Resource> loadJaMoPPResourceSet(final File path) throws IOException {
        final JaMoPPSoftwareModelExtractor jamoppreader = new JaMoPPSoftwareModelExtractor();
        final boolean extractLayoutInformation = true;
        jamoppreader.extractSoftwareModelFromFolders(Collections.singletonList(path), new NullProgressMonitor(), null,
                extractLayoutInformation); 
        return jamoppreader.getSourceResources();
    }
	
	public static ResourceSet extractSoftwareModelFromProjects(IJavaProject iproject) {
		final JaMoPPSoftwareModelExtractor jamoppreader = new JaMoPPSoftwareModelExtractor();
        final boolean extractLayoutInformation = true;
       
        return jamoppreader.extractSoftwareModelFromProjects(Collections.singletonList(iproject), new NullProgressMonitor(), null,
                extractLayoutInformation);
	}
	 
	
	public static String getSeffElementType(AbstractAction abstractAction) {
		String type = null;
		
		if(abstractAction instanceof InternalAction) {
			type =  "IA";
		}
		else if(abstractAction instanceof BranchAction){
			type = "BA";
		}
		else if(abstractAction instanceof LoopAction){
			type = "LA";
		}
		
		return type;
	}
	

	public static ConcreteClassifier findConcreteClassifierWithName(CompilationUnit compilationUnit, 
									 String concreteClassifierName ) {
		ConcreteClassifier concreteClassifier = null;
		for(ConcreteClassifier cClassifier: compilationUnit.getClassifiers()) {
			if(cClassifier.getName().equals(concreteClassifierName)) {
				concreteClassifier  = cClassifier;
				break;
			}
		}
		
		return concreteClassifier;
	}
	
	
	public static Method findMethodByName(ConcreteClassifier concreteClassifier, String methodName) {
		Method method = null;
		for(Method m: concreteClassifier.getMethods()) {
			if(m.getName().equals(methodName)) {
				method = m;
				break;
			}
		}
		return method;
	}
	
	
	public static boolean compareStatements(Statement originalStatement, Statement clonedStatement) {
		
		/**
		 * compare tow statement from different project based on the location
		 */
		if(getStatementLocation(originalStatement) == getStatementLocation(clonedStatement)) {
			return true;
		}
		else {
			return false;
		}

			
	}
	
	private static int getStatementLocation(Statement s) {
		 Resource resource = s.eResource(); 
		 JavaResource textResource = (JavaResource) resource;
		 IJavaLocationMap locationMap = textResource.getLocationMap();
		 int location = locationMap.getLine(s);
		 return location;	 
	}
	
	
	public static EObject copyJaMoPPCompilationUnit(EObject eobjectToCpy) {
		JaMoPPUtil.initialize();
		EObject eobjCopy = EcoreUtil.copy(eobjectToCpy);
		URI uri = URI.createURI("test.java");
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource res = resourceSet.createResource(uri);
		res.getContents().add(eobjCopy);
		return eobjCopy;
	}
	
	
   public static Statement  getbranchLoopFirstChildStatement(Statement branchLoopStatement) {
    	List<Statement> childs = branchLoopStatement.getChildrenByType(Statement.class);
    	if(childs.size() == 0) {
    		return null;
    	}
    	else {
    		if(!childs.get(0).getClass().equals(BlockImpl.class)) {
    			return childs.get(0);
    		}
    		else {
    			if(childs.size() > 1) {
    				return childs.get(1);
    			}
    			else {
    				return childs.get(0);
    			}
    		}
    	}
    }
	
	
	public static String getUUID() {
		 UUID uuid = UUID.randomUUID();
		 return uuid.toString().replaceAll("-", "_");
	}
	
	
}
