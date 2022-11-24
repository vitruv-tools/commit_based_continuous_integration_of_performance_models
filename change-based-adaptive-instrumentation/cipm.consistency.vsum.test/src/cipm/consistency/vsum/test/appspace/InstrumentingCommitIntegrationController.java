package cipm.consistency.vsum.test.appspace;

import cipm.consistency.commitintegration.CommitIntegrationController;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.commitintegration.util.ExternalCommandExecutionUtils;
import cipm.consistency.designtime.instrumentation2.CodeInstrumenter;
import cipm.consistency.models.CodeModelFacade;
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
import org.eclipse.emf.ecore.resource.Resource;
import tools.vitruv.change.composite.description.PropagatedChange;

public abstract class InstrumentingCommitIntegrationController<CM extends CodeModelFacade>
        extends CommitIntegrationController<CM> {
    private static final Logger LOGGER = Logger.getLogger(InstrumentingCommitIntegrationController.class.getName());
    Resource instrumentedModel;
    
    private boolean storeInstrumentedModel = false;

    /**
     * Propagates changes between two commits to the VSUM.
     * 
     * @param start
     *            the first commit.
     * @param end
     *            the second commit.
     * @return A list of propagated changes (which may be empty) or {null}.
     * @throws IncorrectObjectTypeException
     * @throws IOException
     *             if something from the repositories cannot be read.
     */
//    protected List<PropagatedChange> propagateChanges(String start, String end)
//            throws IncorrectObjectTypeException, IOException {
//        String commitId = end.getId()
//            .getName();
//        LOGGER.debug("Obtaining all differences.");
//        List<DiffEntry> diffs = state.getGitRepositoryWrapper()
//            .computeDiffsBetweenTwoCommits(start, end);
//        if (diffs.size() == 0) {
//            LOGGER.info("No source files changed for " + commitId + ": No propagation is performed.");
//            return List.of();
//        }
//        var cs = EvaluationDataContainer.getGlobalContainer()
//            .getChangeStatistic();
//        String oldId = start != null ? start.getId()
//            .getName() : null;
//        cs.setOldCommit(oldId != null ? oldId : "");
//        cs.setNewCommit(commitId);
//        cs.setNumberCommits(state.getGitRepositoryWrapper()
//            .getAllCommitsBetweenTwoCommits(oldId, commitId)
//            .size() + 1);
//
//        if (checkout(commitId)) {
//            return propagateCurrentCheckout();
//        }
//        return null;
//    }

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
     */
    @Override
    protected List<PropagatedChange> propagateChanges(String oldCommit, String newCommit)
            throws IOException {
        {
            var parent = state.getDirLayout()
                .getCommitsFilePath()
                .toAbsolutePath()
                .getParent();
            if (Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(state.getDirLayout()
            .getCommitsFilePath())) {
            if (oldCommit != null) {
                writer.write(oldCommit + "\n");
            }
            writer.write(newCommit + "\n");
        }

        long overallTimer = System.currentTimeMillis();
        state.getImFacade()
            .getDirLayout()
            .clean();
        var insDir = state.getImFacade()
            .getDirLayout()
            .getRootDirPath();

        // Deactivate all action instrumentation points.
        state.getImFacade()
            .getModel()
            .getPoints()
            .forEach(sip -> sip.getActionInstrumentationPoints()
                .forEach(aip -> aip.setActive(false)));
        state.getImFacade()
            .saveToDisk();

        long fineTimer = System.currentTimeMillis();

        // Propagate the changes.
        var propagatedChanges = super.propagateChanges(oldCommit, newCommit);

        fineTimer = System.currentTimeMillis() - fineTimer;
        EvaluationDataContainer.getGlobalContainer()
            .getExecutionTimes()
            .setChangePropagationTime(fineTimer);

        if (propagatedChanges != null) {
            // TODO out commented this
//         @SuppressWarnings("restriction")
//         ExternalCallEmptyTargetFiller filler = new ExternalCallEmptyTargetFiller(facade.getVSUM()
//             .getCorrespondenceModel(),
//                 facade.getPCMWrapper()
//                     .getRepository(),
//                 propagator.getJavaFileLayout()
//                     .getExternalCallTargetPairsFile());
//         filler.fillExternalCalls();

            boolean hasChangedIM = false;
            for (var sip : state.getImFacade()
                .getModel()
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

        return propagatedChanges;
    }

    /**
     * Removes potentially available instrumented code and performs a new instrumentation.
     * 
     * @param performFullInstrumentation
     *            true if a full instrumentation shall be performed. false otherwise.
     * @return the instrumented code model as a copy of the code model in the V-SUM.
     */
    public Resource instrumentCode(boolean performFullInstrumentation) {
        Path insDir = state.getImFacade()
            .getDirLayout()
            .getRootDirPath();
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
        return CodeInstrumenter.instrument(state.getImFacade()
            .getModel(),
                state.getVsumFacade()
                    .getVsum()
                    .getCorrespondenceModel(),
                state.getCodeModelFacade()
                    .getResource(),
                instrumentationDirectory, state.getGitRepositoryWrapper()
                    .getWorkTree()
                    .toPath(),
                !performFullInstrumentation);
    }

    /**
     * Compiles and deploy the instrumented code.
     * 
     * @throws IOException
     *             if an IO operation fails.
     */
    public void compileAndDeployInstrumentedCode() throws IOException {
        Path instrumentationCodeDir = state.getImFacade()
            .getDirLayout()
            .getRootDirPath();
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
//     try (FileSystem fileSys = FileSystems.newFileSystem(file, options)) {
//         String tmcEndPath = "cipm/consistency/bridge/monitoring/controller/ThreadMonitoringController.class";
//         String spEndPath = "cipm/consistency/bridge/monitoring/controller/ServiceParameters.class";
//         fileSys.getRootDirectories()
//             .forEach(root -> {
//                 try {
//                     Files.walk(root)
//                         .filter(p -> {
//                             String fullPath = p.toString();
//                             if (fullPath.endsWith(".jar") || fullPath.endsWith(".war")
//                                     || fullPath.endsWith(".zip")) {
//                                 try {
//                                     removeMonitoringClasses(p);
//                                 } catch (IOException e) {
//                                     LOGGER.error(e);
//                                 }
//                             } else if (fullPath.endsWith(tmcEndPath) || fullPath.endsWith(spEndPath)) {
//                                 return true;
//                             }
//                             return false;
//                         })
//                         .forEach(p -> {
//                             try {
//                                 Files.delete(p);
//                             } catch (IOException e) {
//                                 LOGGER.error(e);
//                             }
//                         });
//                 } catch (IOException e) {
//                     LOGGER.error(e);
//                 }
//             });
//     }
    }

// @SuppressWarnings("restriction")
// public Resource getModelResource() {
//     var modelUri = getFileLayout().getModelFileUri();
//     var model = getVsumFacade().getVsum()
//         .getModelInstance(modelUri);
//     if (model != null)
//         return model.getResource();
//
//     LOGGER.error(String.format("Model URI does not exist in vsum: %s", modelUri));
//     return null;
// }

// public Resource getLastInstrumentedModelResource() {
//     return instrumentedModel;
// }
    
    public void setStoreInstrumentedModel(boolean store) {
        storeInstrumentedModel = store;
    }
}
