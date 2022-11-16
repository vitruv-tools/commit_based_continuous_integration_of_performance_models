package cipm.consistency.models.im;

import cipm.consistency.models.ModelDirLayoutImpl
import java.nio.file.Files
import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
class ImDirLayout extends ModelDirLayoutImpl {
	static final String imFileName = "imm.imm"
	static final String instrumentationDirName = "instrumented"

	Path instrumentationDirPath
	
	Path imFilePath
	URI imFileUri
	
	
	override void initialize(Path rootDirPath) {
		super.initialize(rootDirPath)

		instrumentationDirPath = rootDirPath.resolve(instrumentationDirName)
		if (!instrumentationDirPath.toFile().exists()) {
			Files.createDirectories(instrumentationDirPath);
		}

		imFilePath = rootDirPath.resolve(imFileName)
		imFileUri = URI.createFileURI(imFilePath.toAbsolutePath().toString())
	}
}
