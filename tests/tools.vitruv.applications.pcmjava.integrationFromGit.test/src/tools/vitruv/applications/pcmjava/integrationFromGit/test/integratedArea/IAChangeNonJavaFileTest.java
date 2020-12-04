package tools.vitruv.applications.pcmjava.integrationFromGit.test.integratedArea;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * Test for changing non-java files 
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 */
public class IAChangeNonJavaFileTest {

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
        //checkout and track branch
        gitRepository.checkoutAndTrackBranch(EuFpetersenCbsPc_integratedArea_fineGrained_commits.NON_JAVA_FILES_BRANCH_NAME);
        //get all commits from branch and save them in a Map. Commit hash as Key and commit itself as Value in the Map.
        List<RevCommit> commitsList = gitRepository.getAllCommitsFromBranch(EuFpetersenCbsPc_integratedArea_fineGrained_commits.NON_JAVA_FILES_BRANCH_NAME);
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
		//projectFromGitRepository.delete(true, null);
		//FileUtils.deleteDirectory(new File(workspace.getRoot().getLocation().toFile(), "clonedGitRepositories"));
		// This is necessary because otherwise Maven tests will fail as
		// resources from previous tests are still in the classpath and accidentally resolved
		JavaClasspath.reset();
	}
	*/
	
	//Vitruv does not react to add/change/remove non-java files.
	//Therefore 'Waiting for synchronization timed out' while running is normal. 
	//The test will continue automatically after timeout. But make sure there is no user dialog waiting for user reaction.
	@Test
	public void testCreateDeleteNonJavaFile() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		testCreateFolderAndFile();
		testRenameFile();
		testChangeFileContent();
		testCopyFile();
		testRemoveFile();
	}
	
	
	private void testCreateFolderAndFile() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.INIT), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_FOLDER_AND_FILE), testProject);	
		//Find created file and folder
		IFolder createdFolder = testProject.getFolder(new Path("nonJavaFilesFolder")); 
		IFile createdFile = testProject.getFile(new Path("nonJavaFilesFolder/nonJavaFile.txt"));
		
		assertTrue("in testCreateFolderAndFile() folder does not exist", createdFolder.exists());
		assertTrue("in testCreateFolderAndFile() file does not exist", createdFile.exists());
	}	
	
	
	private void testRenameFile() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.ADD_FOLDER_AND_FILE), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_FILE), testProject);	
		//Find files with the old and new name
		IFile fileWithNewName = testProject.getFile(new Path("nonJavaFilesFolder/nonJavaFileRenamed.txt"));
		IFile fileWithOldName = testProject.getFile(new Path("nonJavaFilesFolder/nonJavaFile.txt"));
		
		assertFalse("in testRenameFile() file with old name still exists", fileWithOldName.exists());
		assertTrue("in testRenameFile() renamed file does not exist", fileWithNewName.exists());
	}
	
	
	private void testChangeFileContent() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.RENAME_FILE), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_FILE_CONTENT), testProject);	
		//Find changed file
		IFile changedFile = testProject.getFile(new Path("nonJavaFilesFolder/nonJavaFileRenamed.txt"));
		
		assertTrue("in testChangeFileContent() file does not exist", changedFile.exists());
		
		String fileContnent = ApplyingChangesTestUtil.getFileContent(changedFile);
		
		assertTrue("in testChangeFileContent() file content isn't correct", fileContnent.equals("newContent"));
		
	}
	

	private void testCopyFile() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.CHANGE_FILE_CONTENT), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.COPY_FILE), testProject);	
		//Find copied file
		IFile copiedFile = testProject.getFile(new Path("src/nonJavaFileRenamed.txt"));
		
		assertTrue("in testCopyFile() file does not exist", copiedFile.exists());
		
		String fileContnent = ApplyingChangesTestUtil.getFileContent(copiedFile);
		
		assertTrue("in testCopyFile() file content isn't correct", fileContnent.equals("newContent"));
		
	}
	
	
	private void testRemoveFile() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		//Apply changes
		changeApplier.applyChangesFromCommit(commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.COPY_FILE), commits.get(EuFpetersenCbsPc_integratedArea_fineGrained_commits.REMOVE_FILE), testProject);	
		//Find files with the old and new name
		IFile removedFile = testProject.getFile(new Path("src/nonJavaFileRenamed.txt"));
		
		assertFalse("in testCopyFile() file does not exist", removedFile.exists());
	}
	
}
