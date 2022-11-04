package cipm.consistency.commitintegration.lang.detection;

import java.nio.file.Path;
import org.eclipse.emf.ecore.resource.ResourceSet;

public interface ComponentDetector {

    void addComponentDetectionStrategy(ComponentDetectionStrategy strategy);

    /**
     * Detects the components of a resourceset based on the detection strategies
     * 
     * @param resourceSet The resource set which is searched for components
     * @param projectRoot The root path of the project containing the resources
     * 
     * @return Candidates for modules
     */
    ModuleCandidates detectComponents(ResourceSet resourceSet, Path projectRoot);

}