package tools.vitruv.applications.pcmjava.modelrefinement.sourcecodeinstrumentation.projectmanager;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;;

public class ProjectManager {
	
	
	final public static String instrumentationProjectLabel = "_instrumentation" ;
	

	public static IProject copyProject(IProject iProject, String iterationNumber) throws CoreException {
		String copyName = iProject.getName() + instrumentationProjectLabel + "_" + iterationNumber;
		if(existProject(copyName)) {
			deleteProject(copyName);
		}
		
	    IProgressMonitor monitoring = new NullProgressMonitor();
	    
	    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

	    IProjectDescription projectDescription = iProject.getDescription();
	    
	    
	    // create clone project in workspace
	    IProjectDescription copyDescription = workspaceRoot.getWorkspace().newProjectDescription(copyName);
	    
	    // copy project files
	    iProject.copy(copyDescription, true, monitoring);
	    IProject clonedProject = workspaceRoot.getProject(copyName);
	    
	    // copy the project properties
	    copyDescription.setNatureIds(projectDescription.getNatureIds());
	    copyDescription.setReferencedProjects(projectDescription.getReferencedProjects());
	    copyDescription.setDynamicReferences(projectDescription.getDynamicReferences());
	    copyDescription.setBuildSpec(projectDescription.getBuildSpec());
	    copyDescription.setReferencedProjects(projectDescription.getReferencedProjects());
	    
	    clonedProject.setDescription(copyDescription, null);
	    
	    return clonedProject;
	}
	
	
	public static void deleteProject(String projectName) throws CoreException {
       IProgressMonitor monitoring = new NullProgressMonitor();
       
	   IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();  
	   for(IProject iProject: workspaceRoot.getProjects()) {
		   if(iProject.getName().equals(projectName)) {
			   iProject.delete(true, monitoring);
		   }
	   }	   	   
	}
	
	
	public static boolean existProject(String projectName) {
		boolean projectExist = false; 
		
	    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();   
	    for(IProject iProject: workspaceRoot.getProjects()) {
		   if(iProject.getName().equals(projectName)) {
			   projectExist = true;
			   break;
		   }
	    }
	    
	    return projectExist;
	}
	
	
	
	
}
