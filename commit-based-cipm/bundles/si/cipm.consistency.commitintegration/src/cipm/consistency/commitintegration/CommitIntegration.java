package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.models.CodeModelFacade;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;

/**
 * Instances of this interface are used to initialize a {@link CommitIntegrationState}.
 * 
 * @author Lukas Burgey
 */
public interface CommitIntegration<CM extends CodeModelFacade> {
    /**
     * 
     * @return The root path of this commit integration. All other paths should be resolved to this
     *         path or subpaths
     */
    public Path getRootPath();

    /**
     * 
     * @return All the change propagation specs that are used by this commit integration
     */
    public List<ChangePropagationSpecification> getChangeSpecs();

    public StateBasedChangeResolutionStrategy getStateBasedChangeResolutionStrategy();

    /**
     * 
     * @return The git repository wrapper instance that is used for the commit integration
     * @throws IOException
     * @throws GitAPIException
     * @throws TransportException
     * @throws InvalidRemoteException
     */
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException;

    /**
     * 
     * @return A supplier to instantiate the generic code model
     */
    public Supplier<CM> getCodeModelFacadeSupplier();
}
