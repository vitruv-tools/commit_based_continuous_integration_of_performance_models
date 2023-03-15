package cipm.consistency.commitintegration.lang.lua

import cipm.consistency.models.ModelDirLayoutImpl
import cipm.consistency.models.code.CodeModelDirLayout
import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
class LuaDirLayout extends ModelDirLayoutImpl implements CodeModelDirLayout {
	static final package String localRepoDirName = "local-repo-clone"
	static final package String instrumentationDirName = "instrumented"

	static final package String parsedFileName = "parsed.code.xmi"
//	static final package String modelFileName = "vsum.code.xmi"
	static final package String moduleConfigurationFileName = "module-configuration.properties"
	static final package String externalCallTargetPairsFileName = "external-call-target-pairs.json"

	package Path localRepoDir
	package Path instrumentationDir

//	package Path modelFilePath
	package Path parsedFilePath
	package Path moduleConfigurationPath
	package Path externalCallTargetPairsFilePath
	
	package URI modelFileUri
	package URI parsedFileUri

	override void initialize(Path rootDirPath) {
		super.initialize(rootDirPath)

		localRepoDir = rootDirPath.resolve(localRepoDirName)
		instrumentationDir = rootDirPath.resolve(instrumentationDirName)

//		modelFilePath = rootDirPath.resolve(modelFileName).toAbsolutePath()
		parsedFilePath = rootDirPath.resolve(parsedFileName).toAbsolutePath()
		moduleConfigurationPath = rootDirPath.resolve(moduleConfigurationFileName)
		externalCallTargetPairsFilePath = rootDirPath.resolve(externalCallTargetPairsFileName)
		
//		modelFileUri = URI.createFileURI(modelFilePath.toString())
		parsedFileUri = URI.createFileURI(parsedFilePath.toString())
	}
	
	override getParsedCodePath() {
		parsedFilePath
	}
	
	override getParsedCodeURI() {
		parsedFileUri
	}
	
}