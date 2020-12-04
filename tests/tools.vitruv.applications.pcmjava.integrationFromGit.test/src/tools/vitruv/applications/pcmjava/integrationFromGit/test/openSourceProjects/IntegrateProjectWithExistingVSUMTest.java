package tools.vitruv.applications.pcmjava.integrationFromGit.test.openSourceProjects;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
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
import tools.vitruv.applications.pcmjava.linkingintegration.ResourceLoadingHelper;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * @author Ilia Chupakhin
 *
 */
public class IntegrateProjectWithExistingVSUMTest {

	private static String testProjectName = // "testAddParameter_Fri_Apr_24_18_45_38_CEST_2020";
			// "humanBeing";
			"eu.fpetersen.cbs.pc";
		// "project";
		// "mediaStore";
	     //"apache-any23-core";

	private static String testProjectPath = // "testProjects/vitruvius/projectToApplyCommitsOn/testAddParameter_Fri_Apr_24_18_45_38_CEST_2020";
			// "testProjects/chupakhin/projectToApplyCommitsOn/humanBeing";
			"testProjects/petersen/projectToApplyCommitsOn/eu.fpetersen.cbs.pc";
		// "testProjects/mediastore/projectToApplyCommitsOn/project";
		// "testProjects/myMediastore/projectToApplyCommitsOn/mediaStore";
		 //"testProjects/any23/projectToApplyCommitsOn/apache-any23-core";

	private static String gitRepositoryPath = // "testProjects/vitruvius/projectWithCommits";
			// "testProjects/chupakhin/projectWithCommits";
			"testProjects/petersen/projectWithCommits";
		// "testProjects/mediastore/projectWithCommits";
		// "testProjects/myMediastore/projectWithCommits";
		//"testProjects/any23/projectWithCommits";
	
	private static String vitruviusMetaPath = "testProjects/petersen/projectToApplyCommitsOn/vitruvius.meta";

	private static ChangePropagationSpecification[] changePropagationSpecifications = {
			//new PackageMappingIntegrationChangePropagationSpecification()
			//new Java2PcmIntegrationChangePropagationSpecification(),
			//new Java2PcmWithSeffstatmantsChangePropagationSpecification()
			//new Pcm2JavaIntegrationChangePropagationSpecification()
			//new Java2PcmChangePropagationSpecification()
			//new MyJava2PcmChangePropagationSpecification()
			new GitIntegrationChangePropagationSpecification()
	};

	
	private static Logger logger = Logger.getLogger("simpleLogger");
	
	private static IProject testProject;
	private static IWorkspace workspace;
	private static InternalVirtualModel virtualModel;
	private static TestUserInteraction testUserInteractor;

	private static GitRepository gitRepository;
	private static GitChangeApplier changeApplier;
	
	private static Map<String, RevCommit> commits = new HashMap<>();

	private static final String REVISION_0_dot_9 = "86daaf897513efce88573c10e06b71d5eb36ad1a";
	private static final String REVISION_1_dot_0 = "aae5baf3eb28e9c4bdffdbb4eab69ec012dc11a0";
	
	private static List<RevCommit> commitsList = new ArrayList<RevCommit>();
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws InvocationTargetException, InterruptedException, IOException,
			URISyntaxException, GitAPIException, CoreException {
		//get workspace
		workspace = ResourcesPlugin.getWorkspace();
        //copy test project into workspace
        testProject = ApplyingChangesTestUtil.importAndCopyProjectIntoWorkspace(workspace, testProjectName, testProjectPath);
        //copy git repository into workspace
        gitRepository = ApplyingChangesTestUtil.copyGitRepositoryIntoWorkspace(workspace, gitRepositoryPath);
        //Thread.sleep(10000);
        //create change applier for copied repository
        changeApplier = new GitChangeApplier(gitRepository);
        //integrate test project in Vitruv
        virtualModel = ApplyingChangesTestUtil.integrateProjectWithChangePropagationSpecificationWithExistingVSUM(testProject, changePropagationSpecifications, changeApplier, vitruviusMetaPath,  ResourcesPlugin.getWorkspace());
        //checkout and track branch
        gitRepository.checkoutAndTrackBranch(EuFpetersenCbsPc_integratedArea_fineGrained_commits.METHOD_HEADER_BRANCH_NAME);
        //get all commits from branch and save them in a Map. Commit hash as Key and commit itself as Value in the Map.
        List<RevCommit> commitsList = gitRepository.getAllCommitsFromBranch(EuFpetersenCbsPc_integratedArea_fineGrained_commits.METHOD_HEADER_BRANCH_NAME);
        for (RevCommit commit: commitsList) {
        	commits.put(commit.getName(), commit);
        }   
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		while (true) {
			System.out.println("All tests are done. Stop the programm manually");
			Thread.sleep(10000);
		}
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
	public void testChangeMethodHeader() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		
		testRenameMethodInInterface();
		testRenameMethodInClass();
		testChangeReturnTypeInInterfaceMethod();
		testChangeReturnTypeInClassMethod();
		testAddReturn0InClassMethod();
		testAddFinalModifierToClassMethod();
		testAddMethodParameterInInterface();
		testAddMethodParameterInClass();
	}
	

	private void testRenameMethodInInterface() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.INIT), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_INTERFACE), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_INTERFACE);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testRenameMethodInInterface() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	private void testRenameMethodInClass() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_INTERFACE), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_CLASS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_CLASS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testRenameMethodInClass() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	private void testChangeReturnTypeInInterfaceMethod() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_METHOD_IN_CLASS), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_INTERFACE_METHOD), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_INTERFACE_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testChangeReturnTypeInInterfaceMethod() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	private void testChangeReturnTypeInClassMethod() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_INTERFACE_METHOD), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_CLASS_METHOD), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_CLASS_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testChangeReturnTypeInClassMethod() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	private void testAddReturn0InClassMethod() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_RETURN_TYPE_IN_CLASS_METHOD), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_RETURN_0), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_RETURN_0);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testAddReturn0InClassMethod() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	private void testAddFinalModifierToClassMethod() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_RETURN_0), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_FINAL_TO_CLASS_METHOD), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_FINAL_TO_CLASS_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testAddFinalModifierToClassMethod() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	private void testAddMethodParameterInInterface() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_FINAL_TO_CLASS_METHOD), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_METHOD_PARAMETER_IN_INTERFACE), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_METHOD_PARAMETER_IN_INTERFACE);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("IDisplay.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testAddMethodParameterInInterface() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	private void testAddMethodParameterInClass() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_METHOD_PARAMETER_IN_INTERFACE), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_METHOD_PARAMETER_IN_CLASS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_METHOD_PARAMETER_IN_CLASS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("Display.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testAddClassAnnotaion() after adding class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testAddMethodParameterInClass() the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
}
