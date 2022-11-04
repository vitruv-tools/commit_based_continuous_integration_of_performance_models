package cipm.consistency.commitintegration.lang.java;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.detection.java.JavaComponentModuleDetector;
import cipm.consistency.vsum.VsumFacade;
import java.nio.file.Path;
import java.util.List;

public class JavaLanguageSpecification implements LanguageSpecification  {

    @Override
    public String getSourceSuffix() {
        return "java";
    }

    @Override
    public LanguageFileSystemLayout getFileLayout(Path root) {
        return new JavaFileSystemLayout(root);
    }

    @Override
    public CommitChangePropagator getCommitChangePropagator(Path root, VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper) {
        var componentDetector = new JavaComponentModuleDetector();
        for (var strategy : getComponentDetectionStrategies()) {
            componentDetector.addComponentDetectionStrategy(strategy);
        }
        return new JavaCommitChangePropagator(vsumFacade, repoWrapper, getFileLayout(root), componentDetector);
    }

    @Override
    public List<ComponentDetectionStrategy> getComponentDetectionStrategies() {
        // TODO Auto-generated method stub
        return null;
    }
}
