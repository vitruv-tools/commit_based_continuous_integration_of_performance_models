package tools.vitruv.applications.pcmjava.linkingintegration.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.CodeIntegrationUtils;
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
import tools.vitruv.framework.vsum.VirtualModelManager;

public class CodeIntegrationTest {
	@SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CodeIntegrationTest.class.getSimpleName());

    private static final String META_PROJECT_NAME = "vitruvius.meta";
    private IProject testProject;
    private IWorkspace workspace;
    private InternalVirtualModel virtualModel;
    
    @Before
    public void beforeTest() throws InvocationTargetException, InterruptedException, IOException, URISyntaxException {
        this.workspace = ResourcesPlugin.getWorkspace();

        CodeIntegrationUtils.importTestProjectFromBundleData(this.workspace, getTestProjectName(), getTestBundleName(), getTestSourceAndModelFolder());

        final IProject project = this.workspace.getRoot().getProject(this.getTestProjectName());
        assert project != null;
        this.testProject = project;
    }

    protected String getTestProjectName() {
        return CodeIntegrationTestCBSNamespace.TEST_PROJECT_NAME;
    }

    protected String getTestBundleName() {
        return CodeIntegrationTestCBSNamespace.TEST_BUNDLE_NAME;
    }

    protected String getTestSourceAndModelFolder() {
        return CodeIntegrationTestCBSNamespace.SOURCE_CODE_PATH;
    }

    protected IProject getTestProject() {
        return this.testProject;
    }


    @After
    public void afterTest() throws CoreException, InterruptedException {
        // Delete test project
        final IProject testProject = this.workspace.getRoot().getProject(this.getTestProjectName());
        if (testProject.exists()) {
            final DoneFlagProgressMonitor progress = new DoneFlagProgressMonitor();
            testProject.delete(true, true, progress);
            /*
            while (!progress.isDone()) {
                Thread.sleep(100);
            }
            */
        }

        // Delete vitruvius.meta project
        final IProject metaProject = this.workspace.getRoot().getProject(META_PROJECT_NAME);
        if (metaProject.exists()) {
            final DoneFlagProgressMonitor progress = new DoneFlagProgressMonitor();
            metaProject.delete(true, true, progress);
            /*
            while (!progress.isDone()) {
                Thread.sleep(100);
            }
            */
        }
    }

    @Test
    public void testStandardCodeIntegration() throws Throwable {
        CodeIntegrationUtils.integratProject(getTestProject());

        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final File metaProject = new File(workspace.getRoot().getLocation().toFile(), META_PROJECT_NAME);
        DoneFlagProgressMonitor progress = new DoneFlagProgressMonitor();
        Assert.assertNotNull(metaProject);

        final File corrFolder = new File(metaProject, "correspondence");
        Assert.assertNotNull(corrFolder);
        final File correspondenceFile = new File(corrFolder, "Correspondences.correspondence");
        Assert.assertNotNull(correspondenceFile);
        
        final File vsumFolder = new File(metaProject, "vsum");
        Assert.assertNotNull(vsumFolder);

/*//Disabled by Ilia Chupakhin
        VirtualModelConfiguration config = new VirtualModelConfiguration();
        List<VitruvDomain> metamodels = new ArrayList<VitruvDomain>();
        metamodels.add(new PcmDomainProvider().getDomain());
        metamodels.add(new JavaDomainProvider().getDomain());
        
        for (VitruvDomain metamodel : metamodels) {
        	config.addMetamodel(metamodel);
        }
*/        
        
        File vsumFile = new File(workspace.getRoot().getLocation().toFile(), META_PROJECT_NAME);
        
        //Added by Ilia Chupakhin
        //start**********
        VirtualModelManager virtualModelManager = VirtualModelManager.getInstance();
        virtualModel = virtualModelManager.getVirtualModel(vsumFile);
        //end************
        
		
        //Disabled by Ilia Chupakhin
        //virtualModel = new VirtualModelImpl(vsumFile, UserInteractionFactory.instance.createDialogUserInteractor(), config);
        
        
        // add PCM Java Builder to Project under test
        final VitruviusJavaBuilderApplicator pcmJavaBuilder = new VitruviusJavaBuilderApplicator();
        pcmJavaBuilder.addToProject(this.testProject, vsumFile, Collections.singletonList(PcmNamespace.REPOSITORY_FILE_EXTENSION));
        // build the project
        progress = new DoneFlagProgressMonitor();
        this.testProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, VitruviusJavaBuilder.BUILDER_ID,
                new HashMap<String, String>(), progress);
        
        //Disabled by Ilia Chupakhin
        /*
        while (!progress.isDone()) {
            Thread.sleep(100);
        }
         */
        this.assertStandardCodeIntegrationTest();

    }

    protected void assertStandardCodeIntegrationTest() throws Throwable {
        final CorrespondenceModel ci = this.getCorrespondenceModel();
        // TODO check if correspondences are correct
        final List<Correspondence> correspondences = ci.getAllCorrespondences();
        Assert.assertNotNull(correspondences);
        Assert.assertFalse(correspondences.isEmpty());

        final Tuid frameCodeTuid = Tuid.getInstance(
                "http://www.emftext.org/java#platform:/resource/eu.fpetersen.cbs.pc/src/eu/fpetersen/cbs/pc/data/Frame.java#classifier-_-Frame");
        final List<Correspondence> frameCodeCorrs = correspondences.stream().
        		filter(correspondence -> correspondence.getATuids().contains(frameCodeTuid) || correspondence.getBTuids().contains(frameCodeTuid)).collect(Collectors.toList());

        Assert.assertNotNull(frameCodeCorrs);
        Assert.assertFalse(frameCodeCorrs.isEmpty());
        Assert.assertEquals(1, frameCodeCorrs.size());

        final Correspondence frameCorr = frameCodeCorrs.iterator().next();
        if (frameCorr.getATuids().contains(frameCodeTuid)) {
            final EList<Tuid> bs = frameCorr.getBTuids();
            Assert.assertNotNull(bs);
            Assert.assertEquals(1, bs.size());

            final Tuid b = bs.get(0);
            Assert.assertEquals(
                    Tuid.getInstance(
                            "http://palladiosimulator.org/PalladioComponentModel/5.2#platform:/resource/eu.fpetersen.cbs.pc/model/internal_architecture_model.repository#<root>-_-Repository-_-id=_auhdcMWvEeWLAeSW2tt_XQ#dataTypes__Repository-_-CompositeDataType-_-id=_auwuAMWvEeWLAeSW2tt_XQ"),
                    b);
        } else if (frameCorr.getBTuids().contains(frameCodeTuid)) {
            final EList<Tuid> as = frameCorr.getATuids();
            Assert.assertNotNull(as);
            Assert.assertEquals(1, as.size());

            final Tuid a = as.get(0);
            Assert.assertEquals(
                    Tuid.getInstance(
                            "http://palladiosimulator.org/PalladioComponentModel/5.2#platform:/resource/eu.fpetersen.cbs.pc/model/internal_architecture_model.repository#<root>-_-Repository-_-id=_auhdcMWvEeWLAeSW2tt_XQ#dataTypes__Repository-_-CompositeDataType-_-id=_auwuAMWvEeWLAeSW2tt_XQ"),
                    a);
        } else {
            Assert.assertTrue(false);
        }
    }

    protected CorrespondenceModel getCorrespondenceModel() throws Throwable {
        final CorrespondenceModel corresponcenceInstance = virtualModel
                .getCorrespondenceModel();
        return corresponcenceInstance;
    }

    /**
     * Thread-safe simple progress monitor for knowing when a job is done.
     *
     */
    private class DoneFlagProgressMonitor extends NullProgressMonitor {

        private final AtomicBoolean isDone = new AtomicBoolean(false);

        @Override
        public void done() {
            this.isDone.set(true);
        }

        public boolean isDone() {
            return this.isDone.get();
        }

    }

}
