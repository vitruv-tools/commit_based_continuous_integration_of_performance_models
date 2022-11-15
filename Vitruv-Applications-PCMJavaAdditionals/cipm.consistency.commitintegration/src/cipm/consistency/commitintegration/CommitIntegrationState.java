package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.models.CodeModel;
import cipm.consistency.models.im.IM;
import cipm.consistency.models.pcm.PCM;
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
public class CommitIntegrationState<CM extends CodeModel> {
    private final Logger LOGGER = Logger.getLogger(CommitIntegration.class.getName());

    private CommitIntegration<CM> commitIntegration;

    private CommitIntegrationDirLayout dirLayout;
    private GitRepositoryWrapper gitRepositoryWrapper;
    private VsumFacade vsum;
    private PCM pcm;
    private IM im;
    private CM codeModel;

    public CommitIntegrationState() {
        dirLayout = new CommitIntegrationDirLayout();
        vsum = new VsumFacadeImpl();
        pcm = new PCM();
        im = new IM();

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
        pcm.initialize(dirLayout.getPcmDirPath());
        im.initialize(dirLayout.getImDirPath());
        codeModel = commitIntegration.createCodeModel();
        codeModel.initialize(dirLayout.getCodeDirPath());
        
        // initialize the vsum
        vsum.initialize(dirLayout.getVsumDirPath(), List.of(pcm, im), commitIntegration.getChangeSpecs());
    }

    @SuppressWarnings("restriction")
    public void dispose() {
        LOGGER.info("Disposing of the CommitIntegrationState");
        vsum.getVsum()
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

    public VsumFacade getVsum() {
        return vsum;
    }

    public PCM getPcm() {
        return pcm;
    }

    public IM getIm() {
        return im;
    }

    public CM getCodeModel() {
        return codeModel;
    }

    public CommitIntegrationDirLayout getDirLayout() {
        return dirLayout;
    }
}
