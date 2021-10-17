package cipm.consistency.commitintegration

import java.nio.file.Path
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * This class represents the layout on the file system related to the Java models.
 * 
 * @author Martin Armbruster
 */
@Accessors
class JavaFileSystemLayout {
	static final String localJavaRepoDirName = "local-repo-clone";
	static final String javaModelFileContainerDirName = "vsum-variant";
	static final String javaModelFileName = "Java.javaxmi";
	static final String instrumentationDirName = "instrumented";
	static final String moduleConfigurationFileName = "module-configuration.properties";
	static final String externalCallTargetPairsFileName = "external-call-target-pairs.json";
	Path localJavaRepo;
	Path javaModelFileContainer;
	Path javaModelFile;
	Path instrumentationCopy;
	Path moduleConfiguration;
	Path externalCallTargetPairsFile;
	
	new(Path parent) {
		localJavaRepo = parent.resolve(localJavaRepoDirName)
		javaModelFileContainer = parent.resolve(javaModelFileContainerDirName)
		javaModelFile = javaModelFileContainer.resolve(javaModelFileName)
		instrumentationCopy = parent.resolve(instrumentationDirName)
		moduleConfiguration = parent.resolve(moduleConfigurationFileName);
		externalCallTargetPairsFile = parent.resolve(externalCallTargetPairsFileName);
	}
}
