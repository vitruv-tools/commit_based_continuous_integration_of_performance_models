package cipm.consistency.models;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.emf.ecore.resource.Resource;

public interface CodeModelFacade extends ModelFacade {

    public Resource parseSourceCodeDir(Path dir);
    
    public Path createNamedCopyOfParsedModel(String name) throws IOException;
}
