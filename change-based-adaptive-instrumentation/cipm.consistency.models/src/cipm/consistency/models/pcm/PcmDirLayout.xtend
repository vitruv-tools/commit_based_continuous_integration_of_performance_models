package cipm.consistency.models.pcm;

import cipm.consistency.base.shared.pcm.LocalFilesystemPCM
import java.nio.file.Path
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Accessors
import cipm.consistency.models.ModelDirLayoutImpl

@Accessors
class PcmDirLayout extends ModelDirLayoutImpl {
	static final String pcmRepositoryFileName = "Repository.repository";
	static final String pcmSystemFileName = "System.system";
	static final String pcmAllocationFileName = "Allocation.allocation";
	static final String pcmUsageModelFileName = "Usage.usagemodel";
	static final String pcmResourceEnvironmentFileName = "ResourceEnvironment.resourceenvironment";

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
	
	override void initialize(Path rootDirPath) {
		super.initialize(rootDirPath)
		pcmRepositoryPath = rootDirPath.resolve(pcmRepositoryFileName).toAbsolutePath();
		pcmRepositoryURI = URI.createFileURI(pcmRepositoryPath.toString());
		pcmSystemPath = rootDirPath.resolve(pcmSystemFileName).toAbsolutePath();
		pcmSystemURI = URI.createFileURI(pcmSystemPath.toString());
		pcmAllocationPath = rootDirPath.resolve(pcmAllocationFileName).toAbsolutePath();
		pcmAllocationURI = URI.createFileURI(pcmAllocationPath.toString());
		pcmUsageModelPath = rootDirPath.resolve(pcmUsageModelFileName).toAbsolutePath();
		pcmUsageModelURI = URI.createFileURI(pcmUsageModelPath.toString());
		pcmResourceEnvironmentPath = rootDirPath.resolve(pcmResourceEnvironmentFileName).toAbsolutePath();
		pcmResourceEnvironmentURI = URI.createFileURI(pcmResourceEnvironmentPath.toString());
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
}
