package code.instrumentation.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;

import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.projectmanager.ProjectManager;
import tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.util.CodeInstrumentationUtil;

public class CloneProjectTest {
	
	final static String originalProjectName = "Test";
	final static String clonedProjectName = "Test_instrumentation";
	
	 
	//@Test
	public void cloneProjectTest() throws CoreException {
		
		// delete the project copy if already exists
		ProjectManager.deleteProject(clonedProjectName);
		
		// clone the project
		//ProjectManager.copyProject(originalProjectName);
		
		//
		assertTrue(ProjectManager.existProject(clonedProjectName));

	}
}
