package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.models.CodeModelFacade;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.vsum.VsumFacade;
import cipm.consistency.vsum.VsumFacadeImpl;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
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
public class CommitIntegrationState<CM extends CodeModelFacade> {
    private final Logger LOGGER = Logger.getLogger(CommitIntegration.class.getName());

    private CommitIntegration<CM> commitIntegration;

    private CommitIntegrationDirLayout dirLayout;
    private GitRepositoryWrapper gitRepositoryWrapper;
    private VsumFacade vsumFacade;
    private PcmFacade pcmFacade;
    private ImFacade imFacade;
    private CM codeModelFacade;

    public CommitIntegrationState() {
        dirLayout = new CommitIntegrationDirLayout();
        vsumFacade = new VsumFacadeImpl();
        pcmFacade = new PcmFacade();
        imFacade = new ImFacade();

        // the codeModel is initialized in initialize()
    }

    public void initialize(CommitIntegration<CM> commitIntegration) throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        initialize(commitIntegration, false);
    }

    public void initialize(CommitIntegration<CM> commitIntegration, boolean overwrite)
            throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        LOGGER.info("Initializing the CommitIntegrationState");
        this.commitIntegration = commitIntegration;
        dirLayout.initialize(commitIntegration.getRootPath());
        
        // delete the directory layout if we are overwriting
        if (overwrite) {
            LOGGER.info(String.format("Deleting CommitIntegrationState at %s", commitIntegration.getRootPath()));
            dirLayout.delete();
            dirLayout.initialize(commitIntegration.getRootPath());
        }

        // the settings container needs to be initialized before everything else
        CommitIntegrationSettingsContainer.initialize(dirLayout.getSettingsFilePath());
        gitRepositoryWrapper = commitIntegration.getGitRepositoryWrapper();

        // initialize models
        pcmFacade.initialize(dirLayout.getPcmDirPath());
        imFacade.initialize(dirLayout.getImDirPath());
        
        codeModelFacade = commitIntegration.getCodeModelFacadeSupplier().get();
        codeModelFacade.initialize(dirLayout.getCodeDirPath());
        
        // initialize the vsum
        vsumFacade.initialize(dirLayout.getVsumDirPath(), List.of(pcmFacade, imFacade), commitIntegration.getChangeSpecs());
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
     * @param Suffix
     *            added to the root path of this integration
     * @throws IOException
     */
    public Path createFileSystemCopy(String nameSuffix) throws IOException {
        LOGGER.debug("Creating copy of CommitIntegrationState: " + nameSuffix);
        Path copyPath = commitIntegration.getRootPath()
            .resolveSibling(commitIntegration.getRootPath()
                .getFileName()
                .toString() + "_" + nameSuffix);
        FileUtils.copyDirectory(commitIntegration.getRootPath()
            .toFile(), copyPath.toFile());
        return copyPath;
    }

    public GitRepositoryWrapper getGitRepositoryWrapper() {
        return gitRepositoryWrapper;
    }

    public VsumFacade getVsumFacade() {
        return vsumFacade;
    }

    public PcmFacade getPcmFacade() {
        return pcmFacade;
    }

    public ImFacade getImFacade() {
        return imFacade;
    }

    public CM getCodeModelFacade() {
        return codeModelFacade;
    }

    public CommitIntegrationDirLayout getDirLayout() {
        return dirLayout;
    }
}
