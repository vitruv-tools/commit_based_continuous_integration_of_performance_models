package cipm.consistency.commitintegration.lang.java;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.CommitChangePropagator;
import cipm.consistency.commitintegration.lang.LanguageSpecification;
import cipm.consistency.commitintegration.lang.LanguageFileSystemLayout;
import java.nio.file.Path;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
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
    public CommitChangePropagator getCommitChangePropagator(Path root, InternalVirtualModel vsum, GitRepositoryWrapper repoWrapper) {
        return new JavaCommitChangePropagator(vsum, repoWrapper, getFileLayout(root));
    }
}
