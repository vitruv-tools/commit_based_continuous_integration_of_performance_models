package cipm.consistency.vsum;

import cipm.consistency.base.shared.pcm.LocalFilesystemPCM
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * Internal layout for the directory structure of VSUM, PCM and IMM.
 * 
 * @author Martin Armbruster
 */
@Accessors
class FileSystemLayout {
	static final String vsumDirName = "vsum";
	static final String pcmDirName = "pcm";
	static final String imDirName = "im";

	static final String pcmRepositoryFileName = "Repository.repository";
	static final String pcmSystemFileName = "System.system";
	static final String pcmAllocationFileName = "Allocation.allocation";
	static final String pcmUsageModelFileName = "Usage.usagemodel";
	static final String pcmResourceEnvironmentFileName = "ResourceEnvironment.resourceenvironment";
	
	static final String vsumCorrespondenceModelName = "correspondence.correspondence"

	static final String imFileName = "InstrumentationModel.imm";
	static final String commitsFileName = ".commits";

	Path rootDirPath
	Path vsumDirPath
	Path pcmDirPath
	Path imDirPath

	Path pcmRepositoryPath
	URI pcmRepositoryURI
	Path pcmSystemPath
	URI pcmSystemURI
	Path pcmAllocationPath
	URI pcmAllocationURI
	Path pcmUsageModelPath
	URI pcmUsageModelURI
	Path pcmResourceEnvironmentPath
	URI pcmResourceEnvironmentURI
	
	Path vsumCorrespondenceModelPath
	URI vsumCorrespondenceModelUri

	Path imFilePath;
	URI imURI;
	Path javaPath;
	Path commitsFilePath;
	
	new(Path rootDirPath) {
		this.rootDirPath = rootDirPath;
		if (!rootDirPath.toFile().exists()) {
			Files.createDirectories(rootDirPath);
		}

		vsumDirPath = rootDirPath.resolve(vsumDirName);
		pcmDirPath = rootDirPath.resolve(pcmDirName);
		imDirPath = rootDirPath.resolve(imDirName);

		pcmRepositoryPath = pcmDirPath.resolve(pcmRepositoryFileName).toAbsolutePath();
		pcmRepositoryURI = URI.createFileURI(pcmRepositoryPath.toString());
		pcmSystemPath = pcmDirPath.resolve(pcmSystemFileName).toAbsolutePath();
		pcmSystemURI = URI.createFileURI(pcmSystemPath.toString());
		pcmAllocationPath = pcmDirPath.resolve(pcmAllocationFileName).toAbsolutePath();
		pcmAllocationURI = URI.createFileURI(pcmAllocationPath.toString());
		pcmUsageModelPath = pcmDirPath.resolve(pcmUsageModelFileName).toAbsolutePath();
		pcmUsageModelURI = URI.createFileURI(pcmUsageModelPath.toString());
		pcmResourceEnvironmentPath = pcmDirPath.resolve(pcmResourceEnvironmentFileName).toAbsolutePath();
		pcmResourceEnvironmentURI = URI.createFileURI(pcmResourceEnvironmentPath.toString());
		
		vsumCorrespondenceModelPath = vsumDirPath.resolve(vsumCorrespondenceModelName)
		vsumCorrespondenceModelUri = URI.createFileURI(vsumCorrespondenceModelPath.toString());

		imFilePath = imDirPath.resolve(imFileName).toAbsolutePath();
		imURI = URI.createFileURI(imFilePath.toString());

//		javaPath = rootDirPath.resolve(javaDirName);
		commitsFilePath = rootDirPath.resolve(commitsFileName);
	}

	def LocalFilesystemPCM getFilePCM() {
		var filePCM = new LocalFilesystemPCM();
		filePCM.setRepositoryFile(pcmRepositoryPath.toFile());
		filePCM.setAllocationModelFile(pcmAllocationPath.toFile());
		filePCM.setSystemFile(pcmSystemPath.toFile());
		filePCM.setResourceEnvironmentFile(pcmResourceEnvironmentPath.toFile());
		filePCM.setUsageModelFile(pcmUsageModelPath.toFile());
		return filePCM;
	}
	
	def deleteVsum() {
		Files.walk(vsumDirPath).sorted(Comparator.reverseOrder()).forEach[
			it.toFile.delete
		]
	}

	def delete() {
		Files.walk(rootDirPath).sorted(Comparator.reverseOrder()).forEach[
			it.toFile.delete
		]
	}
}
