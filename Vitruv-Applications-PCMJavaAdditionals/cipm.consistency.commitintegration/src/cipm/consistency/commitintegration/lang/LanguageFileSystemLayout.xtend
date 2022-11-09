package cipm.consistency.commitintegration.lang

import java.nio.file.Files
import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
abstract class LanguageFileSystemLayout {
	static final package String localRepoDirName = "local-repo-clone"
	static final package String instrumentationDirName = "instrumented"
	static final package String modelFileDirName = "vsum-variant"
	static final package String parsedDirName = "parsed"

	static final package String parsedFileName = "Code.xmi"

	

	static final package String modelFileName = "Code.xmi"
	static final package String moduleConfigurationFileName = "module-configuration.properties"
	static final package String externalCallTargetPairsFileName = "external-call-target-pairs.json"

	package Path localRepoDir
	package Path modelDir
	package Path parsedDirPath
	package Path instrumentationDir

	package Path modelFilePath
	package Path parsedFilePath
	package Path moduleConfigurationPath
	package Path externalCallTargetPairsFilePath
	
	package URI modelFileUri
	package URI parsedFileUri

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
		parsedDirPath = root.resolve(parsedDirName)

		modelFilePath = modelDir.resolve(modelFileName).toAbsolutePath()
		parsedFilePath = parsedDirPath.resolve(parsedFileName).toAbsolutePath()
		moduleConfigurationPath = root.resolve(moduleConfigurationFileName)
		externalCallTargetPairsFilePath = root.resolve(externalCallTargetPairsFileName)
		
		modelFileUri = URI.createFileURI(modelFilePath.toString())
		parsedFileUri = URI.createFileURI(parsedFilePath.toString())
	}
}
