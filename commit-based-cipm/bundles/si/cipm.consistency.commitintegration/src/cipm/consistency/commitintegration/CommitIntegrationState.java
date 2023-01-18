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
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

/**
 * Encapsulates all state of an ongoing integration
 * 
 * @param <CM> The code model class that is used for the integration
 * 
 * @author Lukas Burgey
 *
 */
public class CommitIntegrationState<CM extends CodeModelFacade> {
    private final Logger LOGGER = Logger.getLogger(CommitIntegrationState.class.getName());

    private String tag = "";

    private CommitIntegration<CM> commitIntegration;

    private CommitIntegrationDirLayout dirLayout;
    private GitRepositoryWrapper gitRepositoryWrapper;
    private VsumFacade vsumFacade;
    private PcmFacade pcmFacade;
    private ImFacade imFacade;
    private CM codeModelFacade;
    private CM instrumentedCodeModelFacade;

    // if this state was previously used to propagate something
    private boolean _isFresh = false;

    public CommitIntegrationState() {
        dirLayout = new CommitIntegrationDirLayout();
        vsumFacade = new VsumFacadeImpl();
        pcmFacade = new PcmFacade();
        imFacade = new ImFacade();

        // the codeModel is initialized in initialize()
    }

    public void initialize(CommitIntegration<CM> commitIntegration)
            throws InvalidRemoteException, TransportException, IOException, GitAPIException {
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
            _isFresh = true;
        }

        // the settings container needs to be initialized before everything else
        CommitIntegrationSettingsContainer.initialize(dirLayout.getSettingsFilePath());
        gitRepositoryWrapper = commitIntegration.getGitRepositoryWrapper();

        // initialize models
        pcmFacade.initialize(dirLayout.getPcmDirPath());
        imFacade.initialize(dirLayout.getImDirPath());

        // initialize the code model
        codeModelFacade = commitIntegration.getCodeModelFacadeSupplier()
            .get();
        codeModelFacade.initialize(dirLayout.getCodeDirPath());

        // initialize the code model
        instrumentedCodeModelFacade = commitIntegration.getCodeModelFacadeSupplier()
            .get();
        instrumentedCodeModelFacade.initialize(dirLayout.getInstrumentedCodeDirPath());

        // initialize the vsum
        vsumFacade.initialize(dirLayout.getVsumDirPath(), List.of(pcmFacade, imFacade),
                commitIntegration.getChangeSpecs(), commitIntegration.getStateBasedChangeResolutionStrategy());
    }

    @SuppressWarnings("restriction")
    public void dispose() {
        LOGGER.info("Disposing of the CommitIntegrationState");
        vsumFacade.getVsum()
            .dispose();
        gitRepositoryWrapper.closeRepository();
    }

    public Path createCopyWithTimeStamp() {
        return createCopyWithTimeStamp("");
    }

    public Path createCopyWithTimeStamp(String description) {
        var fileName = commitIntegration.getRootPath()
            .getFileName()
            .toString();

        if (tag != "") {
            fileName += "_" + tag;
        }

        fileName += "_" + LocalDateTime.now()
            .toString();

        if (!description.equals("")) {
            fileName += "_" + description;
        }

        try {
            return createCopy(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Copies the files of this integration to a target path
     * 
     * @param Suffix
     *            added to the root path of this integration
     * @throws IOException
     */
    private Path createCopy(String fileName) throws IOException {
        LOGGER.debug("Creating copy of CommitIntegrationState: " + fileName);

        Path copyPath = commitIntegration.getRootPath()
            .resolveSibling(fileName);

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

    public CM getInstrumentedCodeModelFacade() {
        return instrumentedCodeModelFacade;
    }

    public CommitIntegrationDirLayout getDirLayout() {
        return dirLayout;
    }

    public CommitIntegration<CM> getCommitIntegration() {
        return commitIntegration;
    }

    public boolean isFresh() {
        return _isFresh;
    }

    public void setNotFresh() {
        _isFresh = false;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
