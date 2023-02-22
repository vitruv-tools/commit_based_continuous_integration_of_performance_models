package cipm.consistency.commitintegration;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.models.CodeModelFacade;
import cipm.consistency.models.im.ImFacade;
import cipm.consistency.models.pcm.PcmFacade;
import cipm.consistency.vsum.VsumFacade;
import cipm.consistency.vsum.VsumFacadeImpl;

/**
 * Encapsulates all state of an ongoing integration
 * 
 * @param <CM>
 *            The code model class that is used for the integration
 * 
 * @author Lukas Burgey
 *
 */
public class CommitIntegrationState<CM extends CodeModelFacade> {
    private static final Logger LOGGER = Logger.getLogger(CommitIntegrationState.class.getName());

    private CommitIntegration<CM> commitIntegration;

    private CommitIntegrationDirLayout dirLayout;
    private GitRepositoryWrapper gitRepositoryWrapper;
    private VsumFacade vsumFacade;
    private PcmFacade pcmFacade;
    private ImFacade imFacade;
    private CM codeModelFacade;

    private String tag = "";
    private int snapshotCount = 0;
    private int parsedCodeModelCount = 0;
    private int vsumCodeModelCount = 0;
    
    private Path currentParsedModelPath = null;

    // was this state previously used to propagate something?
    private boolean isFresh = false;

    private int repositoryModelCount;

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
            isFresh = true;
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

        // initialize the vsum
        vsumFacade.initialize(dirLayout.getVsumDirPath(), List.of(pcmFacade, imFacade),
                commitIntegration.getChangeSpecs(), commitIntegration.getStateBasedChangeResolutionStrategy());
    }

    @SuppressWarnings("restriction")
    public void dispose() {
        LOGGER.debug("Disposing of the CommitIntegrationState");
        vsumFacade.getVsum()
            .dispose();
        gitRepositoryWrapper.closeRepository();
    }

    public Path createParsedCodeModelSnapshot() {
        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
        parsedCodeModelCount += 1;
        var name = String.valueOf(parsedCodeModelCount) + "-" + currentCommitHash;
        try {
            return getCodeModelFacade().createNamedCopyOfParsedModel(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Path createVsumCodeModelSnapshot() {
        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
        vsumCodeModelCount += 1;
        var name = "vsum-" + String.valueOf(vsumCodeModelCount) + "-" + currentCommitHash + ".code.xmi";
        var path = dirLayout.getVsumCodeModelPath();
        var targetPath = path.resolveSibling(name);
        try {
            FileUtils.copyFile(path.toFile(), targetPath.toFile());
            return targetPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Path createRepositoryModelSnapshot() {
        var currentCommitHash = getGitRepositoryWrapper().getCurrentCommitHash();
        repositoryModelCount += 1;
        var name = "Repository-" + String.valueOf(vsumCodeModelCount) + "-" + currentCommitHash + ".repository";
        var path = pcmFacade.getDirLayout().getPcmRepositoryPath();
        var targetPath = path.resolveSibling(name);
        try {
            FileUtils.copyFile(path.toFile(), targetPath.toFile());
            return targetPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Path createSnapshotWithCount() {
        return createSnapshotWithCount("");
    }

    public Path createSnapshotWithCount(String additionalIdentifier) {
        snapshotCount += 1;
        var identifier = String.valueOf(snapshotCount);
        if (!additionalIdentifier.equals("")) {
            identifier += "_" + additionalIdentifier;
        }
        return createSnapshot(identifier);
    }

    public Path createSnapshotWithTimeStamp() {
        return createSnapshotWithTimeStamp("");
    }

    public Path createSnapshotWithTimeStamp(String additionalIdentifier) {
        var identifier = "_" + LocalDateTime.now()
            .toString();

        if (!additionalIdentifier.equals("")) {
            identifier += "_" + additionalIdentifier;
        }
        return createSnapshot(identifier);
    }

    private Path createSnapshot(String identifier) {
        var dirName = commitIntegration.getRootPath()
            .getFileName()
            .toString();

        if (!identifier.equals("")) {
            dirName += "_" + identifier;
        }
        if (tag != "") {
            dirName += "_" + tag;
        }

        try {
            return createCopy(dirName);
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

    public CommitIntegrationDirLayout getDirLayout() {
        return dirLayout;
    }

    public CommitIntegration<CM> getCommitIntegration() {
        return commitIntegration;
    }

    public boolean isFresh() {
        return isFresh;
    }

    public void setNotFresh() {
        isFresh = false;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Path getCurrentParsedModelPath() {
        return currentParsedModelPath;
    }

    public void setCurrentParsedModelPath(Path currentParsedModelPath) {
        this.currentParsedModelPath = currentParsedModelPath;
    }

    public int getParsedCodeModelCount() {
        return parsedCodeModelCount;
    }
}
