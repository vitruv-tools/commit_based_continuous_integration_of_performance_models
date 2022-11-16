package cipm.consistency.models;

import java.nio.file.Path;
import org.eclipse.emf.ecore.resource.Resource;

public interface CodeModel extends Model {
    
    public Resource parseSourceCodeDir(Path dir);
}
