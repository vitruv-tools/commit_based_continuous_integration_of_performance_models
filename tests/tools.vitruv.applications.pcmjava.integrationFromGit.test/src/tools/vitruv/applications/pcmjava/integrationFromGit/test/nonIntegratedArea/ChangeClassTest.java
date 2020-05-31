package tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import tools.vitruv.applications.pcmjava.integrationFromGit.GitChangeApplier;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitRepository;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.ApplyingChangesFromGitTest;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.ApplyingChangesTestUtil;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.commits.EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.CodeIntegrationTest;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmWithSeffstatmantsChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * @author Ilia Chupakhin
 * 
 * TODO: Delete this class. All tests from this class were separated into several classes. 
 */
public class ChangeClassTest /*extends ApplyingChangesFromGitTest*/ {

	private static String testProjectName = // "testAddParameter_Fri_Apr_24_18_45_38_CEST_2020";
			// "humanBeing";
			"eu.fpetersen.cbs.pc";
		// "project";
		// "mediaStore";

	private static String testProjectPath = // "testProjects/vitruvius/projectToApplyCommitsOn/testAddParameter_Fri_Apr_24_18_45_38_CEST_2020";
			// "testProjects/chupakhin/projectToApplyCommitsOn/humanBeing";
			"testProjects/petersen/projectToApplyCommitsOn/eu.fpetersen.cbs.pc";
		// "testProjects/mediastore/projectToApplyCommitsOn/project";
		// "testProjects/myMediastore/projectToApplyCommitsOn/mediaStore";

	private static String gitRepositoryPath = // "testProjects/vitruvius/projectWithCommits";
			// "testProjects/chupakhin/projectWithCommits";
			"testProjects/petersen/projectWithCommits";
		// "testProjects/mediastore/projectWithCommits";
		// "testProjects/myMediastore/projectWithCommits";

	private static ChangePropagationSpecification[] changePropagationSpecifications = {
			//new PackageMappingIntegrationChangePropagationSpecification()
			//new Java2PcmIntegrationChangePropagationSpecification(),
			new Java2PcmWithSeffstatmantsChangePropagationSpecification()
			//new Pcm2JavaIntegrationChangePropagationSpecification()
			//new Java2PcmChangePropagationSpecification()
			//new MyJava2PcmChangePropagationSpecification()
	};

	private static Logger logger = Logger.getLogger(CodeIntegrationTest.class.getSimpleName());
	private static IProject testProject;
	private static IWorkspace workspace;
	private static InternalVirtualModel virtualModel;
	private static TestUserInteraction testUserInteractor;

	private static GitRepository gitRepository;
	private static GitChangeApplier changeApplier;
	
	private static Map<String, RevCommit> commits = new HashMap<>();

/*
	@Override
	public void beforeTest() throws InvocationTargetException, InterruptedException, IOException, URISyntaxException,
			GitAPIException, CoreException {
		super.beforeTest();
		this.gitRepository.checkoutAndTrackBranch(BRANCH_NAME);
	}
*/
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
        virtualModel = ApplyingChangesTestUtil.integrateProjectWithChangePropagationSpecification(testProject, changePropagationSpecifications, changeApplier);
        //checkout and track branch
        gitRepository.checkoutAndTrackBranch(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.BRANCH_NAME);
        //get all commits from branch and save them in a Map. Commit hash as Key and commit itself as Value in the Map.
        List<RevCommit> commitsList = gitRepository.getAllCommitsFromBranch(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.BRANCH_NAME);
        for (RevCommit commit: commitsList) {
        	commits.put(commit.getName(), commit);
        }
        //prepare a non-integrated area. The following steps are necessary:
    	//create nonIntegratedPackage in src folder
    	changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.INIT), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_NON_INTEGRATED_PACKAGE), testProject);
    	//create packages contracts and datatypes in nonIntegratedPackage
    	changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_NON_INTEGRATED_PACKAGE), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_CONTRACTS_DATATYPES), testProject);
    	//create package FirstClass in nonIntegratedPackage
    	changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_CONTRACTS_DATATYPES), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_PACKAGE), testProject);
    	//create FirstClassImpl.java in package FirstClass
    	changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_PACKAGE), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_IMPL), testProject);	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		while (true) {
			Thread.sleep(10000);
			System.out.println("All tests are done. Stop the programm manually");
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
	public void testClassAnnotation() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		testAddClassAnnotation();
		testChangeClassAnnotation();
		//TODO:Vitruv does not react to removing class annotation
		testRemoveClassAnnotation();
	}
	
	
	@Test
	public void testImport() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		testAddImport();
		testChangeImport();
		testRemoveClassImport();
	}
	
	
	@Test
	public void testClassModifier() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		testRemovePublicClassModifier();
		testAddFinalClassModifier();
		testChangeFinalToAbstractClassModifier();
		testChangeAbstractToPublicClassModifier();
	}
	
	
	@Test
	public void testExtends() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		testAddFirstImportForExtends();
		testAddExtends();
		testAddSecondImportForExtends();
		testChangeExtends();
		//TODO:Vitruv does not react to removing extends statement
		testRemoveExtends();
		testRemoveSecondImportForExtends();
		testRemoveFirstImportForExtends();
	}
	
	
	@Test
	public void testImplements() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		testAddFirstInterfaceForImplements();
		testAddMethodInFirstInterfaceForImplements();
		testAddFirstImportForImplements();
		testAddImplementsAndMethod();
		testAddSecondInterfaceForImplements();
		testAddMethodInSecondInterfaceForImplements();
		testAddSecondImportForImplements();
		//TODO: Vitruv does NOT remove the corrspondence OperationProvidedRole for FirstInterface in FirstClassImpl, 
		//although FirstClassImpl does not implement FirstInterface anymore 
		testChangeImplementsAndAddMethod();
		testRemoveImplements();
		testRemoveBothMethodsForImplements();
		testRemoveBothImportsForImplements();
		
	}
	

	private void testAddFirstInterfaceForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_IMPL), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_INTERFACE_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_INTERFACE_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddFirstInterfaceForImplements() after adding interface JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}

	
	private void testAddMethodInFirstInterfaceForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException   {
		//Add method in interface
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_INTERFACE_FOR_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_METHOD_IN_FIRST_INTERFACE_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_METHOD_IN_FIRST_INTERFACE_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddMethodInFirstInterfaceForImplements() after adding method in interface JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
		
	}
	
	private void testAddFirstImportForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Add first import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_METHOD_IN_FIRST_INTERFACE_FOR_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_IMPORT_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_IMPORT_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddFirstImportForImplements() after adding import statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	private void testAddImplementsAndMethod()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Add implements statement and implemented method in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_IMPORT_FOR_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_IMPLEMENTS_AND_METHOD), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_IMPLEMENTS_AND_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddImplements() after adding implements statement and method JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);		
	}

	
	private void testAddSecondInterfaceForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_IMPLEMENTS_AND_METHOD), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_INTERFACE_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_INTERFACE_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddSecondInterfaceForImplements() after adding interface JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testAddMethodInSecondInterfaceForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_INTERFACE_FOR_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_METHOD_IN_SECOND_INTERFACE_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_METHOD_IN_SECOND_INTERFACE_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddMethodInSecondInterfaceForImplements() after adding method in interface JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testAddSecondImportForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_METHOD_IN_SECOND_INTERFACE_FOR_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_IMPORT_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_IMPORT_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddSecondImportForImplements() after adding import statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testChangeImplementsAndAddMethod()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_IMPORT_FOR_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_IMPLEMENTS_AND_ADD_METHOD), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_IMPLEMENTS_AND_ADD_METHOD);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testChangeImplementsAndAddMethod() after changing import statement and adding implemented method JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testRemoveImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_IMPLEMENTS_AND_ADD_METHOD), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemoveImplements() after removing implements statemen JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testRemoveBothMethodsForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_BOTH_METHODS_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_BOTH_METHODS_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemoveBothMethodsForImplements() after removing both methods JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	
	
	private void testRemoveBothImportsForImplements()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add interface in contracts package
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_BOTH_METHODS_FOR_IMPLEMENTS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_BOTH_IMPORTS_FOR_IMPLEMENTS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_BOTH_IMPORTS_FOR_IMPLEMENTS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemoveBothImportsForImplements() after removing both imports JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}
	


	private void testAddFirstImportForExtends() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Add first import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_IMPL), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_IMPORT_FOR_EXTENDS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_IMPORT_FOR_EXTENDS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddFirstImportForExtends() after adding import statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	private void testAddExtends() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Add extends statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_IMPORT_FOR_EXTENDS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_EXTENDS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_EXTENDS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddExtends() after adding extends statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	private void testAddSecondImportForExtends() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Add second import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_EXTENDS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_IMPORT_FOR_EXTENDS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_IMPORT_FOR_EXTENDS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddSecondImportForExtends() after adding import statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);

		
	}
	
	private void testChangeExtends() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Change extends statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_SECOND_IMPORT_FOR_EXTENDS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_EXTENDS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_EXTENDS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testChangeExtends() after changing extends statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);

		
	}
	
	private void testRemoveExtends() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Remove extends statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_EXTENDS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_EXTENDS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_EXTENDS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemoveExtends() after removing extends statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);	
	}
	
	private void testRemoveSecondImportForExtends() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Remove second import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_EXTENDS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_SECOND_IMPORT_FOR_EXTENDS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_SECOND_IMPORT_FOR_EXTENDS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemoveSecondImportForExtends() after removing import statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);

		
	}
	
	private void testRemoveFirstImportForExtends() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException  {
		//Remove first import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_SECOND_IMPORT_FOR_EXTENDS), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_FIRST_IMPORT_FOR_EXTENDS), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_FIRST_IMPORT_FOR_EXTENDS);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemoveFirstImportForExtends() after removing import statement JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}


	private void testRemovePublicClassModifier()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Remove import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_IMPL), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_PUBLIC), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_PUBLIC);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemovePublicClassModifier() after removing 'public' class modifier JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);

	}	
	
	private void testAddFinalClassModifier()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Remove import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_PUBLIC), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FINAL), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FINAL);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddFinalClassModifier() after adding 'final' class modifier the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	
	}
	
	
	private void testChangeFinalToAbstractClassModifier() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Remove import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FINAL), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_FINAL_TO_ABSTRACT), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_FINAL_TO_ABSTRACT);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testChangeFinalToAbstractClassModifier() after changing 'final' class modifier to 'abstract' the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);

	}
	
	
	private void testChangeAbstractToPublicClassModifier()  throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Remove import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_FINAL_TO_ABSTRACT), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_ABSTRACT_TO_PUBLIC), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_ABSTRACT_TO_PUBLIC);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testChangeAbstractToPublicClassModifier() after changing 'abstract' class modifier to 'public' the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	
	}

	
	private void testAddImport() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Add import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_IMPL), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_IMPORT), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_IMPORT);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddImport() after adding import the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
		
	}


	private void testChangeImport() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Change import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_IMPORT), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_IMPORT), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_IMPORT);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testChangeClassAnnotaion() after changing import the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);		
	}

	
	private void testRemoveClassImport() throws CoreException, InterruptedException, IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//Remove import statement in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_IMPORT), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_IMPORT), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_IMPORT);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testRemoveClassImport() after removing import the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
			
	}
	
	private void testAddClassAnnotation() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Add class annotation in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_FIRST_CLASS_IMPL), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_CLASS_ANNOTATION), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_CLASS_ANNOTATION);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
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
		assertTrue("In testAddClassAnnotaion() after adding class annotation the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}	
	
	
	
	private void testChangeClassAnnotation() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Change class annotation in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.ADD_CLASS_ANNOTATION), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_CLASS_ANNOTATION), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_CLASS_ANNOTATION);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testChangeClassAnnotaion() after changing class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testChangeClassAnnotaion() after changing class annotation the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	
	
	//TODO:Vitruv does not react to removing class annotation
	private void testRemoveClassAnnotation() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Remove class annotation in FirstClassImpl.java
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.CHANGE_CLASS_ANNOTATION), commits.get(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_CLASS_ANNOTATION), testProject);	
		//Checkout the repository on the certain commit
		gitRepository.checkoutFromCommitId(EuFpetersenCbsPc_nonIntegratedArea_classChanges_fineGrained_Commits.REMOVE_CLASS_ANNOTATION);
		//Create temporary model from project from git repository. It does NOT add the created project to the workspace.
		IProject projectFromGitRepository = ApplyingChangesTestUtil.createIProject(workspace, workspace.getRoot().getLocation().toString() + "/clonedGitRepositories/" + testProjectName + ".withGit");
		//Get the changed compilation unit and the compilation unit from git repository to compare
		ICompilationUnit compUnitFromGit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", projectFromGitRepository);
		ICompilationUnit compUnitChanged = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName("FirstClassImpl.java", testProject);
		//Compare the buffers from compilation units
		boolean compUnitsBuffersAreEqual = ApplyingChangesTestUtil.compareCompilationUnitsBuffers(compUnitChanged, compUnitFromGit, true);
		//Compare JaMoPP-Models 
		boolean jamoppClassifiersAreEqual = ApplyingChangesTestUtil.compareJaMoPPCompilationUnits(compUnitChanged, compUnitFromGit, virtualModel);
		//TODO: Compare PCM Repository Models
		//TODO: comparePCMBasicComponents(...) does NOT work
		//boolean pcmBasicComponentsAreEqual = ApplyingChangesTestUtil.comparePCMBasicComponents(compUnitChanged, compUnitReference);
		//Remove temporary project
		//projectFromGitRepository.delete(true, new NullProgressMonitor());
		
		//assertTrue("In testRemoveClassAnnotaion() after removing class annotation the buffers of the compilation units are NOT equal, but they should be", compUnitsBuffersAreEqual);
		assertTrue("In testRemoveClassAnnotaion() after removing class annotation the JaMoPP-models are NOT equal, but they should be", jamoppClassifiersAreEqual);
	}
	

}
