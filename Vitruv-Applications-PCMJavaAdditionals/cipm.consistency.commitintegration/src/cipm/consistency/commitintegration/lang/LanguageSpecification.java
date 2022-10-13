package cipm.consistency.commitintegration.lang;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import java.nio.file.Path;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
public interface LanguageSpecification {
    public String getSourceSuffix();
    public LanguageFileSystemLayout getFileLayout(Path root);
    public CommitChangePropagator getCommitChangePropagator(Path root, InternalVirtualModel vsum, GitRepositoryWrapper repoWrapper);
}
