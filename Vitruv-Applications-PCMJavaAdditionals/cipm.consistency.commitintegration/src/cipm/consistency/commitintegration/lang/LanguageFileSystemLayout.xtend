package cipm.consistency.commitintegration.lang

import java.nio.file.Path
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
abstract class LanguageFileSystemLayout {
	static final package String localRepoDirName = "local-repo-clone"
	static final package String modelFileContainerDirName = "vsum-variant"
	// TODO rename
	static final package String modelFileName = "Java.javaxmi"
	static final package String instrumentationDirName = "instrumented"
	static final package String moduleConfigurationFileName = "module-configuration.properties"
	static final package String externalCallTargetPairsFileName = "external-call-target-pairs.json"
	package Path localRepo
	package Path modelFileContainer
	package Path modelFile
	package Path instrumentationDir
	package Path moduleConfiguration
	package Path externalCallTargetPairsFile

	new(Path root) {
		this.initialize(root)
	}

	def void initialize(Path root) {
		localRepo = root.resolve(localRepoDirName)
		modelFileContainer = root.resolve(modelFileContainerDirName)
		modelFile = modelFileContainer.resolve(modelFileName)
		instrumentationDir = root.resolve(instrumentationDirName)
		moduleConfiguration = root.resolve(moduleConfigurationFileName)
		externalCallTargetPairsFile = root.resolve(externalCallTargetPairsFileName)
	}
}
