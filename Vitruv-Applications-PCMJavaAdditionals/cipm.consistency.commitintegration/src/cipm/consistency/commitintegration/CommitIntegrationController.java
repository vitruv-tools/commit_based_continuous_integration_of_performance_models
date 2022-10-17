package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.commitintegration.util.ExternalCommandExecutionUtils;
import cipm.consistency.designtime.instrumentation2.CodeInstrumenter;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * This central class is responsible for controlling the complete change propagation and adaptive
 * instrumentation.
 * 
 * @author Martin Armbruster
 */
public abstract class CommitIntegrationController extends CommitIntegrationState {
    private static final Logger LOGGER = Logger.getLogger("cipm." + CommitIntegrationController.class.getSimpleName());
    private Resource instrumentedModel;

    /**
     * Propagates the changes between two commits.
     * 
     * @param oldCommit
     *            the first commit or null.
     * @param newCommit
     *            the second commit. Changes between the oldCommit and newCommit are propagated.
     * @return true if the propagation was successful. false otherwise.
     * @throws IOException
     *             if an IO operation fails.
     * @throws GitAPIException
     *             if a Git operation fails.
     */
    public boolean propagateChanges(String oldCommit, String newCommit) throws IOException, GitAPIException {
        return propagateChanges(oldCommit, newCommit, true);
    }

    /**
     * Propagates the changes between two commits.
     * 
     * @param oldCommit
     *            the first commit or null.
     * @param newCommit
     *            the second commit. Changes between the oldCommit and newCommit are propagated.
     * @param storeInstrumentedModel
     *            true if the instrumented code model shall be stored in this instance.
     * @return true if the propagation was successful. false otherwise.
     * @throws IOException
     *             if an IO operation fails.
     * @throws GitAPIException
     *             if a Git operation fails.
     */
    public boolean propagateChanges(String oldCommit, String newCommit, boolean storeInstrumentedModel)
            throws IOException {
        {
            var parent = getVsumFacade().getFileSystemLayout()
                .getCommitsPath()
                .toAbsolutePath()
                .getParent();
            if (Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(getVsumFacade().getFileSystemLayout()
            .getCommitsPath())) {
            if (oldCommit != null) {
                writer.write(oldCommit + "\n");
            }
            writer.write(newCommit + "\n");
        }

        long overallTimer = System.currentTimeMillis();
        instrumentedModel = null;
        Path insDir = getFileSystemLayout().getInstrumentationDir();
        removeInstrumentationDirectory(insDir);

        // Deactivate all action instrumentation points.
        getVsumFacade().getInstrumentationModel()
            .getPoints()
            .forEach(sip -> sip.getActionInstrumentationPoints()
                .forEach(aip -> aip.setActive(false)));
        getVsumFacade().getInstrumentationModel()
            .eResource()
            .save(null);

        long fineTimer = System.currentTimeMillis();

        // Propagate the changes.
        boolean result = false;
        try {
            result = getCommitChangePropagator().propagateChanges(oldCommit, newCommit);

            fineTimer = System.currentTimeMillis() - fineTimer;
            EvaluationDataContainer.getGlobalContainer()
                .getExecutionTimes()
                .setChangePropagationTime(fineTimer);

            if (result) {
                // TODO out commented this
//            @SuppressWarnings("restriction")
//            ExternalCallEmptyTargetFiller filler = new ExternalCallEmptyTargetFiller(facade.getVSUM()
//                .getCorrespondenceModel(),
//                    facade.getPCMWrapper()
//                        .getRepository(),
//                    propagator.getJavaFileSystemLayout()
//                        .getExternalCallTargetPairsFile());
//            filler.fillExternalCalls();

                boolean hasChangedIM = false;
                for (var sip : getVsumFacade().getInstrumentationModel()
                    .getPoints()) {
                    for (var aip : sip.getActionInstrumentationPoints()) {
                        hasChangedIM |= aip.isActive();
                    }
                }
                if (!hasChangedIM) {
                    LOGGER.debug("No instrumentation points changed.");
                }
                boolean fullInstrumentation = CommitIntegrationSettingsContainer.getSettingsContainer()
                    .getPropertyAsBoolean(SettingKeys.PERFORM_FULL_INSTRUMENTATION);

                // Instrument the code only if there is a new action instrumentation point or if a
                // full
                // instrumentation
                // shall be performed.
                if (hasChangedIM || fullInstrumentation) {
                    fineTimer = System.currentTimeMillis();
                    Resource insModel = performInstrumentation(insDir, fullInstrumentation);
                    fineTimer = System.currentTimeMillis() - fineTimer;
                    EvaluationDataContainer.getGlobalContainer()
                        .getExecutionTimes()
                        .setInstrumentationTime(fineTimer);
                    if (storeInstrumentedModel) {
                        this.instrumentedModel = insModel;
                    }
                }
            }
            overallTimer = System.currentTimeMillis() - overallTimer;
            EvaluationDataContainer.getGlobalContainer()
                .getExecutionTimes()
                .setOverallTime(overallTimer);

        } catch (GitAPIException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Removes potentially available instrumented code and performs a new instrumentation.
     * 
     * @param performFullInstrumentation
     *            true if a full instrumentation shall be performed. false otherwise.
     * @return the instrumented code model as a copy of the code model in the V-SUM.
     */
    public Resource instrumentCode(boolean performFullInstrumentation) {
        Path insDir = getFileSystemLayout().getInstrumentationDir();
        removeInstrumentationDirectory(insDir);
        return performInstrumentation(insDir, performFullInstrumentation);
    }

    private void removeInstrumentationDirectory(Path instrumentationDirectory) {
        if (Files.exists(instrumentationDirectory)) {
            LOGGER.debug("Deleting the instrumentation directory.");
            try {
                FileUtils.deleteDirectory(instrumentationDirectory.toFile());
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    @SuppressWarnings("restriction")
    private Resource performInstrumentation(Path instrumentationDirectory, boolean performFullInstrumentation) {
        Resource javaModel = getModelResource();
        return CodeInstrumenter.instrument(getVsumFacade().getInstrumentationModel(), getVsumFacade().getVsum()
            .getCorrespondenceModel(), javaModel, instrumentationDirectory, getFileSystemLayout().getLocalRepo(),
                !performFullInstrumentation);
    }

    /**
     * Compiles and deploy the instrumented code.
     * 
     * @throws IOException
     *             if an IO operation fails.
     */
    public void compileAndDeployInstrumentedCode() throws IOException {
        Path instrumentationCodeDir = getFileSystemLayout().getInstrumentationDir();
        if (Files.exists(instrumentationCodeDir)) {
            boolean compilationResult = compileInstrumentedCode(instrumentationCodeDir);
            if (compilationResult) {
                Path deployPath = Paths.get(CommitIntegrationSettingsContainer.getSettingsContainer()
                    .getProperty(SettingKeys.DEPLOYMENT_PATH));
                var result = copyArtifacts(instrumentationCodeDir, deployPath);
                LOGGER.debug("Removing the monitoring classes.");
                result.forEach(p -> {
                    try {
                        removeMonitoringClasses(p);
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                });
            } else {
                LOGGER.debug("Could not compile the instrumented code.");
            }
        }
        LOGGER.debug("Finished the compilation and deployment.");
    }

    private boolean compileInstrumentedCode(Path insCode) {
        LOGGER.debug("Compiling the instrumented code.");
        String compileScript = CommitIntegrationSettingsContainer.getSettingsContainer()
            .getProperty(SettingKeys.PATH_TO_COMPILATION_SCRIPT);
        compileScript = new File(compileScript).getAbsolutePath();
        return ExternalCommandExecutionUtils.runScript(insCode.toFile(), compileScript);
    }

    private List<Path> copyArtifacts(Path insCode, Path deployPath) throws IOException {
        LOGGER.debug("Copying the artifacts to " + deployPath);
        var warFiles = Files.walk(insCode)
            .filter(Files::isRegularFile)
            .filter(p -> p.getFileName()
                .toString()
                .endsWith(".war"))
            .collect(Collectors.toCollection(ArrayList::new));
        List<String> fileNames = new ArrayList<>();
        for (int idx = 0; idx < warFiles.size(); idx++) {
            String name = warFiles.get(idx)
                .getFileName()
                .toString();
            if (fileNames.contains(name)) {
                warFiles.remove(idx);
                idx--;
            } else {
                fileNames.add(name);
            }
        }
        List<Path> result = new ArrayList<>();
        warFiles.forEach(p -> {
            Path target = deployPath.resolve(p.getFileName());
            try {
                Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
                result.add(target);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        });
        return result;
    }

    private void removeMonitoringClasses(Path file) throws IOException {
        Map<String, String> options = new HashMap<>();
        options.put("create", "false");
//        try (FileSystem fileSys = FileSystems.newFileSystem(file, options)) {
//            String tmcEndPath = "cipm/consistency/bridge/monitoring/controller/ThreadMonitoringController.class";
//            String spEndPath = "cipm/consistency/bridge/monitoring/controller/ServiceParameters.class";
//            fileSys.getRootDirectories()
//                .forEach(root -> {
//                    try {
//                        Files.walk(root)
//                            .filter(p -> {
//                                String fullPath = p.toString();
//                                if (fullPath.endsWith(".jar") || fullPath.endsWith(".war")
//                                        || fullPath.endsWith(".zip")) {
//                                    try {
//                                        removeMonitoringClasses(p);
//                                    } catch (IOException e) {
//                                        LOGGER.error(e);
//                                    }
//                                } else if (fullPath.endsWith(tmcEndPath) || fullPath.endsWith(spEndPath)) {
//                                    return true;
//                                }
//                                return false;
//                            })
//                            .forEach(p -> {
//                                try {
//                                    Files.delete(p);
//                                } catch (IOException e) {
//                                    LOGGER.error(e);
//                                }
//                            });
//                    } catch (IOException e) {
//                        LOGGER.error(e);
//                    }
//                });
//        }
    }

    /**
     * Loads the propagated commits.
     * 
     * @return an empty array if the commits cannot be loaded. Otherwise, the first index contains
     *         the start commit (possibly null for the initial commit), and the second index
     *         contains the target commit.
     */
    public String[] loadCommits() {
        try {
            var lines = Files.readAllLines(getVsumFacade().getFileSystemLayout()
                .getCommitsPath());
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

    @SuppressWarnings("restriction")
    public Resource getModelResource() {
        var uri = URI.createFileURI(getFileSystemLayout().getModelFile().toAbsolutePath()
            .toString());
        var model =getVsumFacade().getVsum()
            .getModelInstance(uri);
        if (model != null)
            return model.getResource();

        LOGGER.error(String.format("Model URI does not exist in vsum: %s", uri));
        return null;
    }

    public Resource getLastInstrumentedModelResource() {
        return instrumentedModel;
    }
}
