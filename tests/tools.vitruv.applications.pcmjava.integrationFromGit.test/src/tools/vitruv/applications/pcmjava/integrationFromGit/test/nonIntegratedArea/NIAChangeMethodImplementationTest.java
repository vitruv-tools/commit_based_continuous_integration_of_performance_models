package tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea;

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
import tools.vitruv.applications.pcmjava.integrationFromGit.response.GitIntegrationChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.ApplyingChangesTestUtil;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.commits.EuFpetersenCbsPc_integratedArea_fineGrained_commits;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.commits.EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * Test for changing a method implementation in Non-Integrated Area (NIA) 
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 */
public class NIAChangeMethodImplementationTest {

	//Project name
	private static String testProjectName = "eu.fpetersen.cbs.pc";
	//Relative path to the project which will be copied into Workspace and the copied project will be integrated into Vitruv. Commits will be applied on the copy.
	private static String testProjectPath =	"testProjects/petersen/projectToApplyCommitsOn/eu.fpetersen.cbs.pc";
	//Relative path to the folder that contains git repository as well as the project. The folder will be copied into workspace. The commits will be read from this repository.  
	private static String gitRepositoryPath = "testProjects/petersen/projectWithCommits";
	//Change propagation specification(s). It defines how the changes on JaMoPP models will be propagate to the corresponding PCM models.
	//More than one change propagation specification can be used at the same time, but not all of them are compatible with each other.
	private static ChangePropagationSpecification[] changePropagationSpecifications = {	new GitIntegrationChangePropagationSpecification()};
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
        virtualModel = ApplyingChangesTestUtil.integrateProjectWithChangePropagationSpecification(testProject, changePropagationSpecifications, changeApplier);
        //prepare a non-integrated area
        prepareNonIntegratedArea(); 
	}
	
		
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
	

	private static void prepareNonIntegratedArea() throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException, IOException, CoreException, InterruptedException {
		// checkout and track branch
		gitRepository.checkoutAndTrackBranch(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.BRANCH_NAME_COMMON);
		// get all commits from branch
		List<RevCommit> commitsList = gitRepository.getAllCommitsFromBranch(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.BRANCH_NAME_COMMON);
		// apply all commits
		changeApplier.applyChangesFromCommits(commitsList, testProject);
		// checkout and track branch
		gitRepository.checkoutAndTrackBranch(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.BRANCH_NAME_METHOD_IMPLEMENTATION);
		// get all commits from branch and save them in a Map. Commit hash as Key and
		// commit itself as Value in the Map.
		commitsList = gitRepository.getAllCommitsFromBranch(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.BRANCH_NAME_METHOD_IMPLEMENTATION);
		for (RevCommit commit : commitsList) {
			commits.put(commit.getName(), commit);
		}
	}

	
	@Test
	public void testMethodImplementation() throws Throwable {
		testAddFirstInternalAction();
		//JaMoPP-Models are not equals. Why?
		testAddImport();
		testAddFirstExternalAction();
		testAddSecondInternalActionAndLoopWithSecondExternalAction();
		testAddIfElseWithInternalActionAndExternalCall();
	}
	

	private void testAddFirstInternalAction()
			throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		// Remove import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_IMPLEMENTS_AND_METHOD_IN_SECOND_CLASS_IMPL),
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_FIRST_INTERNAL_ACTION_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL),
				testProject);
		// Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_FIRST_INTERNAL_ACTION_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL);
		// Create temporary model from project from git repository. It does NOT add the
		// created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace,
				workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName
						+ ".withGit");
		// Get the changed compilation unit and the compilation unit from git repository
		// to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit,  virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertNumberOfInternalActions("firstMethodInFirstInterface", compUnitChanged, virtualModel, 1);
		
		assertTrue("In testAddFirstInternalAction() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testAddFirstInternalAction() corresponding PCM model does not exist, but it should exist", pcmExists);
	}
	
	
	private void testAddImport() throws Throwable {
		//Apply changes
		changeApplier.applyChangesFromCommit(
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_FIRST_INTERNAL_ACTION_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL),
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_SECOND_IMPORT_IN_FIRST_CLASS_IMPL),
				testProject);
		// Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_SECOND_IMPORT_IN_FIRST_CLASS_IMPL);
		// Create temporary model from project from git repository. It does NOT add the
		// created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace,
				workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName
						+ ".withGit");
		// Get the changed compilation unit and the compilation unit from git repository
		// to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit,  virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertRepositoryComponentCorrespondingToCompilationUnit(compUnitChanged.getElementName(), compUnitChanged, virtualModel);
		
		assertTrue("In testAddImport() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testAddImport() corresponding PCM model does not exist, but it should exist", pcmExists);
	}
	
	
	
	private void testAddFirstExternalAction() 
			throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Apply changes
		changeApplier.applyChangesFromCommit(
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_SECOND_IMPORT_IN_FIRST_CLASS_IMPL),
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_FIRST_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL),
				testProject);	
		// Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_FIRST_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL);
		// Create temporary model from project from git repository. It does NOT add the
		// created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace,
				workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName
						+ ".withGit");
		// Get the changed compilation unit and the compilation unit from git repository
		// to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit,  virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertNumberOfExternalCalls("firstMethodInFirstInterface", compUnitChanged, virtualModel, 1);
		
		assertTrue("In testAddFirstExternalAction() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testAddFirstExternalAction() corresponding PCM model does not exist, but it should exist", pcmExists);
	}	

	
	private void testAddSecondInternalActionAndLoopWithSecondExternalAction() 
		throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException,
		InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Apply changes
		changeApplier.applyChangesFromCommit(
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_FIRST_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL),
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_SECOND_INTERNAL_ACTION_AND_LOOP_WITH_SECOND_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL),
				testProject);
		// Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_SECOND_INTERNAL_ACTION_AND_LOOP_WITH_SECOND_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL);
		// Create temporary model from project from git repository. It does NOT add the
		// created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace,
				workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName
						+ ".withGit");
		// Get the changed compilation unit and the compilation unit from git repository
		// to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit,  virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists_twoInternalActions = ApplyingChangesTestUtil.assertNumberOfInternalActions("firstMethodInFirstInterface", compUnitChanged, virtualModel, 2);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists_oneLoop = ApplyingChangesTestUtil.assertOneLoopWithOneExternalCall("firstMethodInFirstInterface", compUnitChanged, virtualModel);
		
		assertTrue("In testAddSecondInternalActionAndLoopWithSecondExternalAction() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testAddSecondInternalActionAndLoopWithSecondExternalAction() corresponding PCM model for two internal actions does not exist, but it should exist", pcmExists_twoInternalActions);
		assertTrue("In testAddSecondInternalActionAndLoopWithSecondExternalAction() corresponding PCM model for loop does not exist, but it should exist", pcmExists_oneLoop);
	}
	
	
	private void testAddIfElseWithInternalActionAndExternalCall() 
			throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Apply changes
		changeApplier.applyChangesFromCommit(
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_SECOND_INTERNAL_ACTION_AND_LOOP_WITH_SECOND_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL),
				commits.get(EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_IF_ELSE_WITH_INTERNAL_ACTION_AND_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL),
				testProject);
		// Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(
				EuFpetersenCbsPc_nonIntegratedArea_methodChanges_fineGrained_Commits.ADD_IF_ELSE_WITH_INTERNAL_ACTION_AND_EXTERNAL_CALL_IN_FIRST_METHOD_IN_FIRST_CLASS_IMPL);
		// Create temporary model from project from git repository. It does NOT add the
		// created project to the workspace.
		projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace,
				workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName
						+ ".withGit");
		// Get the changed compilation unit and the compilation unit from git repository
		// to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper
				.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit,  virtualModel);
		//Ensure that there is a corresponding PCM model to the compUnitChanged.
		boolean pcmExists = ApplyingChangesTestUtil.assertOneBranchWithOneExternalCallAndOneInternalAction("firstMethodInFirstInterface", compUnitChanged, virtualModel);
			
		assertTrue("In testAddIfElseWithInternalActionAndExternalCall() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		assertTrue("In testAddIfElseWithInternalActionAndExternalCall() corresponding PCM model does not exist, but it should exist", pcmExists);
	
	}
	
}
