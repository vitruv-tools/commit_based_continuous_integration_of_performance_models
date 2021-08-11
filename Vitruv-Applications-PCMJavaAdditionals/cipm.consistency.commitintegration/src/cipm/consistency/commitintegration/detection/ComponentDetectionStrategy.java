package cipm.consistency.commitintegration.detection;

import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

public interface ComponentDetectionStrategy {
	public void detectComponent(Resource res, Path file, Path container, ModuleCandidates candidate);
}
