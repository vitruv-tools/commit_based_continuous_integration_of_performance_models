package cipm.consistency.commitintegration.lang.lua;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
public class LuaCommitChangePropagator extends CommitChangePropagator {

    public LuaCommitChangePropagator(InternalVirtualModel vsum, GitRepositoryWrapper repoWrapper,
            LanguageFileSystemLayout fileLayout) {
        super(vsum, repoWrapper, fileLayout);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean propagateChanges(RevCommit start, RevCommit end) throws GitAPIException, IOException {
        // TODO Auto-generated method stub
        return false;
    }

}
