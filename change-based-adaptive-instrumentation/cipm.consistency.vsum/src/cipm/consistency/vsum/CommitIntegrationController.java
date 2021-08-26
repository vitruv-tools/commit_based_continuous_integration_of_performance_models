package cipm.consistency.vsum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jgit.api.errors.GitAPIException;

import cipm.consistency.base.models.instrumentation.InstrumentationModel.ActionInstrumentationPoint;
import cipm.consistency.commitintegration.CommitChangePropagator;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.designtime.instrumentation2.CodeInstrumenter;
import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

public class CommitIntegrationController {
	private final static Logger logger = Logger.getLogger("cipm." + CommitIntegrationController.class.getSimpleName());
	private VSUMFacade facade;
	private CommitChangePropagator prop;
	private Resource instrumentedModel;
	
	public CommitIntegrationController(Path rootPath, String repositoryPath, Path settingsPath)
			throws IOException, GitAPIException {
		CommitIntegrationSettingsContainer.initialize(settingsPath);
		facade = new VSUMFacade(rootPath);
		prop = new CommitChangePropagator(repositoryPath,
				facade.getFileLayout().getJavaPath().toString(), facade.getVSUM());
		prop.initialize();
	}
	
	public boolean propagateChanges(String oldCommit, String newCommit) throws IOException, GitAPIException {
		return propagateChanges(oldCommit, newCommit, true);
	}
	
	public boolean propagateChanges(String oldCommit, String newCommit, boolean storeInstrumentedModel)
			throws IOException, GitAPIException {
		long overallTimer = System.currentTimeMillis();
		instrumentedModel = null;
		Path insDir = this.prop.getJavaFileSystemLayout().getInstrumentationCopy();
		removeInstrumentationDirectory(insDir);
		this.facade.getInstrumentationModel().eAllContents().forEachRemaining(ip -> {
			if (ip instanceof ActionInstrumentationPoint) {
				((ActionInstrumentationPoint) ip).setActive(false);
			}
		});
		this.facade.getInstrumentationModel().eResource().save(null);
		long fineTimer = System.currentTimeMillis();
		boolean result = prop.propagateChanges(oldCommit, newCommit);
		fineTimer = System.currentTimeMillis() - fineTimer;
		EvaluationDataContainer.getGlobalContainer().getExecutionTimes()
				.setChangePropagationTime(fineTimer);
		if (result) {
			boolean hasChangedIM = false;
			for (var iter = this.facade.getInstrumentationModel().eAllContents(); iter.hasNext();) {
				var next = iter.next();
				if (next instanceof ActionInstrumentationPoint) {
					hasChangedIM |= ((ActionInstrumentationPoint) next).isActive();
				}
			}
			if (hasChangedIM) {
				logger.debug("No instrumentation points changed.");
			}
			boolean fullInstrumentation = CommitIntegrationSettingsContainer.getSettingsContainer()
					.getPropertyAsBoolean(SettingKeys.PERFORM_FULL_INSTRUMENTATION);
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
	
	public Resource instrumentCode(boolean performFullInstrumentation) {
		Path insDir = this.prop.getJavaFileSystemLayout().getInstrumentationCopy();
		removeInstrumentationDirectory(insDir);
		return performInstrumentation(insDir, performFullInstrumentation);
	}
	
	private void removeInstrumentationDirectory(Path instrumentationDirectory) {
		if (Files.exists(instrumentationDirectory)) {
			logger.debug("Deleting the instrumentation directory.");
			try {
				FileUtils.deleteDirectory(instrumentationDirectory.toFile());
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	
	private Resource performInstrumentation(Path instrumentationDirectory, boolean performFullInstrumentation) {
		Resource javaModel = getJavaModelResource();
		return new CodeInstrumenter().instrument(
			this.facade.getInstrumentationModel(),
			this.facade.getVSUM().getCorrespondenceModel(),
			javaModel, instrumentationDirectory,
			this.prop.getJavaFileSystemLayout().getLocalJavaRepo(), !performFullInstrumentation);
	}
	
	public void shutdown() {
		facade.getVSUM().dispose();
		prop.shutdown();
	}
	
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
