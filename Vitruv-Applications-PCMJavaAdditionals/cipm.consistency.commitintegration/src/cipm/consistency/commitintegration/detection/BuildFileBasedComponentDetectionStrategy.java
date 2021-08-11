package cipm.consistency.commitintegration.detection;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

public class BuildFileBasedComponentDetectionStrategy implements ComponentDetectionStrategy {
	private static final String MAVEN_POM_FILE_NAME = "pom.xml";
	private static final String GRADLE_BUILD_FILE_NAME = "build.gradle";
	private static final String DOCKERFILE_FILE_NAME = "Dockerfile";
	
	@Override
	public void detectComponent(Resource res, Path file, Path container, ModuleCandidates candidate) {
		Path parent = file.getParent();
		while (container.compareTo(parent) != 0) {
			boolean buildFileExistence = checkSiblingExistence(parent, MAVEN_POM_FILE_NAME)
					|| checkSiblingExistence(parent, GRADLE_BUILD_FILE_NAME);
			boolean dockerFileExistence = checkSiblingExistence(parent, DOCKERFILE_FILE_NAME);
			if (buildFileExistence) {
				String modName = parent.getParent().getFileName().toString();
				if (dockerFileExistence) {
					candidate.addModuleClassifier(ModuleState.MICROSERVICE_COMPONENT, 
							modName, res);
				} else {
					candidate.addModuleClassifier(ModuleState.COMPONENT_CANDIDATE,
							modName, res);
				}
				return;
			}
			parent = parent.getParent();
		}
	}
	
	private boolean checkSiblingExistence(Path file, String siblingName) {
		Path sibling = file.resolveSibling(siblingName);
		return Files.exists(sibling);
	}
}
