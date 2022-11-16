package cipm.consistency.commitintegration;

import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.models.CodeModel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

/**
 * Instances of this interface are used to initialize a {@link CommitIntegrationState}.
 * 
 * @author Lukas Burgey
 */
public interface CommitIntegration <CM extends CodeModel> {
    /**
     * 
     * @return The root path of this commit integration. All other paths should be resolved to this path or subpaths
     */
    public Path getRootPath();
    
    /**
     * 
     * @return All the change propagation specs that are used by this commit integration
     */
    public List<ChangePropagationSpecification> getChangeSpecs();

    // TODO we could remove this method from the interface
    public List<ComponentDetectionStrategy> getComponentDetectionStrategies();

    /**
     * 
     * @return The git repository wrapper instance that is used for the commit integration
     * @throws IOException 
     * @throws GitAPIException 
     * @throws TransportException 
     * @throws InvalidRemoteException 
     */
    public GitRepositoryWrapper getGitRepositoryWrapper() throws InvalidRemoteException, TransportException, GitAPIException, IOException;
    
    public CM createCodeModel();
}
