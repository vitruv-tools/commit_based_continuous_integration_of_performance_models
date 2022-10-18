package cipm.consistency.commitintegration.lang.java;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import cipm.consistency.vsum.VsumFacade;
import java.nio.file.Path;

public class JavaLanguageSpecification implements LanguageSpecification  {

    @Override
    public String getSourceSuffix() {
        return "java";
    }

    @Override
    public LanguageFileSystemLayout getFileLayout(Path root) {
        return new JavaFileSystemLayout(root);
    }

    @Override
    public CommitChangePropagator getCommitChangePropagator(Path root, VsumFacade vsumFacade, GitRepositoryWrapper repoWrapper) {
        return new JavaCommitChangePropagator(vsumFacade, repoWrapper, getFileLayout(root));
    }
}
