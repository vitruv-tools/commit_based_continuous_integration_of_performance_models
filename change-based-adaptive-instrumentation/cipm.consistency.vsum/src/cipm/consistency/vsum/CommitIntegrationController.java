package cipm.consistency.vsum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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

import cipm.consistency.commitintegration.CommitChangePropagator;
import cipm.consistency.commitintegration.ExternalCommandExecutionUtils;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.cpr.javapcm.additional.validation.ExternalCallEmptyTargetFiller;
import cipm.consistency.designtime.instrumentation2.CodeInstrumenter;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

/**
 * This central class is responsible for controlling the complete change propagation and adaptive instrumentation.
 * 
 * @author Martin Armbruster
 */
public class CommitIntegrationController {
	private static final Logger LOGGER = Logger.getLogger("cipm." + CommitIntegrationController.class.getSimpleName());
	private VSUMFacade facade;
	private CommitChangePropagator prop;
	private Resource instrumentedModel;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param rootPath path to the root directory in which all data is stored.
	 * @param repositoryPath path to the remote repository from which commits are fetched.
	 * @param settingsPath path to the settings file.
	 * @throws IOException if an IO operation fails.
	 * @throws GitAPIException if a Git operation fails.
	 */
	public CommitIntegrationController(Path rootPath, String repositoryPath, Path settingsPath)
			throws IOException, GitAPIException {
		CommitIntegrationSettingsContainer.initialize(settingsPath);
		facade = new VSUMFacade(rootPath);
		prop = new CommitChangePropagator(repositoryPath,
				facade.getFileLayout().getJavaPath().toString(), facade.getVSUM());
		prop.initialize();
	}
	
	/**
	 * Propagates the changes between two commits.
	 * 
	 * @param oldCommit the first commit or null.
	 * @param newCommit the second commit. Changes between the oldCommit and newCommit are propagated.
	 * @return true if the propagation was successful. false otherwise.
	 * @throws IOException if an IO operation fails.
	 * @throws GitAPIException if a Git operation fails.
	 */
	public boolean propagateChanges(String oldCommit, String newCommit) throws IOException, GitAPIException {
		return propagateChanges(oldCommit, newCommit, true);
	}
	
	/**
	 * Propagates the changes between two commits.
	 * 
	 * @param oldCommit the first commit or null.
	 * @param newCommit the second commit. Changes between the oldCommit and newCommit are propagated.
	 * @param storeInstrumentedModel true if the instrumented code model shall be stored in this instance.
	 * @return true if the propagation was successful. false otherwise.
	 * @throws IOException if an IO operation fails.
	 * @throws GitAPIException if a Git operation fails.
	 */
	public boolean propagateChanges(String oldCommit, String newCommit, boolean storeInstrumentedModel)
			throws IOException, GitAPIException {
		Files.createDirectory(this.facade.getFileLayout().getCommitsPath().toAbsolutePath().getParent());
		try (BufferedWriter writer = Files.newBufferedWriter(this.facade.getFileLayout().getCommitsPath())) {
			if (oldCommit != null) {
				writer.write(oldCommit + "\n");
			}
			writer.write(newCommit + "\n");
		}
		
		long overallTimer = System.currentTimeMillis();
		instrumentedModel = null;
		Path insDir = this.prop.getJavaFileSystemLayout().getInstrumentationCopy();
		removeInstrumentationDirectory(insDir);
		
		// Deactivate all action instrumentation points.
		this.facade.getInstrumentationModel().getPoints().forEach(sip -> 
			sip.getActionInstrumentationPoints().forEach(aip -> aip.setActive(false)));
		this.facade.getInstrumentationModel().eResource().save(null);
		
		long fineTimer = System.currentTimeMillis();
		
		// Propagate the changes.
		boolean result = prop.propagateChanges(oldCommit, newCommit);
		
		fineTimer = System.currentTimeMillis() - fineTimer;
		EvaluationDataContainer.getGlobalContainer().getExecutionTimes()
				.setChangePropagationTime(fineTimer);
		
		if (result) {
			@SuppressWarnings("restriction")
			ExternalCallEmptyTargetFiller filler = new ExternalCallEmptyTargetFiller(
					facade.getVSUM().getCorrespondenceModel(),
					facade.getPCMWrapper().getRepository(),
					prop.getJavaFileSystemLayout().getExternalCallTargetPairsFile());
			filler.fillExternalCalls();
			
			boolean hasChangedIM = false;
			for (var sip : this.facade.getInstrumentationModel().getPoints()) {
				for (var aip : sip.getActionInstrumentationPoints()) {
					hasChangedIM |= aip.isActive();
				}
			}
			if (!hasChangedIM) {
				LOGGER.debug("No instrumentation points changed.");
			}
			boolean fullInstrumentation = CommitIntegrationSettingsContainer.getSettingsContainer()
					.getPropertyAsBoolean(SettingKeys.PERFORM_FULL_INSTRUMENTATION);
			
			// Instrument the code only if there is a new action instrumentation point or if a full instrumentation
			// shall be performed.
			if (hasChangedIM || fullInstrumentation) {
				fineTimer = System.currentTimeMillis();
				Resource insModel = performInstrumentation(insDir, fullInstrumentation);
				fineTimer = System.currentTimeMillis() - fineTimer;
				EvaluationDataContainer.getGlobalContainer().getExecutionTimes()
						.setInstrumentationTime(fineTimer);
				if (storeInstrumentedModel) {
					this.instrumentedModel = insModel;
				}
			}
		}
		overallTimer = System.currentTimeMillis() - overallTimer;
		EvaluationDataContainer.getGlobalContainer().getExecutionTimes().setOverallTime(overallTimer);
		return result;
	}

	/**
	 * Removes potentially available instrumented code and performs a new instrumentation.
	 * 
	 * @param performFullInstrumentation true if a full instrumentation shall be performed. false otherwise.
	 * @return the instrumented code model as a copy of the code model in the V-SUM.
	 */
	public Resource instrumentCode(boolean performFullInstrumentation) {
		Path insDir = this.prop.getJavaFileSystemLayout().getInstrumentationCopy();
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
		Resource javaModel = getJavaModelResource();
		return CodeInstrumenter.instrument(
			this.facade.getInstrumentationModel(),
			this.facade.getVSUM().getCorrespondenceModel(),
			javaModel, instrumentationDirectory,
			this.prop.getJavaFileSystemLayout().getLocalJavaRepo(), !performFullInstrumentation);
	}

	/**
	 * Compiles and deploy the instrumented code.
	 * 
	 * @throws IOException if an IO operation fails.
	 */
	public void compileAndDeployInstrumentedCode() throws IOException {
		Path instrumentationCodeDir = this.prop.getJavaFileSystemLayout().getInstrumentationCopy();
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
		var warFiles = Files.walk(insCode).filter(Files::isRegularFile)
			.filter(p -> p.getFileName().toString().endsWith(".war"))
			.collect(Collectors.toCollection(ArrayList::new));
		List<String> fileNames = new ArrayList<>();
		for (int idx = 0; idx < warFiles.size(); idx++) {
			String name = warFiles.get(idx).getFileName().toString();
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
		try (FileSystem fileSys = FileSystems.newFileSystem(file, options)) {
			String tmcEndPath = "cipm/consistency/bridge/monitoring/controller/ThreadMonitoringController.class";
			String spEndPath = "cipm/consistency/bridge/monitoring/controller/ServiceParameters.class";
			fileSys.getRootDirectories().forEach(root -> {
				try {
					Files.walk(root).filter(p -> {
						String fullPath = p.toString();
						if (fullPath.endsWith(".jar") || fullPath.endsWith(".war")
								|| fullPath.endsWith(".zip")) {
							try {
								removeMonitoringClasses(p);
							} catch (IOException e) {
								LOGGER.error(e);
							}
						} else if (fullPath.endsWith(tmcEndPath) || fullPath.endsWith(spEndPath)) {
							return true;
						}
						return false;
					}).forEach(p -> {
						try {
							Files.delete(p);
						} catch (IOException e) {
							LOGGER.error(e);
						}
					});
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
		}
	}

	/**
	 * Shutdowns the environment.
	 */
	@SuppressWarnings("restriction")
	public void shutdown() {
		facade.getVSUM().dispose();
		prop.shutdown();
	}
	
	@SuppressWarnings("restriction")
	public Resource getJavaModelResource() {
		return this.facade.getVSUM().getModelInstance(
				URI.createFileURI(prop.getJavaFileSystemLayout().getJavaModelFile().toString()))
				.getResource();
	}
	
	public Resource getLastInstrumentedModelResource() {
		return this.instrumentedModel;
	}
	
	public VSUMFacade getVSUMFacade() {
		return facade;
	}
	
	public CommitChangePropagator getCommitChangePropagator() {
		return prop;
	}
}
