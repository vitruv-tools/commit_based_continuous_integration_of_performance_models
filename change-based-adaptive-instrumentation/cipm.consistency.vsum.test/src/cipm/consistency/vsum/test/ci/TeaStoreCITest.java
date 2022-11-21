package cipm.consistency.vsum.test.ci;

import cipm.consistency.commitintegration.diff.util.ComparisonBasedJaccardCoefficientCalculator;
import cipm.consistency.commitintegration.diff.util.pcm.PCMModelComparator;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.git.impl.JavaDiffComputation;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import cipm.consistency.commitintegration.lang.java.JavaLanguageSpecification;
import java.io.IOException;
import java.nio.file.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * A test class for the TeaStore.
 * 
 * @author Martin Armbruster
 */
public class TeaStoreCITest extends InstrumentingCommitIntegrationController {
    private static final String COMMIT_TAG_1_0 = "b0d046e178dbaab7e045de57c01795ce5d1dac92";
    private static final String COMMIT_TAG_1_1 = "77733d9c6ab6680c6cc460c631cd408a588a595c";
    private static final String COMMIT_TAG_1_2 = "53c6efa1dca64a87e536d8c5a3dcc3c12ad933b5";
    private static final String COMMIT_TAG_1_2_1 = "f8f13f4390f80d3dc8adb0a6167938a688ddb45e";
    private static final String COMMIT_TAG_1_3 = "745469e55fad8a801a92b0be96dc009acbe7e3fb";
    private static final String COMMIT_TAG_1_3_1 = "de69e957597d20d4be17fc7db2a0aa2fb3a414f7";

    @Override
    public GitRepositoryWrapper initializeGitRepositoryWrapper() throws NoHeadException, InvalidRemoteException, TransportException, IOException, GitAPIException {
        return (new GitRepositoryWrapper(getLanguageSpec(), new JavaDiffComputation()))
            .withRemoteRepositoryCopy(getRootPath().resolve("repo"), "https://github.com/DescartesResearch/TeaStore")
            .initialize();
    }

    @Override
    public Path getSettingsPath() {
        return Path.of("teastore-exec-files", "settings.properties");
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_0Integration() throws Exception {
        // Integrates TeaStore version 1.0.
        executePropagationAndEvaluation(null, COMMIT_TAG_1_0, 0);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_0To1_1Propagation() throws Exception {
        // Propagation of changes between TeaStore version 1.0 and 1.1.
        executePropagationAndEvaluation(COMMIT_TAG_1_0, COMMIT_TAG_1_1, 1);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStoreWithMultipleCommits1_0To1_1() throws GitAPIException, IOException, InterruptedException {
        propagateMultipleCommits(COMMIT_TAG_1_0, COMMIT_TAG_1_1);
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_1Integration() throws Exception {
        // Integrates TeaStore version 1.1.
        executePropagationAndEvaluation(null, COMMIT_TAG_1_1, 0);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_1To1_2Propagation() throws Exception {
        // Propagation of changes between TeaStore version 1.1 and 1.2.
        executePropagationAndEvaluation(COMMIT_TAG_1_1, COMMIT_TAG_1_2, 1);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStoreWithMultipleCommits1_1To_1_2() throws Exception {
        propagateMultipleCommits(COMMIT_TAG_1_1, COMMIT_TAG_1_2);
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_2Integration() throws Exception {
        // Integrates TeaStore version 1.2.
        executePropagationAndEvaluation(null, COMMIT_TAG_1_2, 0);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_2To_1_2_1Propagation() throws Exception {
        // Propagation of changes between TeaStore version 1.2 and 1.2.1.
        executePropagationAndEvaluation(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1, 1);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStoreWithMultipleCommits1_2To1_2_1() throws GitAPIException, IOException, InterruptedException {
        propagateMultipleCommits(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1);
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_2_1Integration() throws Exception {
        // Integrates TeaStore version 1.2.1.
        executePropagationAndEvaluation(null, COMMIT_TAG_1_2_1, 0);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_2_1To_1_3Propagation() throws Exception {
        // Propagation of changes between TeaStore version 1.2.1 and 1.3.
        executePropagationAndEvaluation(COMMIT_TAG_1_2_1, COMMIT_TAG_1_3, 1);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStoreWithMultipleCommits1_2_1To1_3() throws GitAPIException, IOException, InterruptedException {
        propagateMultipleCommits(COMMIT_TAG_1_2_1, COMMIT_TAG_1_3);
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_3Integration() throws Exception {
        // Integrates TeaStore version 1.3.
        executePropagationAndEvaluation(null, COMMIT_TAG_1_3, 0);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_3To_1_3_1Propagation() throws Exception {
        // Propagation of changes between TeaStore version 1.3 and 1.3.1.
        executePropagationAndEvaluation(COMMIT_TAG_1_3, COMMIT_TAG_1_3_1, 1);
//		performIndependentEvaluation();
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStoreWithMultipleCommits1_3To1_3_1() throws GitAPIException, IOException, InterruptedException {
        propagateMultipleCommits(COMMIT_TAG_1_3, COMMIT_TAG_1_3_1);
    }

    @Disabled("Only one test case should run at once.")
    @Test
    public void testTeaStore1_3_1Integration() throws Exception {
        // Integrates TeaStore version 1.3.1.
        executePropagationAndEvaluation(null, COMMIT_TAG_1_3_1, 0);
//		performIndependentEvaluation();
    }

    @Disabled("Was previously deactivated")
    @Test
    public void testTemplateForPCMRepositoryComparison() {
        ResourceSet set = new ResourceSetImpl();
        Resource res1 = set.getResource(URI.createFileURI("path1"), true);
        Resource res2 = set.getResource(URI.createFileURI("path2"), true);
        var comp = PCMModelComparator.compareRepositoryModels(res1, res2);
        var res = ComparisonBasedJaccardCoefficientCalculator.calculateJaccardCoefficient(comp);
        System.out.println(res);
    }

    @Override
    public LanguageSpecification getLanguageSpec() {
        return new JavaLanguageSpecification();
    }
}
