package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import java.nio.file.Path;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
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
    public CommitChangePropagator getCommitChangePropagator(Path root, InternalVirtualModel vsum,
            GitRepositoryWrapper repoWrapper) {
        // TODO Auto-generated method stub
        return new LuaCommitChangePropagator(vsum, repoWrapper, getFileLayout(root));
    }
}
