package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectorImpl;
import cipm.consistency.vsum.VsumFacade;
import java.nio.file.Path;
import java.util.List;

public abstract class LuaLanguageSpecification implements LanguageSpecification {

    @Override
    public String getSourceSuffix() {
        return "lua";
    }

    @Override
    public LanguageFileSystemLayout getFileLayout(Path root) {
        var luaDirPath = root.resolve("lua");
        return new LuaLanguageFileSystemLayout(luaDirPath);
    }

    @Override
    public CommitChangePropagator getCommitChangePropagator(Path root, VsumFacade vsumFacade,
            GitRepositoryWrapper repoWrapper) {
        return new LuaCommitChangePropagator(vsumFacade, repoWrapper, getFileLayout(root), createComponentDetector());
    }
    
    private ComponentDetector createComponentDetector() {
        var detector = new ComponentDetectorImpl();
        for (var strategy : getComponentDetectionStrategies()) {
            detector.addComponentDetectionStrategy(strategy);
        }
        return detector;
    }

    @Override
    public abstract List<ComponentDetectionStrategy> getComponentDetectionStrategies();
}
