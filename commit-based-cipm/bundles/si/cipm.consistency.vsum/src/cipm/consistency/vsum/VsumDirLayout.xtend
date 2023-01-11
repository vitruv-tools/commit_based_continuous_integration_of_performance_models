package cipm.consistency.vsum;

import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors
import cipm.consistency.models.ModelDirLayoutImpl

/**
 * Internal layout for the directory structure of VSUM, PCM and IMM.
 * 
 * @author Martin Armbruster
 */
@Accessors
class VsumDirLayout extends ModelDirLayoutImpl {
	static final String vsumCorrespondenceModelName = "correspondence.correspondence"

//	static final String commitsFileName = ".commits";
//
//	Path imDirPath
	
	Path vsumCorrespondenceModelPath
	URI vsumCorrespondenceModelUri

//	Path commitsFilePath;
	
	override initialize(Path rootDirPath) {
		super.initialize(rootDirPath)
		vsumCorrespondenceModelPath = rootDirPath.resolve(vsumCorrespondenceModelName)
		vsumCorrespondenceModelUri = URI.createFileURI(vsumCorrespondenceModelPath.toString());

//		commitsFilePath = rootDirPath.resolve(commitsFileName);
	}
}
