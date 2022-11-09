package cipm.consistency.commitintegration.lang.lua.appspace;

import cipm.consistency.commitintegration.lang.detection.BuildFileBasedComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.detection.ModuleState;
import java.nio.file.Path;

public class AppSpaceComponentDetectionStrategy extends BuildFileBasedComponentDetectionStrategy {
	private static final String APP_MANIFEST_FILE_NAME = "project.mf.xml";

    @Override
    protected ModuleState checkDirectoryForComponent(Path parent) {
        if (checkSiblingExistence(parent, APP_MANIFEST_FILE_NAME)) {
            return ModuleState.REGULAR_COMPONENT;
        }
        return null;
    }
}
