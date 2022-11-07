package cipm.consistency.commitintegration.lang

import java.nio.file.Files
import java.nio.file.Path
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
abstract class LanguageFileSystemLayout {
	static final package String localRepoDirName = "local-repo-clone"
	static final package String modelFileDirName = "vsum-variant"
	static final package String instrumentationDirName = "instrumented"

	static final package String modelFileName = "Code.xmi"
	static final package String moduleConfigurationFileName = "module-configuration.properties"
	static final package String externalCallTargetPairsFileName = "external-call-target-pairs.json"

	package Path localRepoDir
	package Path modelDir
	package Path instrumentationDir

	package Path modelFile
	package Path moduleConfiguration
	package Path externalCallTargetPairsFile

	new(Path root) {
		this.initialize(root)
	}

	def void initialize(Path root) {
		if (!root.toFile().exists()) {
			Files.createDirectories(root);
		}

		localRepoDir = root.resolve(localRepoDirName)
		modelDir = root.resolve(LanguageFileSystemLayout.modelFileDirName)
		instrumentationDir = root.resolve(instrumentationDirName)

		modelFile = modelDir.resolve(modelFileName)
		moduleConfiguration = root.resolve(moduleConfigurationFileName)
		externalCallTargetPairsFile = root.resolve(externalCallTargetPairsFileName)
	}
}
