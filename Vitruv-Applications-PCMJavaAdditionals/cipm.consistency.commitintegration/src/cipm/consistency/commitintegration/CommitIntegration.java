package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import java.nio.file.Path;

public interface CommitIntegration {
    public Path getVsumPath();

    public Path getSettingsPath();

    public Path getRootPath();

    public LanguageSpecification getLanguageSpec();

    public GitRepositoryWrapper getGitRepositoryWrapper();
}
