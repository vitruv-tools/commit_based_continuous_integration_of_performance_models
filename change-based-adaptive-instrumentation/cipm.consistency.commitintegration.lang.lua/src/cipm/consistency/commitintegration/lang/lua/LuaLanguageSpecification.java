package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import cipm.consistency.vsum.VsumFacade;
import java.nio.file.Path;

public class LuaLanguageSpecification implements LanguageSpecification {

    @Override
    public String getSourceSuffix() {
        return "lua";
    }

    @Override
    public LanguageFileSystemLayout getFileLayout(Path root) {
        // TODO Auto-generated method stub
        return new LuaLanguageFileSystemLayout(root.resolve("lua"));
    }

    @Override
    public CommitChangePropagator getCommitChangePropagator(Path root, VsumFacade vsumFacade,
            GitRepositoryWrapper repoWrapper) {
        // TODO Auto-generated method stub
        return new LuaCommitChangePropagator(vsumFacade, repoWrapper, getFileLayout(root));
    }
}
