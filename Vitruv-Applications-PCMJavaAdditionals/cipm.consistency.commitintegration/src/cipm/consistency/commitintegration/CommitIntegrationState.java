package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.vsum.VsumFacade;
import java.io.IOException;

/**
 * Encapsulates all state of an ongoing integration
 * 
 * @author Lukas Burgey
 *
 */
public class CommitIntegrationState {
    private CommitIntegration commitIntegration;
    private VsumFacade vsumFacade;
    private GitRepositoryWrapper gitRepositoryWrapper;
    private CommitChangePropagator commitChangePropagator;
    private LanguageFileSystemLayout fileSystemLayout;
    
    public CommitIntegrationState(CommitIntegration commitIntegration) {
        this.commitIntegration = commitIntegration;
        vsumFacade = new VsumFacade(commitIntegration.getVsumPath());
    }
    
    public void initialize() throws IOException {
        vsumFacade.initialize();
        commitChangePropagator = commitIntegration.getLanguageSpec()
            .getCommitChangePropagator(commitIntegration.getRootPath(), getVsumFacade().getVsum(), commitIntegration.getRepoWrapper());
        fileSystemLayout = commitIntegration.getLanguageSpec().getFileLayout(commitIntegration.getRootPath());
    }
    
    @SuppressWarnings("restriction")
    public void dispose() {
        vsumFacade.getVsum().dispose();
        commitChangePropagator.shutdown();
    }

    public VsumFacade getVsumFacade() {
        return vsumFacade;
    }

    public CommitChangePropagator getCommitChangePropagator() {
        return commitChangePropagator;
    }

    public GitRepositoryWrapper getGitRepositoryWrapper() {
        return gitRepositoryWrapper;
    }
    
    public LanguageFileSystemLayout getFileSystemLayout() {
        return this.fileSystemLayout;
    }
}
