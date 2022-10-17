package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.vsum.VsumFacade;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

/**
 * Encapsulates all state of an ongoing integration
 * 
 * @author Lukas Burgey
 *
 */
public abstract class CommitIntegrationState {
    private final Logger LOGGER = Logger.getLogger("cipm.consistency.commitintegration.CommitIntegrationState");
    private CommitIntegration commitIntegration;
    private VsumFacade vsumFacade;
    private GitRepositoryWrapper gitRepositoryWrapper;
    private CommitChangePropagator commitChangePropagator;
    private LanguageFileSystemLayout fileSystemLayout;

    protected abstract GitRepositoryWrapper initializeGitRepositoryWrapper()
            throws IOException, InvalidRemoteException, TransportException, GitAPIException;

    protected VsumFacade initializeVsumFacade(Path vsumPath) throws IOException {
        var vsumFacade = new VsumFacade(vsumPath);
        vsumFacade.initialize();
        return vsumFacade;
    }

    protected CommitChangePropagator initializePropagator(CommitIntegration commitIntegration, VsumFacade vsumFacade,
            GitRepositoryWrapper gitRepositoryWrapper) throws IOException {
        return commitIntegration.getLanguageSpec()
            .getCommitChangePropagator(commitIntegration.getRootPath(), vsumFacade.getVsum(), gitRepositoryWrapper);
    }

    public void initialize(CommitIntegration commitIntegration)
            throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        LOGGER.info("Initializing the CommitIntegrationState");
        this.commitIntegration = commitIntegration;
        // the settings container needs to be initialized before everything else
        CommitIntegrationSettingsContainer.initialize(commitIntegration.getSettingsPath());
        gitRepositoryWrapper = initializeGitRepositoryWrapper();
        vsumFacade = initializeVsumFacade(commitIntegration.getVsumPath());
        commitChangePropagator = initializePropagator(commitIntegration, vsumFacade, gitRepositoryWrapper);
        fileSystemLayout = commitIntegration.getLanguageSpec()
            .getFileLayout(commitIntegration.getRootPath());
    }

    @SuppressWarnings("restriction")
    public void dispose() {
        LOGGER.info("Disposing of the CommitIntegrationState");
        vsumFacade.getVsum()
            .dispose();
        gitRepositoryWrapper.closeRepository();
    }

    /**
     * Copies the files of this integration to a target path
     * 
     * @param Suffix added to the root path of this integration
     * @throws IOException 
     */
    protected Path createFileSystemCopy(String nameSuffix) throws IOException {
        LOGGER.debug("Creating copy of CommitIntegrationState: " + nameSuffix);
        Path copyPath = commitIntegration.getRootPath().resolveSibling(commitIntegration.getRootPath().getFileName().toString() + "_" + nameSuffix);
        FileUtils.copyDirectory(commitIntegration.getRootPath().toFile(), copyPath.toFile());
        return copyPath;
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
        return fileSystemLayout;
    }
}
