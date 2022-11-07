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
    ModuleCandidates detectModuleCandidates(ResourceSet resourceSet, Path projectRoot);
    

    /**
     * Like detectModuleCandidates but also resolves candidates to modules by using a configuration and user interaction 
     */
    public ModuleCandidates detectModules(ResourceSet resourceSet, Path projectRoot, Path configPath);
}