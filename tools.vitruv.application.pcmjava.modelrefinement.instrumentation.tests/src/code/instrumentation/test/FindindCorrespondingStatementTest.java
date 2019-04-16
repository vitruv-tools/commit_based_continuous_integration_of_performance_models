package code.instrumentation.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.management.remote.TargetedNotification;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.impl.CompilationUnitImpl;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.statements.Statement;
import org.junit.Test;
import org.splevo.jamopp.diffing.similarity.SimilarityChecker;

import tools.vitruv.applications.pcmjava.linkingintegration.ResourceLoadingHelper;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.util.CodeInstrumentationUtil;

public class FindindCorrespondingStatementTest {
	
	@Test
	public void compareTest() throws IOException {
		//List<Resource> test_resource_lis = ResourceLoadingHelper.loadJaMoPPResourceSet(new File("testProject_3/Test/src"));
		//List<Resource> test_copy_resource_lis = ResourceLoadingHelper.loadJaMoPPResourceSet(new File("testProject/Test_instrumentation/src"));
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  	
        IJavaProject project = (IJavaProject)JavaCore.create((IProject)workspace.getRoot().getProject("Test"));
        
        ResourceSet resourceSet = CodeInstrumentationUtil.extractSoftwareModelFromProjects(project);
        List<Resource> test_resource_lis = resourceSet.getResources();
        
		// get the compilation Unit containing the class Model
		EObject eo_class_model = test_resource_lis.get(0).getContents().get(0);				
		CompilationUnitImpl unit = (CompilationUnitImpl) eo_class_model;
		
		ConcreteClassifier modelClass = CodeInstrumentationUtil.findConcreteClassifierWithName(unit, "Model");
		
		ClassMethod summeMethod = (ClassMethod) CodeInstrumentationUtil.findMethodByName(modelClass, "summe");
		ClassMethod multiplicationMethod = (ClassMethod) CodeInstrumentationUtil.findMethodByName(modelClass, "multiplication");
		
		Statement firstStatemet = summeMethod.getStatements().get(0);
		Statement secondStatement = summeMethod.getStatements().get(1);
		
		System.out.println("----->" + firstStatemet.getClass().getSimpleName());
		System.out.println("----->" + secondStatement.getClass().getSimpleName());
		
		SimilarityChecker test = new SimilarityChecker();
		boolean areEqual = test.isSimilar(firstStatemet, secondStatement);
		
		if(areEqual) {
			System.out.println("\n\n----->: they are equal");
		}
		else {
			System.out.println("\n\n----->: they are not equal");
		}
		
	}
		
	//@Test
	public void findingCorrespondingStatementTest() throws IOException {
		List<Resource> test_resource_lis = ResourceLoadingHelper.loadJaMoPPResourceSet(new File("testProject/Test/src"));
		List<Resource> test_copy_resource_lis = ResourceLoadingHelper.loadJaMoPPResourceSet(new File("testProject/Test_instrumentation/src"));
		
		// get the compilation Unit containing the class Model
		EObject eo_class_model = test_resource_lis.get(0).getContents().get(0);				
		CompilationUnitImpl unit = (CompilationUnitImpl) eo_class_model;
		ConcreteClassifier modelClass = CodeInstrumentationUtil.findConcreteClassifierWithName(unit, "Model");
		ClassMethod summeMethod = (ClassMethod) CodeInstrumentationUtil.findMethodByName(modelClass, "summe");
		
		Statement firstStatemet = summeMethod.getStatements().get(0);
		
		
		// copied class
		EObject eo_class_model_copy = test_copy_resource_lis.get(0).getContents().get(0);
		CompilationUnitImpl copied_unit = (CompilationUnitImpl) eo_class_model_copy;
		ConcreteClassifier copied_modelClass = CodeInstrumentationUtil.findConcreteClassifierWithName(copied_unit, "Model");
		ClassMethod copied_summeMethod = (ClassMethod) CodeInstrumentationUtil.findMethodByName(copied_modelClass, "summe");	
		
		Statement correspondingStatement = copied_summeMethod.getStatements().get(0);
		
		
		assertTrue(CodeInstrumentationUtil.compareStatements(firstStatemet, correspondingStatement));
	}
}
