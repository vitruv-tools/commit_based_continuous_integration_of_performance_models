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
 */
public class MethodModifiersChangeTest /*extends ApplyingChangesFromGitTest*/ {

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

	
	//TODO
	
}
