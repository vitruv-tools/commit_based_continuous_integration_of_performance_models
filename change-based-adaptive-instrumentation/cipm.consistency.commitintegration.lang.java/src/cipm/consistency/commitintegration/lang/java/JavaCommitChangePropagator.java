package cipm.consistency.commitintegration.lang.java;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.lang.detection.ComponentDetector;
import cipm.consistency.commitintegration.settings.CommitIntegrationSettingsContainer;
import cipm.consistency.commitintegration.settings.SettingKeys;
import cipm.consistency.commitintegration.util.ExternalCommandExecutionUtils;
import cipm.consistency.vsum.VsumFacade;
import java.io.File;
import java.util.List;
import tools.vitruv.change.composite.description.PropagatedChange;

/**
 * This class propagates changes from Git commits to JaMoPP models within Vitruv by directly
 * propagating the changes of a commit within a VSUM using the state-based change propagation.
 * 
 * @author Martin Armbruster
 * @author Ilia Chupakhin
 */
public class JavaCommitChangePropagator extends CommitChangePropagator {
    public JavaCommitChangePropagator(VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper,
            LanguageFileSystemLayout fileLayout, ComponentDetector componentDetector) {
        super(vsumFacade, repoWrapper, fileLayout, componentDetector);
    }

    @Override
    protected boolean preprocessCheckout() {
        File possibleFile = new File(CommitIntegrationSettingsContainer.getSettingsContainer()
            .getProperty(SettingKeys.PATH_TO_PREPROCESSING_SCRIPT));
        String absPath = possibleFile.getAbsolutePath();
        if (possibleFile.exists()) {
            return ExternalCommandExecutionUtils.runScript(repoWrapper.getWorkTree(), absPath);
        } else {
            LOGGER.debug(absPath + " not found.");
        }
        return false;
    }

    public List<PropagatedChange> propagateCurrentCheckout() {
        LOGGER.debug("Delegating the change propagation to the JavaParserAndPropagatorUtility.");
        JavaParserAndPropagatorUtils.parseAndPropagateJavaCode(repoWrapper.getWorkTree()
            .toPath(), fileLayout.getModelFile(), vsumFacade.getVsum(), fileLayout.getModuleConfiguration());

        // TODO The Jamopp stuff needs to be rewritten (it needs to return List<PropagatedChange>)
        return null;
    }
}
