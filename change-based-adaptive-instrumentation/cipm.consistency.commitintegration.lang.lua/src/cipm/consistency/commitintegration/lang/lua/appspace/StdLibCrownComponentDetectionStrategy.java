package cipm.consistency.commitintegration.lang.lua.appspace;

import cipm.consistency.commitintegration.lang.detection.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.detection.ModuleCandidates;
import cipm.consistency.commitintegration.lang.detection.ModuleState;
import java.nio.file.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.xtext.lua.scoping.LuaLinkingService;

public class StdLibCrownComponentDetectionStrategy implements ComponentDetectionStrategy {

    @Override
    public void detectComponent(Resource res, Path projectRoot, ModuleCandidates candidates) {
        if (res.getURI().equals(LuaLinkingService.MOCK_URI)) {
            candidates.addModuleClassifier(ModuleState.REGULAR_COMPONENT, LuaLinkingService.MOCK_URI.path(), res);
        }
    }

}
