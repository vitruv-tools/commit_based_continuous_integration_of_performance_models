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
class FileLayout {
	static final String vsumDirName = "vsum";
	static final String pcmDirName = "pcm";
	static final String pcmRepositoryFileName = "Repository.repository";
	static final String pcmSystemFileName = "System.system";
	static final String pcmAllocationFileName = "Allocation.allocation";
	static final String pcmUsageModelFileName = "Usage.usagemodel";
	static final String pcmResourceEnvironmentFileName = "ResourceEnvironment.resourceenvironment";
	static final String imDirName = "im";
	static final String imFileName = "InstrumentationModel.imm";
	static final String javaDirName = "java";
	static final String commitsFileName = ".commits";
	Path rootPath;
	Path vsumPath;
	Path pcmRepositoryPath;
	URI pcmRepositoryURI;
	Path pcmSystemPath;
	URI pcmSystemURI;
	Path pcmAllocationPath;
	URI pcmAllocationURI;
	Path pcmUsageModelPath;
	URI pcmUsageModelURI;
	Path pcmResourceEnvironmentPath;
	URI pcmResourceEnvironmentURI;
	Path imPath;
	URI imURI;
	Path javaPath;
	Path commitsPath;
	
	new(Path rootDir) {
		rootPath = rootDir;
		vsumPath = rootDir.resolve(vsumDirName);
		var pcm = rootDir.resolve(pcmDirName);
		pcmRepositoryPath = pcm.resolve(pcmRepositoryFileName).toAbsolutePath();
		pcmRepositoryURI = URI.createFileURI(pcmRepositoryPath.toString());
		pcmSystemPath = pcm.resolve(pcmSystemFileName).toAbsolutePath();
		pcmSystemURI = URI.createFileURI(pcmSystemPath.toString());
		pcmAllocationPath = pcm.resolve(pcmAllocationFileName).toAbsolutePath();
		pcmAllocationURI = URI.createFileURI(pcmAllocationPath.toString());
		pcmUsageModelPath = pcm.resolve(pcmUsageModelFileName).toAbsolutePath();
		pcmUsageModelURI = URI.createFileURI(pcmUsageModelPath.toString());
		pcmResourceEnvironmentPath = pcm.resolve(pcmResourceEnvironmentFileName).toAbsolutePath();
		pcmResourceEnvironmentURI = URI.createFileURI(pcmResourceEnvironmentPath.toString());
		pcm = rootDir.resolve(imDirName);
		imPath = pcm.resolve(imFileName).toAbsolutePath();
		imURI = URI.createFileURI(imPath.toString());
		javaPath = rootDir.resolve(javaDirName);
		commitsPath = rootDir.resolve(commitsFileName);
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
		Files.walk(vsumPath).sorted(Comparator.reverseOrder()).forEach[
			it.toFile.delete
		]
	}

	def delete() {
		Files.walk(rootPath).sorted(Comparator.reverseOrder()).forEach[
			it.toFile.delete
		]
	}
}
