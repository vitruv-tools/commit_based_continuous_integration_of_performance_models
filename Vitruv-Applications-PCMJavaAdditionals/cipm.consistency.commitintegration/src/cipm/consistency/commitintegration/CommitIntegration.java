package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import java.nio.file.Path;

/**
 * Instance of this interface are used to initialize a {@link CommitIntegrationState}.
 * 
 * @author Lukas Burgey
 */
public interface CommitIntegration {
    /**
     * 
     * @return The root path of this commit integration. All other paths should be resolved to this path or subpaths
     */
    public Path getRootPath();

    /**
     * 
     * @return The path of the vsum
     */
    public Path getVsumPath();

    public Path getSettingsPath();

    /**
     * 
     * @return The language specification that is used for the commit integration
     */
    public LanguageSpecification getLanguageSpec();

    /**
     * 
     * @return The git repository wrapper instance that is used for the commit integration
     */
    public GitRepositoryWrapper getGitRepositoryWrapper();
}
