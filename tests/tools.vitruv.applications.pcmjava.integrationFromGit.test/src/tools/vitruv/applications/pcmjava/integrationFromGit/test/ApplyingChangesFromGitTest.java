package tools.vitruv.applications.pcmjava.integrationFromGit.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.Bundle;

import mir.reactions.packageMappingIntegration.PackageMappingIntegrationChangePropagationSpecification;

import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.emftext.language.java.JavaClasspath;

import tools.vitruv.applications.pcmjava.linkingintegration.change2command.Java2PcmIntegrationChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.linkingintegration.change2command.Pcm2JavaIntegrationChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.linkingintegration.seffstatments.PcmJavaCorrespondenceModelForSeffTransformation;
import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaCorrespondenceModelTransformation;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.CodeIntegrationTest;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.CodeIntegrationUtils;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.DoneFlagProgressMonitor;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.Java2PcmChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.pojotransformations.pcm2java.Pcm2JavaChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.Java2PcmWithSeffstatmantsChangePropagationSpecification;
import tools.vitruv.applications.pcmjava.pojotransformations.java2pcm.Java2PcmUserSelection;
import tools.vitruv.applications.pcmjava.tests.util.Java2PcmTransformationTest;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitChangeApplier;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitRepository;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilder;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.domains.pcm.PcmNamespace;
import tools.vitruv.extensions.integration.correspondence.integration.IntegrationCorrespondence;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelViewFactory;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.Correspondence;
import tools.vitruv.framework.tuid.Tuid;
import tools.vitruv.framework.ui.monitorededitor.ProjectBuildUtils;
import tools.vitruv.framework.userinteraction.InteractionResultProvider;
import tools.vitruv.framework.userinteraction.InternalUserInteractor;
import tools.vitruv.framework.userinteraction.PredefinedInteractionResultProvider;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModelConfiguration;
import tools.vitruv.framework.vsum.VirtualModelImpl;
import tools.vitruv.framework.vsum.VirtualModelManager;
import tools.vitruv.testutils.TestUserInteraction;
import tools.vitruv.testutils.util.TestUtil;

/**
 * @author Ilia Chupakhin
 *
 */
public class ApplyingChangesFromGitTest /*extends Java2PcmTransformationTest*/ {


    protected String testProjectName = //"testAddParameter_Fri_Apr_24_18_45_38_CEST_2020";   
    								//"humanBeing"; 
    								"eu.fpetersen.cbs.pc";
    								//"project";
    								//"mediaStore";
    
    protected String testProjectPath =//"testProjects/vitruvius/projectToApplyCommitsOn/testAddParameter_Fri_Apr_24_18_45_38_CEST_2020";
    								//"testProjects/chupakhin/projectToApplyCommitsOn/humanBeing";
    								"testProjects/petersen/projectToApplyCommitsOn/eu.fpetersen.cbs.pc";
    								//"testProjects/mediastore/projectToApplyCommitsOn/project";
									//"testProjects/myMediastore/projectToApplyCommitsOn/mediaStore";    
 
    
    protected String gitRepositoryPath = //"testProjects/vitruvius/projectWithCommits";
    								   //"testProjects/chupakhin/projectWithCommits";
    									"testProjects/petersen/projectWithCommits";
    									//"testProjects/mediastore/projectWithCommits";
    									//"testProjects/myMediastore/projectWithCommits";
    

	protected ChangePropagationSpecification[] changePropagationSpecifications = {
		//new PackageMappingIntegrationChangePropagationSpecification()
		//new Java2PcmIntegrationChangePropagationSpecification(),
		new Java2PcmWithSeffstatmantsChangePropagationSpecification()	
		//new Pcm2JavaIntegrationChangePropagationSpecification()
		//new Java2PcmChangePropagationSpecification()
		//new MyJava2PcmChangePropagationSpecification()
	};
    
	//@SuppressWarnings("unused")
	protected static final Logger logger = Logger.getLogger(CodeIntegrationTest.class.getSimpleName());
    protected IProject testProject;
    
    protected String clonedGitRepositoryPath = "temporaryGitRepository";
    
    protected IWorkspace workspace;
    protected InternalVirtualModel virtualModel;
    protected TestUserInteraction testUserInteractor;
	
    protected GitRepository gitRepository;
    protected GitChangeApplier changeApplier;
 
    

    @Before
    public void beforeTest() throws InvocationTargetException, InterruptedException, IOException, URISyntaxException, GitAPIException, CoreException {
        workspace = ResourcesPlugin.getWorkspace();
        //copy test project into workspace
        testProject = ApplyingChangesTestUtil.importAndCopyProjectIntoWorkspace(workspace, testProjectName, testProjectPath);
        //copy git repository into workspace
        gitRepository = ApplyingChangesTestUtil.copyGitRepositoryIntoWorkspace(workspace, gitRepositoryPath);
        //create change applier for copied repository
        changeApplier = new GitChangeApplier(gitRepository);
        //integrate test project in Vitruv
        virtualModel = ApplyingChangesTestUtil.integrateProjectWithChangePropagationSpecification(testProject, changePropagationSpecifications, changeApplier);
        // This is necessary because otherwise Maven tests will fail as
		// resources from previous
		// tests are still in the classpath and accidentally resolved
        JavaClasspath.reset();
		// Pipe JaMoPP error output to null
		java.lang.System.setErr(null);
    }
    
    
	@Test
	public void testApplyCommits() throws NoHeadException, GitAPIException, IOException, CoreException, InterruptedException {
		CorrespondenceModel correspModel = virtualModel.getCorrespondenceModel();
		List<Correspondence> correspondences = correspModel.getAllCorrespondences();
		
		
		List<RevCommit> commits = gitRepository.getAllCommits();
		
		/*
		testUserInteractor.addNextMultiSelection(new int [] {
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection(),
				Java2PcmUserSelection.SELECT_BASIC_COMPONENT.getSelection()});
		*/				
		for (int i = commits.size() - 1; i > 0; i--) {
			changeApplier.applyChangesFromCommit(commits.get(i/*1*/), commits.get(i - 1/*0*/), testProject);
			Thread.sleep(10000);
		}
		
		System.out.println("dummy line");
		
		CorrespondenceModel corModel = virtualModel.getCorrespondenceModel();
		
		
		while(true) {
			Thread.sleep(10000);
			System.out.println("All tests are done. Stop the programm manually");
		}
		
		//CorrespondenceModelViewFactory
		//corModel.getView(IntegrationCorrespondence.class);
	}
    
/*
	public void afterTest() {
		// Remove PCM Java Builder
		final VitruviusJavaBuilderApplicator pcmJavaRemoveBuilder = new VitruviusJavaBuilderApplicator();
		pcmJavaRemoveBuilder.removeBuilderFromProject(testProject);
	}
*/	

		
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


	 

}
