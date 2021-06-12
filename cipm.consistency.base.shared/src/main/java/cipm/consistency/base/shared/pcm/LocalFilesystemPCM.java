package cipm.consistency.base.shared.pcm;

import java.io.File;

import lombok.Data;

/**
 * Contains file system paths where a PCM model can be stored.
 * 
 * @author David Monschein
 *
 */
@Data
public class LocalFilesystemPCM {

	private File repositoryFile;
	private File systemFile;
	private File usageModelFile;
	private File allocationModelFile;
	private File resourceEnvironmentFile;

	/**
	 * Determines whether the given paths for the models are valid. Does not check
	 * its content!
	 * 
	 * @return true if all paths are correct and not null, false otherwise
	 */
	public boolean isValid() {
		return repositoryFile != null && systemFile != null && usageModelFile != null && allocationModelFile != null
				&& resourceEnvironmentFile != null && repositoryFile.exists() && systemFile.exists()
				&& usageModelFile.exists() && allocationModelFile.exists() && resourceEnvironmentFile.exists();
	}

}