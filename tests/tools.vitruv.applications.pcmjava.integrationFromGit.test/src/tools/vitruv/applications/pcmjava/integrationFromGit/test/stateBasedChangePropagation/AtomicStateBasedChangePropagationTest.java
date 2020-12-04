package tools.vitruv.applications.pcmjava.integrationFromGit.test.stateBasedChangePropagation;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.emftext.language.java.JavaClasspath;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import tools.vitruv.applications.pcmjava.integrationFromGit.GitChangeApplier;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitRepository;

import tools.vitruv.applications.pcmjava.integrationFromGit.propagation.GitStateBasedChangeResolutionStrategy;
import tools.vitruv.applications.pcmjava.integrationFromGit.response.GitIntegrationChangePropagationSpecification;

import tools.vitruv.applications.pcmjava.integrationFromGit.test.ApplyingChangesTestUtil;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.commits.EuFpetersenCbsPc_integratedArea_fineGrained_commits;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.commits.EuFpetersenCbsPc_nonIntegratedArea_compilationUnitChanges_coarseGrained_Commits;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * Test for changing a method header in Integrated Area (IA) with State Based Propagation Strategy (SBPS)
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 */
public class AtomicStateBasedChangePropagationTest {

	//Project name
	private static String testProjectName = "eu.fpetersen.cbs.pc";
	//Relative path to the project which will be copied into Workspace and the copied project will be integrated into Vitruv. Commits will be applied on the copy.
	private static String testProjectPath =	"testProjects/petersen/projectToApplyCommitsOn/eu.fpetersen.cbs.pc";
	//Relative path to the folder that contains git repository as well as the project. The folder will be copied into workspace. The commits will be read from this repository.  
	private static String gitRepositoryPath = "testProjects/petersen/projectWithCommits";
	//Change propagation specification(s). It defines how the changes on JaMoPP models will be propagate to the corresponding PCM models.
	//More than one change propagation specification can be used at the same time, but not all of them are compatible with each other.
	private static ChangePropagationSpecification[] changePropagationSpecifications = {new GitIntegrationChangePropagationSpecification()};
	//Logger used to print some useful information about program while program running on the console
	private static Logger logger = Logger.getLogger("simpleLogger");
	//JDT Model of the integrated project
	private static IProject testProject;
	//JDT Model of the project from git repository
	private static IProject projectFromGitRepository;
	//JDT Model of the current workspace
	private static IWorkspace workspace;
	//Vitruv Virtual Model. It contains all created JaMoPP models as well as correspondences between the JaMoPP and PCM models. 
	private static InternalVirtualModel virtualModel;
	//User dialog used for informing or asking user to make a decision about propagated changes
	//private static TestUserInteraction testUserInteractor;
	//Git repository copied into workspace
	private static GitRepository gitRepository;
	//Git change applier. It applies commits on the integrated project
	private static GitChangeApplier changeApplier;
	//Contains all commits. A key is commit hash, a value is commit. 
	private static Map<String, RevCommit> commits = new HashMap<>();

	@BeforeClass
	public static void setUpBeforeClass() throws InvocationTargetException, InterruptedException, IOException,
			URISyntaxException, GitAPIException, CoreException {
		//get workspace
		workspace = ResourcesPlugin.getWorkspace();
        //copy git repository into workspace
        gitRepository = ApplyingChangesTestUtil.copyGitRepositoryIntoWorkspace(workspace, gitRepositoryPath);
		//copy test project into workspace
        testProject = ApplyingChangesTestUtil.importAndCopyProjectIntoWorkspace(workspace, testProjectName, testProjectPath);
        //Thread.sleep(10000);
        //create change applier for copied repository
        changeApplier = new GitChangeApplier(gitRepository);
        //integrate test project in Vitruv       
        virtualModel = ApplyingChangesTestUtil.integrateProjectWithChangePropagationSpecification(testProject, changePropagationSpecifications, changeApplier, 
        		new PcmDomainProvider().getDomain(), new JavaDomainProvider().getDomain());
        //checkout and track branch
        gitRepository.checkoutAndTrackBranch(EuFpetersenCbsPc_integratedArea_fineGrained_commits.METHOD_HEADER_BRANCH_NAME);
        //get all commits from branch and save them in a Map. Commit hash as Key and commit itself as Value in the Map.
        List<RevCommit> commitsList = gitRepository.getAllCommitsFromBranch(EuFpetersenCbsPc_integratedArea_fineGrained_commits.METHOD_HEADER_BRANCH_NAME);
        for (RevCommit commit: commitsList) {
        	commits.put(commit.getName(), commit);
        } 
	}
	
	//Enable this method if you want to execute more than one test class
	/*	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//Remove Vitruv Java Builder that is responsible for change propagation
		final VitruviusJavaBuilderApplicator pcmJavaRemoveBuilder = new VitruviusJavaBuilderApplicator();
		pcmJavaRemoveBuilder.removeBuilderFromProject(testProject);
		//Remove JDT model of the copied project as well as this project from file system
		testProject.delete(true, null);
		//Remove the folder containing Vitruv meta data from file system
		FileUtils.deleteDirectory(virtualModel.getFolder());
		//Close and remove copied git repository
		gitRepository.closeRepository();
		//projectFromGitRepository.close(null);
		projectFromGitRepository.delete(true, null);
		FileUtils.deleteDirectory(new File(workspace.getRoot().getLocation().toFile(), "clonedGitRepositories"));
		// This is necessary because otherwise Maven tests will fail as
		// resources from previous tests are still in the classpath and accidentally resolved
		JavaClasspath.reset();
	}
	*/	
		
	@Test
	public void testChangeMethodHeader() throws Throwable {
		testRenameMethodInInterfaceWithSBPS();
		
		//TODO Does not work yet
		//testRenameMethodInClassWithSBPS();
		
		//TODO Does not work yet
		//testChangeReturnTypeInInterfaceMethodWithSBPS();
		
		//TODO Does not work yet
		//testChangeReturnTypeInClassMethodWithSBPS();
		
		//TODO Does not work yet
		//testAddReturn0InClassMethodWithSBPS();
		
		//TODO Does not work yet
		//testAddFinalModifierToClassMethodWithSBPS();
		
		//TODO Does not work yet
		//testAddMethodParameterInInterfaceWithSBPS();
		
		//TODO Does not work yet
		//testAddMethodParameterInClassWithSBPS();
	}
	
	

	
	
	
	
	private void testRenameMethodInInterfaceWithSBPS() throws Throwable {			
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_INTERFACE);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertInterfaceMethodWithName("drawFrameRenamed", compUnitChanged, virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean noPcmExists = ApplyingChangesTestUtil.assertNoInterfaceMethodWithName("drawFrame", compUnitChanged, virtualModel);
				
		assertTrue("In testRenameMethodInInterfaceWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testRenameMethodInInterfaceWithSBPS() corresponding PCM model does not exist, but it should exist", pcmExists);
		assertTrue("In testRenameMethodInInterfaceWithSBPS() corresponding PCM model exists, but it should not exist", noPcmExists);
	}
	
	
	private void testRenameMethodInClassWithSBPS() throws Throwable {			
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_CLASS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertClassMethodWithName("drawFrameRenamed", compUnitChanged, virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean noPcmExists = ApplyingChangesTestUtil.assertNoClassMethodWithName("drawFrame", compUnitChanged, virtualModel);
				
		assertTrue("In testRenameMethodInClassWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testRenameMethodInClassWithSBPS() corresponding PCM model does not exist, but it should exist", pcmExists);
		assertTrue("In testRenameMethodInClassWithSBPS() corresponding PCM model exists, but it should not exist", noPcmExists);
	}
	
	
	
	private void testChangeReturnTypeInInterfaceMethodWithSBPS() throws Throwable {			
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_INTERFACE_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertInterfaceMethodPrimitiveReturnTypeWithName("drawFrameRenamed", compUnitChanged, virtualModel);
		
				
		assertTrue("In testChangeReturnTypeInInterfaceMethodWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testChangeReturnTypeInInterfaceMethodWithSBPS() corresponding PCM model does not exist, but it should exist", pcmExists);
	}
	
	
	
	private void testChangeReturnTypeInClassMethodWithSBPS() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_CLASS_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
					
		assertTrue("In testChangeReturnTypeInClassMethodWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testAddReturn0InClassMethodWithSBPS() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_RETURN_0);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
			
		assertTrue("In testAddReturn0InClassMethodWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testAddFinalModifierToClassMethodWithSBPS() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_FINAL_TO_CLASS_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
			
		assertTrue("In testAddFinalModifierToClassMethodWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testAddMethodParameterInInterfaceWithSBPS() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_METHOD_PARAMETER_IN_INTERFACE);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertInterfaceMethodParameterWithName("drawFrameRenamed", "parameter", compUnitChanged, virtualModel);
				
		assertTrue("In testAddMethodParameterInInterfaceWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testAddMethodParameterInInterfaceWithSBPS() corresponding PCM model does not exist, but it should exist", pcmExists);
		
	}
	
	
	private void testAddMethodParameterInClassWithSBPS() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_METHOD_PARAMETER_IN_CLASS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//State based propagation
		changeApplier.applyChangesFromCommitUsingStateBasedChangePropagation(compUnitFromGit, compUnitChanged, virtualModel);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
						
		assertTrue("In testAddMethodParameterInClassWithSBPS() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
}
