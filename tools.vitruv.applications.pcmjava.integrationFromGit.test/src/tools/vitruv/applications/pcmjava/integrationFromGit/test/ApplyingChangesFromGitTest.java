package tools.vitruv.applications.pcmjava.integrationFromGit.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.eclipse.jgit.api.errors.*;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.jgit.api.errors.GitAPIException;

import tools.vitruv.applications.pcmjava.linkingintegration.tests.CodeIntegrationTest;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.CodeIntegrationUtils;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.DoneFlagProgressMonitor;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitRepository;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilder;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.domains.pcm.PcmNamespace;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.tuid.Tuid;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModelConfiguration;
import tools.vitruv.framework.vsum.VirtualModelImpl;

/**
 * @author Ilia
 *
 */
public class ApplyingChangesFromGitTest {

	
	@SuppressWarnings("unused")
    
	private static final Logger logger = Logger.getLogger(CodeIntegrationTest.class.getSimpleName());
    private static final String META_PROJECT_NAME = "vitruvius.meta";
    
    private IProject testProject;
    private String testProjectName;
    private String testProjectPath = "testProjects/petersen/projectToApplyCommitsOn/eu.fpetersen.cbs.pc";
    
    private GitRepository gitRepository;
    private String gitRepositoryPath = "testProjects/petersen/projectWithCommits";
    private String clonedGitRepositoryPath = "temporaryGitRepository";
    
    private IWorkspace workspace;
    private String workspacePath;
    private InternalVirtualModel virtualModel;
    
 
    //private String testBundleName;
    //private String testSourceAndModelFolder;
    

    @Before
    public void beforeTest() throws InvocationTargetException, InterruptedException, IOException, URISyntaxException, GitAPIException, CoreException {
        this.workspace = ResourcesPlugin.getWorkspace();
        this.workspacePath = workspace.getRoot().getFullPath().makeAbsolute().toString();
        this.testProjectName = "eu.fpetersen.cbs.pc";
        //this.testBundleName = "tools.vitruv.applications.pcmjava.integrationFromGit.test";
        //this.testSourceAndModelFolder = "C:/Users/Ilia/git/Petersen/eu.fpetersen.cbs.pc";
        
        importAndCopyProjectIntoWorkspace(workspace, testProjectName, testProjectPath);
        IProject[] iProjects = this.workspace.getRoot().getProjects();
        IProject project = this.workspace.getRoot().getProject(this.testProjectName);
        
        assert project != null;
        this.testProject = project;
        
        File clonedGitRepository = File.createTempFile("git_" + testProjectName, "", new File("testProjects/clonedGitRepositories"));
        Files.delete(clonedGitRepository.toPath());
        clonedGitRepository.deleteOnExit();
        
        //Compute the absolute path to the origin git repository to clone
        String pathToOriginGitRepository = (new Path(clonedGitRepository.getAbsolutePath())).removeLastSegments(3).toString() + "/" + gitRepositoryPath;
       
        
        
        gitRepository = new GitRepository(clonedGitRepository, pathToOriginGitRepository/*"C:/Users/Ilia/eclipse-workspace-2020-03_createdAt_20-04-2020/tools.vitruv.applications.pcmjava.integrationFromGit.test/testProjects/petersen/projectWithCommits"*/);
        
        //gitRepository = new GitRepository(new File(workspacePath + "myTemporaryGitRepository"), "C:/Users/Ilia/eclipse-workspace-2020-03_createdAt_20-04-2020/tools.vitruv.applications.pcmjava.integrationFromGit.test/testProjects/petersen/projectWithCommits");
        
        //gitRepository = new GitRepository(new File("C:/Users/Ilia/myTemporaryGitRepository"), new File("C:/Users/Ilia/eclipse-workspace-2020-03_createdAt_20-04-2020/tools.vitruv.applications.pcmjava.integrationFromGit.test/testProjects/petersen/projectWithCommits/.git"/*gitRepositoryPath + "/.git"*/));
        System.out.println("");
       //TODO: integrate test project in Vitruv
      //CodeIntegrationUtils.integratProject(project);
        
    }
    
    
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	
	 public static void importAndCopyProjectIntoWorkspace(IWorkspace workspace, String projectName, String projectPath
		 	/*String testBundleName, String testSourceAndModelFolder*/)
	            throws IOException, URISyntaxException, InvocationTargetException, InterruptedException {
	        IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
	            public String queryOverwrite(String file) {
	                return ALL;
	            };
	        };
	        IPath workspacePath = workspace.getRoot().getFullPath().append("/" + projectName);

	        //Bundle bundle = Platform.getBundle(testBundleName);
	        //URL projectBluePrintBundleURL = bundle.getEntry(testSourceAndModelFolder);
	        //URL fileURL = FileLocator.resolve(projectBluePrintBundleURL);
	        //File file = new File(fileURL.toURI());
	        //String baseDir = file.getAbsolutePath();// location of files to import
	        ImportOperation importOperation = new ImportOperation(workspacePath, new File(projectPath/*baseDir*/),
	                FileSystemStructureProvider.INSTANCE, overwriteQuery);
	        importOperation.setCreateContainerStructure(false);
	        //DoneFlagProgressMonitor progress = new DoneFlagProgressMonitor();//probably does not work correctly, because progress.isDone() is never true
	        importOperation.run(/*progress*/null);

	        // Wait for the project to be imported 
	        /*
	        while (!progress.isDone()) {
	            Thread.sleep(100);
	        }
	        */
	 }
	 
	 public static void importProjectIntoWorkspace(IWorkspace workspace, String pathToProject) throws CoreException {

	     IPath projectDotProjectFile = new Path(pathToProject + "/.project");
		 IProjectDescription projectDescription = workspace.loadProjectDescription(projectDotProjectFile);
		 IProject project = workspace.getRoot().getProject(projectDescription.getName());
		 JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), null);
	     
	 }

	 
	 

}
