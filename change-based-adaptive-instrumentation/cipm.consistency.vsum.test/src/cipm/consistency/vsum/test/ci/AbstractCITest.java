package cipm.consistency.vsum.test.ci;

import cipm.consistency.commitintegration.CommitIntegration;
import cipm.consistency.commitintegration.CommitIntegrationController;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainerReaderWriter;
import cipm.consistency.vsum.test.evaluator.IMUpdateEvaluator;
import cipm.consistency.vsum.test.evaluator.InstrumentationEvaluator;
import cipm.consistency.vsum.test.evaluator.JavaModelEvaluator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * An abstract superclass for test cases providing the setup.
 * 
 * 
 * @author Martin Armbruster
 * @author Lukas Burgey
 */
public abstract class AbstractCITest extends CommitIntegrationController implements CommitIntegration {
    private static final Logger LOGGER = Logger.getLogger("cipm." + AbstractCITest.class.getSimpleName());
//    protected CommitIntegrationController controller;

    @BeforeEach
    public void setUpLogging() {
        Logger logger = Logger.getLogger("cipm");
        logger.setLevel(Level.ALL);
        logger = Logger.getLogger("jamopp");
        logger.setLevel(Level.ALL);
        logger = Logger.getRootLogger();
        logger.removeAllAppenders();
//        ConsoleAppender ap = new ConsoleAppender(new PatternLayout("[%d{DATE}] %-5p: %c - %m%n"),
        ConsoleAppender ap = new ConsoleAppender(new PatternLayout("%-5p: %c - %m%n"), ConsoleAppender.SYSTEM_OUT);
        logger.addAppender(ap);
    }

    @BeforeEach
    public void initialize() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        super.initialize(this);
    }

    @AfterEach
    public void dispose() {
        super.dispose();
    }

    protected void propagateMultipleCommits(String firstCommit, String lastCommit)
            throws IOException, InterruptedException {
        List<String> successfulCommits = new ArrayList<>();
        var commits = convertToStringList(
                getGitRepositoryWrapper().getAllCommitsBetweenTwoCommits(firstCommit, lastCommit));
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
            LOGGER.debug("Successful propagated: " + c);
        }
    }

    private List<String> convertToStringList(List<RevCommit> commits) {
        List<String> result = new ArrayList<>();
        for (RevCommit com : commits) {
            result.add(com.getId()
                .getName());
        }
        return result;
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
    @SuppressWarnings("restriction")
    protected boolean executePropagationAndEvaluation(String oldCommit, String newCommit, int num) throws IOException {
        EvaluationDataContainer evalResult = new EvaluationDataContainer();
        EvaluationDataContainer.setGlobalContainer(evalResult);
        String repoFile = getVsumFacade().getPCMWrapper()
            .getRepository()
            .eResource()
            .getURI()
            .toFileString();
        FileUtils.copyFile(new File(repoFile), new File(this.getRootPath()
            .toString(), "Repository.repository"));
        FileUtils.copyFile(new File(repoFile), new File(this.getRootPath()
            .toString(), "Repository_" + num + "_mu.repository"));

        try {
            var propagatedChanges = propagateChanges(oldCommit, newCommit, true);
            if (propagatedChanges != null) {
                Resource codeModel = getModelResource();
                Resource instrumentedModel = getLastInstrumentedModelResource();
                var copy = createFileSystemCopy(num + "-" + newCommit);
                LOGGER.debug("Evaluating the instrumentation.");
                new InstrumentationEvaluator().evaluateInstrumentationDependently(
                        getVsumFacade().getInstrumentationModel(), codeModel, instrumentedModel,
                        getVsumFacade().getVsum()
                            .getCorrespondenceModel());
                EvaluationDataContainerReaderWriter.write(evalResult, copy.resolve("DependentEvaluationResult.json"));
                LOGGER.debug("Finished the evaluation.");
                return true;
            }
        } catch (IOException | GitAPIException  e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        return false;
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
    @SuppressWarnings("restriction")
    protected void performIndependentEvaluation() throws IOException {
        String[] commits = loadCommits();
        String oldCommit = commits[0];
        String newCommit = commits[1];
        LOGGER.debug("Evaluating the propagation " + oldCommit + "->" + newCommit);
        EvaluationDataContainer evalResult = EvaluationDataContainer.getGlobalContainer();
        evalResult.getChangeStatistic()
            .setOldCommit(oldCommit);
        evalResult.getChangeStatistic()
            .setNewCommit(newCommit);
        Resource javaModel = getModelResource();
        LOGGER.debug("Evaluating the Java model.");
        new JavaModelEvaluator().evaluateJavaModels(javaModel, getLanguageSpec().getFileLayout(getSettingsPath())
            .getLocalRepo(), evalResult.getJavaComparisonResult(),
                getCommitChangePropagator().getFileSystemLayout()
                    .getModuleConfiguration());
        LOGGER.debug("Evaluating the instrumentation model.");
        new IMUpdateEvaluator().evaluateIMUpdate(getVsumFacade().getPCMWrapper()
            .getRepository(), getVsumFacade().getInstrumentationModel(), evalResult.getImEvalResult(),
                getRootPath().toString());
        LOGGER.debug("Evaluating the instrumentation.");
        new InstrumentationEvaluator().evaluateInstrumentationIndependently(getVsumFacade().getInstrumentationModel(),
                javaModel, getCommitChangePropagator().getFileSystemLayout(), getVsumFacade().getVsum()
                    .getCorrespondenceModel());
        EvaluationDataContainerReaderWriter.write(evalResult, getRootPath()
            .resolveSibling("EvaluationResult-" + newCommit + "-" + evalResult.getEvaluationTime() + ".json"));
        LOGGER.debug("Finished the evaluation.");
    }

    /**
     * Returns the path to the local directory in which the data is stored.
     * 
     * @return the path.
     */
    public Path getRootPath() {
        return Path.of("testData", "tests", this.getClass()
            .getSimpleName());
    };

    /**
     * Returns the path to the settings file.
     * 
     * @return the path.
     */
    public Path getSettingsPath() {
        return getRootPath().resolve("settings.settings");
    };

    /**
     * Returns the path to vsum.
     * 
     * @return the path.
     */
    public Path getVsumPath() {
        return getRootPath();
    };
}
