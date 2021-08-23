package cipm.consistency.vsum.test.deployment;

import static org.junit.jupiter.api.Assertions.fail;

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

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;

import cipm.consistency.commitintegration.ExternalCommandExecutionUtility;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.vsum.test.TeaStoreCITest;

public class DeploymentUtility extends TeaStoreCITest {
	private final static Logger logger = Logger.getLogger("cipm." + DeploymentUtility.class.getSimpleName());
	private Path instrumentationCodeDir;

	@Test
	public void compileAndDeployInstrumentedCode() throws IOException {
		instrumentationCodeDir = this.controller.getCommitChangePropagator()
				.getJavaFileSystemLayout().getInstrumentationCopy();
		if (Files.exists(instrumentationCodeDir)) {
			boolean compilationResult = compileInstrumentedCode();
			if (compilationResult) {
				Path deployPath = Paths.get(CommitIntegrationSettingsContainer.getSettingsContainer()
						.getProperty(SettingKeys.DEPLOYMENT_PATH));
				var result = copyArtifacts(deployPath);
				result.forEach(p -> {
					try {
						removeMonitoringClasses(p);
					} catch (IOException e) {
						fail(e);
					}
				});
			} else {
				logger.debug("Could not compile the instrumented code.");
			}
		}
	}
	
	private boolean compileInstrumentedCode() {
		String compileScript = CommitIntegrationSettingsContainer.getSettingsContainer()
			.getProperty(SettingKeys.PATH_TO_COMPILATION_SCRIPT);
		compileScript = new File(compileScript).getAbsolutePath();
		if (Files.exists(instrumentationCodeDir)) {
			logger.debug("Compiling the instrumented code.");
			return ExternalCommandExecutionUtility.runScript(instrumentationCodeDir.toFile(), compileScript);
		} else {
			logger.debug("No instrumented code available.");
		}
		return false;
	}
	
	private List<Path> copyArtifacts(Path deployPath) throws IOException {
		logger.debug("Copying the artifacts to " + deployPath);
		var warFiles = Files.walk(instrumentationCodeDir).filter(Files::isRegularFile)
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
				fail(e);
			}
		});
		return result;
	}
	
	private void removeMonitoringClasses(Path file) throws IOException {
		logger.debug("Removing the monitoring classes from the artifacts.");
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
								fail(e);
							}
						} else if (fullPath.endsWith(tmcEndPath) || fullPath.endsWith(spEndPath)) {
							return true;
						}
						return false;
					}).forEach(p -> {
						try {
							Files.delete(p);
						} catch (IOException e) {
							fail(e);
						}
					});
				} catch (IOException e) {
					fail(e);
				}
			});
		}
	}
}
