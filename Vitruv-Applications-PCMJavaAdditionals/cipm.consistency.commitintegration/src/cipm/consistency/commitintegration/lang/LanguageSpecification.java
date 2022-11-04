package cipm.consistency.commitintegration.lang;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.detection.ComponentDetectionStrategy;
import cipm.consistency.vsum.VsumFacade;
import java.nio.file.Path;
import java.util.List;

public interface LanguageSpecification {
    public String getSourceSuffix();
    public LanguageFileSystemLayout getFileLayout(Path root);
    public CommitChangePropagator getCommitChangePropagator(Path root, VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper);
    public List<ComponentDetectionStrategy> getComponentDetectionStrategies();
}
