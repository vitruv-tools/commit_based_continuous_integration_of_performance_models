/**
 * 
 */
package tools.vitruv.applications.pcmjava.integrationFromGit.test.nonIntegratedArea;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.vitruv.applications.pcmjava.integrationFromGit.GitRepository;
import tools.vitruv.applications.pcmjava.integrationFromGit.test.ApplyingChangesFromGitTest;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.testutils.TestUserInteraction;

/**
 * @author Ilia Chupakhin
 *
 */
public class ChangeClassTest extends ApplyingChangesFromGitTest {
	
	protected final String BRANCH_NAME = "nonIntegratedArea_ChangeClassTest";
	
	@Override
	public void beforeTest() throws InvocationTargetException, InterruptedException, IOException, URISyntaxException, GitAPIException, CoreException {
		super.beforeTest();
		this.gitRepository.checkoutAndTrackBranch(BRANCH_NAME);
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
	public void test() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		CorrespondenceModel correspModel = virtualModel.getCorrespondenceModel();
		List<Correspondence> correspondences = correspModel.getAllCorrespondences();

		List<RevCommit> commits = gitRepository.getAllCommitsFromBranch(BRANCH_NAME);	
		
		
		for (int i = commits.size() - 1; i > 0; i--) {
			changeApplier.applyChangesFromCommit(commits.get(i), commits.get(i - 1), testProject);
			//Thread.sleep(10000);
		}
		
		/*
		changeApplier.applyChangesFromCommit(commits.get(7), commits.get(6), testProject);
		changeApplier.applyChangesFromCommit(commits.get(6), commits.get(5), testProject);
		changeApplier.applyChangesFromCommit(commits.get(5), commits.get(2), testProject);
		changeApplier.applyChangesFromCommit(commits.get(2), commits.get(1), testProject);
		changeApplier.applyChangesFromCommit(commits.get(1), commits.get(4), testProject);
		changeApplier.applyChangesFromCommit(commits.get(4), commits.get(3), testProject);
		changeApplier.applyChangesFromCommit(commits.get(3), commits.get(0), testProject);
		*/
		
		System.out.println("dummy line");

		
		
		while(true) {
			Thread.sleep(10000);
			System.out.println("All tests are done. Stop the programm manually");
		}
	}

}
