package cipm.consistency.vsum.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.designtime.instrumentation2.CodeInstrumenter;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;

public class TeaStoreCITest extends AbstractCITest {
	private static final Logger logger = Logger.getLogger("cipm." + TeaStoreCITest.class.getSimpleName());
	private static final String COMMIT_TAG_1_1 = "77733d9c6ab6680c6cc460c631cd408a588a595c";
	private static final String COMMIT_TAG_1_2 = "53c6efa1dca64a87e536d8c5a3dcc3c12ad933b5";
	private static final String COMMIT_TAG_1_2_1 = "f8f13f4390f80d3dc8adb0a6167938a688ddb45e";
	
	@Override
	protected String getTestPath() {
		return "target" + File.separator + "TeaStoreTest";
	}
	
	@Override
	protected String getRepositoryPath() {
		return "../.git/modules/cipm.consistency.base.vitruv.vsum.test/TeaStore";
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_1Integration() throws Exception {
		// Integrates TeaStore version 1.1.
		prop.propagateChanges(COMMIT_TAG_1_1);
	}
	
	@Disabled("Only one test case should run at once.")
	@Test
	public void testTeaStore1_1To1_2Propagation() throws Exception {
		// Propagation of changes between TeaStore version 1.1 and 1.2.
		prop.propagateChanges(COMMIT_TAG_1_1, COMMIT_TAG_1_2);
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
		List<String> successfulCommits = new ArrayList<>();
		var commits = convertToStringList(this.prop.getWrapper()
				.getAllCommitsBetweenTwoCommits(COMMIT_TAG_1_2, COMMIT_TAG_1_2_1));
		var oldCommit = commits.get(0);
		successfulCommits.add(oldCommit);
		for (int idx = 1; idx < commits.size(); idx++) {
			var newCommit = commits.get(idx);
			boolean result = executePropagationAndEvaluation(oldCommit, newCommit, idx);
			if (result) {
				oldCommit = newCommit;
				successfulCommits.add(oldCommit);
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
	
	private boolean executePropagationAndEvaluation(String oldCommit, String newCommit, int num)
			throws GitAPIException, IOException {
		EvaluationDataContainer evalResult = new EvaluationDataContainer();
		EvaluationDataContainer.setGlobalContainer(evalResult);
		this.facade.getInstrumentationModel().eAllContents().forEachRemaining(ip -> {
			if (ip instanceof ActionInstrumentationPoint) {
				((ActionInstrumentationPoint) ip).setActive(false);
			}
		});
		this.facade.getInstrumentationModel().eResource().save(null);
		boolean result = prop.propagateChanges(oldCommit, newCommit);
		if (result) {
			FileUtils.deleteDirectory(this.prop.getJavaFileSystemLayout().getInstrumentationCopy().toFile());
			Resource javaModel = getJavaModel();
			Resource instrumentedModel = new CodeInstrumenter().instrument(
					this.facade.getInstrumentationModel(),
					this.facade.getVSUM().getCorrespondenceModel(),
					javaModel,
					this.prop.getJavaFileSystemLayout().getInstrumentationCopy(),
					this.prop.getJavaFileSystemLayout().getLocalJavaRepo(), true);
//			new CodeInstrumenter().instrument(this.facade.getInstrumentationModel(),
//					this.facade.getVSUM().getCorrespondenceModel(),
//					javaModel,
//					this.prop.getJavaFileSystemLayout().getInstrumentationCopy().resolveSibling("ins-all"),
//					this.prop.getJavaFileSystemLayout().getLocalJavaRepo(), false);
			Path root = this.facade.getFileLayout().getRootPath();
			Path copy = root.resolveSibling(root.getFileName().toString() + "-" + num + "-" + newCommit);
			logger.debug("Copying the propagated state.");
			FileUtils.copyDirectory(root.toFile(), copy.toFile());
			logger.debug("Evaluating the instrumentation.");
			new InstrumentationEvaluator().evaluateInstrumentationDependently(this.facade.getInstrumentationModel(),
					javaModel, instrumentedModel, this.facade.getVSUM().getCorrespondenceModel());
			EvaluationDataContainerReaderWriter.write(evalResult, copy.resolve("DependentEvaluationResult.json"));
			logger.debug("Finished the evaluation.");
		}
		return result;
	}
	
	private Resource getJavaModel() {
		return this.facade.getVSUM().getModelInstance(
				URI.createFileURI(prop.getJavaFileSystemLayout().getJavaModelFile().toString()))
				.getResource();
	}
	
	private void performIndependentEvaluation(String oldCommit, String newCommit) throws IOException {
		logger.debug("Evaluating the propagation " + oldCommit + "->" + newCommit);
		EvaluationDataContainer evalResult = EvaluationDataContainer.getGlobalContainer();
		evalResult.getChangeStatistic().setOldCommit(oldCommit);
		evalResult.getChangeStatistic().setNewCommit(newCommit);
		Resource javaModel = getJavaModel();
		logger.debug("Evaluating the Java model.");
		new JavaModelEvaluator().evaluateJavaModels(javaModel,
				prop.getJavaFileSystemLayout().getLocalJavaRepo(), evalResult.getJavaComparisonResult(),
				prop.getJavaFileSystemLayout().getModuleConfiguration());
		logger.debug("Evaluating the instrumentation model.");
		new IMUpdateEvaluator().evaluateIMUpdate(this.facade.getPCMWrapper().getRepository(),
				this.facade.getInstrumentationModel(), evalResult.getImEvalResult());
		logger.debug("Evaluating the instrumentation.");
		new InstrumentationEvaluator().evaluateInstrumentationIndependently(this.facade.getInstrumentationModel(),
				javaModel, this.prop.getJavaFileSystemLayout(),
				this.facade.getVSUM().getCorrespondenceModel());
		Path root = this.facade.getFileLayout().getRootPath();
		EvaluationDataContainerReaderWriter.write(evalResult, root.resolveSibling("EvaluationResult-"
				+ newCommit + "-" + evalResult.getEvaluationTime() + ".json"));
		logger.debug("Finished the evaluation.");
	}
	
//	@Disabled("Only one test case should run at once.")
//	@Test
//	public void testTeaStoreIntegration() throws Exception {
//		prop.propagateChanges("b0ecb45238772f06db1e11e8e7baaa72f48cdd96");
//	}
	
//	@Disabled("Only one test case should run at once.")
//	@Test
//	public void testTeaStoreFirstPropagation() throws Exception {
//		prop.propagateChanges("b0ecb45238772f06db1e11e8e7baaa72f48cdd96",
//					"653528e387a4fb764e48c9422ab218c65f87082f",
//					"0ce0cababe2590eff6d03c15f9eb3c977fdf4914");
//	}
	
//	@Disabled("Only one test case should run at once.")
//	@Test
//	public void testTeaStoreSecondPropagation() throws Exception {
//		prop.propagateChanges("0ce0cababe2590eff6d03c15f9eb3c977fdf4914",
//				"205d163bc2c878c90ac5038276ad04cdf502a695");
//	}
}
