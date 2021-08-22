package cipm.consistency.commitintegration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

public class ExternalCommandExecutionUtility {
	private final static Logger logger = Logger.getLogger("cipm." + ExternalCommandExecutionUtility.class.getSimpleName());
	
	public static boolean runScript(File directory, String command) {
		int result = -1;
		logger.debug("Executing " + command);
		if (SystemUtils.IS_OS_WINDOWS) {
			result = internalRunScript(directory, "cmd.exe", "/c", "\"" + command + "\"");
		} else {
			result = internalRunScript(directory, command);
		}
		return result == 0;
	}
	
	private static int internalRunScript(File directory, String... command) {
		try {
			Process process = new ProcessBuilder().directory(directory)
					.inheritIO().command(command).start();
			return process.waitFor();
		} catch (IOException | InterruptedException e) {
			return -1;
		}
	}
}
