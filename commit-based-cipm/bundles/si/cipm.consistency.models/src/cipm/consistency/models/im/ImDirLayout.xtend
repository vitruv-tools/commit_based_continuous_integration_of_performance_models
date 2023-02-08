package cipm.consistency.models.im;

import cipm.consistency.models.ModelDirLayoutImpl
import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
class ImDirLayout extends ModelDirLayoutImpl {
	static final String imFileName = "imm.imm"
	
	Path imFilePath
	URI imFileUri
	
	
	override void initialize(Path rootDirPath) {
		super.initialize(rootDirPath)

		imFilePath = rootDirPath.resolve(imFileName)
		imFileUri = URI.createFileURI(imFilePath.toAbsolutePath().toString())
	}
}
