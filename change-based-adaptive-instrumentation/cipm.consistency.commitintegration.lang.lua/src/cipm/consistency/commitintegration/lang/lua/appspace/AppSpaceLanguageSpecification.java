package cipm.consistency.commitintegration.lang.lua.appspace;

import cipm.consistency.commitintegration.lang.detection.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.lua.LuaLanguageSpecification;
import java.util.List;

public class AppSpaceLanguageSpecification extends LuaLanguageSpecification {

    @Override
    public List<ComponentDetectionStrategy> getComponentDetectionStrategies() {
        return List.of(new AppSpaceComponentDetectionStrategy(),
                new StdLibCrownComponentDetectionStrategy());
    }

}
