package cipm.consistency.vsum.test.ci;

import cipm.consistency.commitintegration.lang.LanguageSpecification;
import cipm.consistency.commitintegration.lang.lua.appspace.AppSpaceLanguageSpecification;

public abstract class AppSpaceCITest extends AbstractCITest {

    @Override
    public LanguageSpecification getLanguageSpec() {
        return new AppSpaceLanguageSpecification();
    }

    protected String getLatestCommitId() {
        return getGitRepositoryWrapper().getLatestCommit()
            .getName();
    }
}
