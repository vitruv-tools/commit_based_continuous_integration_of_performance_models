package cipm.consistency.commitintegration

import java.nio.file.Path
import org.eclipse.xtend.lib.annotations.Accessors

@Accessors
class JavaFileSystemLayout {
	static final String localJavaRepoDirName = "local-repo-clone";
	static final String javaModelFileContainerDirName = "vsum-variant";
	static final String javaModelFileName = "Java.javaxmi";
	static final String instrumentationDirName = "instrumented";
	static final String moduleConfigurationFileName = "module-configuration.properties";
	Path localJavaRepo;
	Path javaModelFileContainer;
	Path javaModelFile;
	Path instrumentationCopy;
	Path moduleConfiguration;
	
	new(Path parent) {
		localJavaRepo = parent.resolve(localJavaRepoDirName)
		javaModelFileContainer = parent.resolve(javaModelFileContainerDirName)
		javaModelFile = javaModelFileContainer.resolve(javaModelFileName)
		instrumentationCopy = parent.resolve(instrumentationDirName)
		moduleConfiguration = parent.resolve(moduleConfigurationFileName);
	}
}
