package cipm.consistency.vsum.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;

/**
 * A test class for the TeaStore.
 * 
 * @author Martin Armbruster
 */
public class TeaStoreCITest extends AbstractCITest {
	private static final Logger logger = Logger.getLogger("cipm." + TeaStoreCITest.class.getSimpleName());
	private static final String COMMIT_TAG_1_0 = "b0d046e178dbaab7e045de57c01795ce5d1dac92";
	private static final String COMMIT_TAG_1_1 = "77733d9c6ab6680c6cc460c631cd408a588a595c";
	private static final String COMMIT_TAG_1_2 = "53c6efa1dca64a87e536d8c5a3dcc3c12ad933b5";
	private static final String COMMIT_TAG_1_2_1 = "f8f13f4390f80d3dc8adb0a6167938a688ddb45e";
	private static final String COMMIT_TAG_1_3 = "745469e55fad8a801a92b0be96dc009acbe7e3fb";
	private static final String COMMIT_TAG_1_3_1 = "de69e957597d20d4be17fc7db2a0aa2fb3a414f7";
	
	@Override
	protected String getTestPath() {
		return "target" + File.separator + "TeaStoreTest";
	}
	
	@Override
	protected String getRepositoryPath() {
		return "https://github.com/DescartesResearch/TeaStore";
	}
	
	@Override
	protected String getSettingsPath() {
		return "teastore-exec-files" + File.separator + "settings.properties";
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_0Integration() throws Exception {
		// Integrates TeaStore version 1.0.
		executePropagationAndEvaluation(null, COMMIT_TAG_1_0, 0);
		performIndependentEvaluation(null, COMMIT_TAG_1_0);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_0To1_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.0 and 1.1.
		executePropagationAndEvaluation(COMMIT_TAG_1_0, COMMIT_TAG_1_1, 1);
//		performIndependentEvaluation(COMMIT_TAG_1_0, COMMIT_TAG_1_1);
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
//		performIndependentEvaluation(null, COMMIT_TAG_1_1);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_1To1_2Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.1 and 1.2.
		executePropagationAndEvaluation(COMMIT_TAG_1_1, COMMIT_TAG_1_2, 1);
//		performIndependentEvaluation(COMMIT_TAG_1_1, COMMIT_TAG_1_2);
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
//		performIndependentEvaluation(null, COMMIT_TAG_1_2);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2To_1_2_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.2 and 1.2.1.
		executePropagationAndEvaluation(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1, 1);
//		performIndependentEvaluation(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1);
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
//		performIndependentEvaluation(null, COMMIT_TAG_1_2_1);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_2_1To_1_3Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.2.1 and 1.3.
		executePropagationAndEvaluation(COMMIT_TAG_1_2_1, COMMIT_TAG_1_3, 1);
//		performIndependentEvaluation(COMMIT_TAG_1_2_1, COMMIT_TAG_1_3);
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
//		performIndependentEvaluation(null, COMMIT_TAG_1_3);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_3To_1_3_1Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.3 and 1.3.1.
		executePropagationAndEvaluation(COMMIT_TAG_1_3, COMMIT_TAG_1_3_1, 1);
//		performIndependentEvaluation(COMMIT_TAG_1_3, COMMIT_TAG_1_3_1);
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
//		performIndependentEvaluation(null, COMMIT_TAG_1_3_1);
	}

	private void propagateMultipleCommits(String firstCommit, String lastCommit) throws InterruptedException, GitAPIException, IOException {
		List<String> successfulCommits = new ArrayList<>();
		var commits = convertToStringList(this.controller.getCommitChangePropagator().getWrapper()
				.getAllCommitsBetweenTwoCommits(firstCommit, lastCommit));
		commits.add(0, firstCommit);
		int startIndex = 0;
		var oldCommit = commits.get(startIndex);
		successfulCommits.add(oldCommit);
		for (int idx = startIndex + 1; idx < commits.size(); idx++) {
			var newCommit = commits.get(idx);
			boolean result = executePropagationAndEvaluation(oldCommit, newCommit, idx);
			if (result) {
				oldCommit = newCommit;
				successfulCommits.add(oldCommit);
				break;
			}
			Thread.sleep(1000);
		}
		for (String c : successfulCommits) {
			logger.debug("Successful propagated: " + c);
		}
	}
	
	private List<String> convertToStringList(List<RevCommit> commits) {
		List<String> result = new ArrayList<>();
		for (RevCommit com : commits) {
			result.add(com.getId().getName());
		}
		return result;
	}

	/**
	 * Propagates changes between two commits and performs a partial evaluation on the result.
	 * 
	 * @param oldCommit the first commit. Can be null.
	 * @param newCommit the second commit.
	 * @param num the number of the propagation.
	 * @return true if the changes were propagated. false otherwise.
	 * @throws GitAPIException if an exception occurs during the Git processing.
	 * @throws IOException if an IO operation cannot be performed.
	 */
	private boolean executePropagationAndEvaluation(String oldCommit, String newCommit, int num)
			throws GitAPIException, IOException {
		EvaluationDataContainer evalResult = new EvaluationDataContainer();
		EvaluationDataContainer.setGlobalContainer(evalResult);
		boolean result = this.controller.propagateChanges(oldCommit, newCommit, true);
		if (result) {
			Resource javaModel = this.controller.getJavaModelResource();
			Resource instrumentedModel = this.controller.getLastInstrumentedModelResource();
			Path root = this.controller.getVSUMFacade().getFileLayout().getRootPath();
			Path copy = root.resolveSibling(root.getFileName().toString() + "-" + num + "-" + newCommit);
			logger.debug("Copying the propagated state.");
			FileUtils.copyDirectory(root.toFile(), copy.toFile());
			logger.debug("Evaluating the instrumentation.");
			new InstrumentationEvaluator().evaluateInstrumentationDependently(
					this.controller.getVSUMFacade().getInstrumentationModel(),
					javaModel, instrumentedModel,
					this.controller.getVSUMFacade().getVSUM().getCorrespondenceModel());
			EvaluationDataContainerReaderWriter.write(evalResult, copy.resolve("DependentEvaluationResult.json"));
			logger.debug("Finished the evaluation.");
		}
		return result;
	}
	
	/**
	 * Performs an evaluation independent of the change propagation. It requires that changes between
	 * two commits has been propagated. It is recommended that this method is not executed with the
	 * executePropagationAndEvaluation method at the same time because this can cause a OutOfMemoryError.
	 * 
	 * @param oldCommit the first commit. Can be null.
	 * @param newCommit the second commit.
	 * @throws IOException if an IO operation cannot be performed.
	 */
	private void performIndependentEvaluation(String oldCommit, String newCommit) throws IOException {
		logger.debug("Evaluating the propagation " + oldCommit + "->" + newCommit);
		EvaluationDataContainer evalResult = EvaluationDataContainer.getGlobalContainer();
		evalResult.getChangeStatistic().setOldCommit(oldCommit);
		evalResult.getChangeStatistic().setNewCommit(newCommit);
		Resource javaModel = this.controller.getJavaModelResource();
		logger.debug("Evaluating the Java model.");
		new JavaModelEvaluator().evaluateJavaModels(javaModel,
				this.controller.getCommitChangePropagator().getJavaFileSystemLayout().getLocalJavaRepo(),
				evalResult.getJavaComparisonResult(),
				this.controller.getCommitChangePropagator().getJavaFileSystemLayout().getModuleConfiguration());
		logger.debug("Evaluating the instrumentation model.");
		new IMUpdateEvaluator().evaluateIMUpdate(
				this.controller.getVSUMFacade().getPCMWrapper().getRepository(),
				this.controller.getVSUMFacade().getInstrumentationModel(),
				evalResult.getImEvalResult());
		logger.debug("Evaluating the instrumentation.");
		new InstrumentationEvaluator().evaluateInstrumentationIndependently(
				this.controller.getVSUMFacade().getInstrumentationModel(),
				javaModel, this.controller.getCommitChangePropagator().getJavaFileSystemLayout(),
				this.controller.getVSUMFacade().getVSUM().getCorrespondenceModel());
		Path root = this.controller.getVSUMFacade().getFileLayout().getRootPath();
		EvaluationDataContainerReaderWriter.write(evalResult, root.resolveSibling("EvaluationResult-"
				+ newCommit + "-" + evalResult.getEvaluationTime() + ".json"));
		logger.debug("Finished the evaluation.");
	}
}
