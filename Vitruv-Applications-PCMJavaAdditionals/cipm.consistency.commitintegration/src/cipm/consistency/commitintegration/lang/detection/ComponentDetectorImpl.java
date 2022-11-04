package cipm.consistency.commitintegration.lang.detection;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class ComponentDetectorImpl implements ComponentDetector {
    protected Set<ComponentDetectionStrategy> strategies = new HashSet<>();

    @Override
    public void addComponentDetectionStrategy(ComponentDetectionStrategy strategy) {
        strategies.add(strategy);
    }

    /**
     * Detects the components of a resourceset based on the detection strategies
     */
    @Override
    public ModuleCandidates detectComponents(ResourceSet resourceSet, Path projectRoot) {
        var candidates = new ModuleCandidates();

        for (var resource : resourceSet.getResources()) {
            for (var strategy : strategies) {
                strategy.detectComponent(resource, projectRoot, candidates);
            }
        }

        return candidates;
    }
}