package cipm.consistency.vsum.test.appspace;

//import cipm.consistency.vsum.test.evaluator.JavaModelEvaluator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.vsum.Propagation;
import cipm.consistency.vsum.test.evaluator.PropagationEvaluator;

public abstract class AppSpaceCITestController extends AppSpaceCommitIntegrationController {

    private static final Logger LOGGER = Logger.getLogger(AppSpaceCITestController.class);

    /**
     * The path to the git directory of the whole cipm repository. This path is relative to the
     * current project.
     */
    protected static final Path CIPM_GIT_DIR = Paths.get("../../../../.git");

    /**
     * The path to the directory where all test data is put. Examples are dumps the models during
     * different stages of the propagation
     */
    protected static final Path TESTDATA_PATH = Path.of("testData", "tests");

    protected boolean forceEmptyPropagation = true;

    private Path rootPath;

    /**
     * Returns the path to the local directory in which the test data is stored. This directory is
     * created if it does not exist
     * 
     * The directory is <test data root>/<class name>/<method name> so it can be easily located
     * 
     * @return the path.
     */
    public Path getRootPath() {
        return this.rootPath;
    };

    private void setRootPath(TestInfo testInfo) {
        var className = this.getClass()
            .getSimpleName();
        var methodName = testInfo.getDisplayName()
            .replace("()", "");

        this.rootPath = TESTDATA_PATH.resolve(className)
            .resolve(methodName);
    }

    /**
     * 
     * @param testInfo
     * @param overwrite
     *            Are existing files (models, etc.) to be deleted before initializing the commit
     *            integration state?
     * @throws GitAPIException
     * @throws IOException
     * @throws org.eclipse.jgit.api.errors.TransportException
     * @throws InvalidRemoteException
     */
    protected void setup(TestInfo testInfo, boolean overwrite) throws InvalidRemoteException,
            org.eclipse.jgit.api.errors.TransportException, IOException, GitAPIException {
        // the root path of the integration data contains the current class and method name
        // hence it is dynamically set using the test info
        setRootPath(testInfo);

        // Create new empty state
        this.state = new CommitIntegrationState<>();
//
//        // overwrite existing files?
        state.initialize(this, overwrite);
    }

    @BeforeEach
    public void setup(TestInfo testInfo) {
        try {
            LoggingSetup.setMinLogLevel(Level.WARN);
            setup(testInfo, true);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        } finally {
            LoggingSetup.resetLogLevels();
        }
    }

    @AfterEach
    public void cleanupAfterTest() {
        state.dispose();
    }

    /*
     * Deletes all testdata before running a new batch of tests
     */
    @BeforeAll
    public static void deleteDataBeforeRunningTests() {

        try {
            Files.walk(TESTDATA_PATH)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> path.toFile()
                    .delete());
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
    }

    @Override
    protected boolean prePropagationChecks(String firstCommitId, String secondCommitId) {
        if (forceEmptyPropagation) {
            return true;
        }
        return super.prePropagationChecks(firstCommitId, secondCommitId);
    }

    protected boolean evaluatePropagation(Propagation propagation) {
        if (propagation == null) {
            Assert.fail("PropagatedChanges may not be null");
        }
        return PropagationEvaluator.evaluate(propagation);
    }

    private boolean getImmediateEvaluation() {
        return false;
    }

    /**
     * Propagates the given commits and evaluates every propagation.
     * 
     * @param commitIds
     *            The commits to be propagated
     * @return The list of all the propagations.
     */
    protected List<Propagation> propagateAndEvaluate(String... commitIds) {

        List<Propagation> allPropagations = new ArrayList<>();
        try {
            for (var commitId : commitIds) {
                // only one commit, only one propagation
                var propagations = propagateChanges(commitId);

                if (commitId == null || propagations.size() != 1 || propagations.get(0)
                    .isEmpty()) {
                    continue;
                }

                var propagation = propagations.get(0)
                    .get();
                if (getImmediateEvaluation() && !evaluatePropagation(propagation)) {
                    failTest("Propagation failed evaluation (immediate abort)");
                }
                allPropagations.add(propagation);
            }

            if (!getImmediateEvaluation()) {
                LOGGER.info("\n\tEvaluating all propagations");
                var i = 1;
                var failures = 0;
                for (var propagation : allPropagations) {
                    if (!evaluatePropagation(propagation)) {
                        failures++;
                        LOGGER.error(String.format("Propagation #%d failed evaluation", i));
                    }
                    i++;
                }
                if (failures > 0) {
                    failTest(String.format("%d propagations where invalid", failures));
                }
            }

            return allPropagations;
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        return null;
    }

    private void failTest(String msg) {
        LOGGER.error(msg);
        Assert.fail(msg);
    }

    /**
     * Propagates changes between two commits and performs a partial evaluation on the result.
     * 
     * @param oldCommit
     *            the first commit. Can be null.
     * @param newCommit
     *            the second commit.
     * @param num
     *            the number of the propagation.
     * @return true if the changes were propagated. false otherwise.
     * @throws GitAPIException
     *             if an exception occurs during the Git processing.
     * @throws IOException
     *             if an IO operation cannot be performed.
     */
//    @SuppressWarnings("restriction")
//    protected boolean executePropagationAndEvaluation(String oldCommit, String newCommit, int num) throws IOException {
//        EvaluationDataContainer evalResult = new EvaluationDataContainer();
//        EvaluationDataContainer.setGlobalContainer(evalResult);
//
//        // TODO this is currently not needed i think
////        String repoFile = getVsumFacade().getPCMWrapper()
////            .getRepository()
////            .eResource()
////            .getURI()
////            .toFileString();
////        FileUtils.copyFile(new File(repoFile), new File(this.getRootPath()
////            .toString(), "Repository.repository"));
////        FileUtils.copyFile(new File(repoFile), new File(this.getRootPath()
////            .toString(), "Repository_" + num + "_mu.repository"));
//
//        assertSuccessfulPropagation(oldCommit, newCommit);
//
////        Resource codeModel = getModelResource();
////        Resource codeModel = state.getCodeModel().getResources();
//
//        var copy = state.createCopyWithTimeStamp(num + "-" + newCommit);
//        LOGGER.debug("Evaluating the instrumentation.");
//        new InstrumentationEvaluator.evaluateInstrumentationDependently(state.getImFacade()
//            .getModel(),
//                state.getCodeModelFacade()
//                    .getResource(),
//                instrumentedModel, state.getVsumFacade()
//                    .getVsum()
//                    .getCorrespondenceModel());
//        EvaluationDataContainerReaderWriter.write(evalResult, copy.resolve("DependentEvaluationResult.json"));
//        LOGGER.debug("Finished the evaluation.");
//        return true;
//    }

    // I don't think i need the next method
//    protected void propagateMultipleCommits(String firstCommit, String lastCommit)
//            throws IOException, InterruptedException, InvalidRemoteException, TransportException, GitAPIException {
//        List<String> successfulCommits = new ArrayList<>();
//        var commits = convertToStringList(
//                getGitRepositoryWrapper().getAllCommitsBetweenTwoCommits(firstCommit, lastCommit));
//        commits.add(0, firstCommit);
//        int startIndex = 0;
//        var oldCommit = commits.get(startIndex);
//        successfulCommits.add(oldCommit);
//        for (int idx = startIndex + 1; idx < commits.size(); idx++) {
//            var newCommit = commits.get(idx);
//            boolean result = executePropagationAndEvaluation(oldCommit, newCommit, idx);
//            if (result) {
//                oldCommit = newCommit;
//                successfulCommits.add(oldCommit);
//                break;
//            }
//            Thread.sleep(1000);
//        }
//        for (String c : successfulCommits) {
//            LOGGER.debug("Successful propagated: " + c);
//        }
//    }
    /**
     * Loads the propagated commits.
     * 
     * @return an empty array if the commits cannot be loaded. Otherwise, the first index contains
     *         the start commit (possibly null for the initial commit), and the second index
     *         contains the target commit.
     */
    public String[] loadCommits() {
        try {
            var lines = Files.readAllLines(state.getDirLayout()
                .getCommitsFilePath());
            String[] result = new String[2];
            if (lines.size() == 1) {
                result[1] = lines.get(0);
            } else {
                result[0] = lines.get(0);
                result[1] = lines.get(1);
            }
            return result;
        } catch (IOException e) {
            return new String[0];
        }
    }

    protected void propagateAndEvaluateIndividually(TestInfo testInfo, String... commits) {
        for (var commit : commits) {
            setup(testInfo);
            propagateAndEvaluate(null, commit);
            cleanupAfterTest();
        }
    }

    /**
     * Performs an evaluation independent of the change propagation. It requires that changes
     * between two commits has been propagated. It is recommended that this method is not executed
     * with the executePropagationAndEvaluation method at the same time because this can cause a
     * OutOfMemoryError.
     * 
     * @throws IOException
     *             if an IO operation cannot be performed.
     */
//    @SuppressWarnings("restriction")
//    protected void performIndependentEvaluation() throws IOException {
//        String[] commits = loadCommits();
//        String oldCommit = commits[0];
//        String newCommit = commits[1];
//        LOGGER.debug("Evaluating the propagation " + oldCommit + "->" + newCommit);
//        EvaluationDataContainer evalResult = EvaluationDataContainer.getGlobalContainer();
//        evalResult.getChangeStatistic()
//            .setOldCommit(oldCommit);
//        evalResult.getChangeStatistic()
//            .setNewCommit(newCommit);
//        Resource javaModel = state.getCodeModelFacade()
//            .getResource();
//        LOGGER.debug("Evaluating the Java model.");
//        new JavaModelEvaluator().evaluateJavaModels(javaModel, state.getDirLayout()
//            .getSettingsFilePath(),
//                state.getCodeModelFacade()
//                    .getDirLayout()
//                    .getLocalRepoDir(),
//                evalResult.getJavaComparisonResult(), state.getCodeModelFacade()
//                    .getDirLayout()
//                    .getModuleConfigurationPath());
//
//        LOGGER.debug("Evaluating the instrumentation model.");
//        new IMUpdateEvaluator().evaluateIMUpdate(state.getPcmFacade()
//            .getInMemoryPCM()
//            .getRepository(),
//                state.getImFacade()
//                    .getModel(),
//                evalResult.getImEvalResult(), getRootPath().toString());
//        LOGGER.debug("Evaluating the instrumentation.");
////        new InstrumentationEvaluator().evaluateInstrumentationIndependently(state.getIm()
////            .getModel(), javaModel, getCommitChangePropagator().getFileSystemLayout(),
////                state.getVsum()
////                    .getVsum()
////                    .getCorrespondenceModel());
//        new InstrumentationEvaluator().evaluateInstrumentationIndependently(state);
//        EvaluationDataContainerReaderWriter.write(evalResult, getRootPath()
//            .resolveSibling("EvaluationResult-" + newCommit + "-" + evalResult.getEvaluationTime() + ".json"));
//        LOGGER.debug("Finished the evaluation.");
//    }

    /**
     * Returns the path to the settings file.
     * 
     * @return the path.
     */
    public Path getSettingsPath() {
        return getRootPath().resolve("settings.settings");
    };

    @BeforeEach
    public void setUpLogging() {
        LoggingSetup.setupLogging(Level.INFO);
    }
}
