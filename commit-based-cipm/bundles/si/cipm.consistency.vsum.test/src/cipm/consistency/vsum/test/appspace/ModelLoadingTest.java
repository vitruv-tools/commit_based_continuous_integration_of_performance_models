package cipm.consistency.vsum.test.appspace;

import cipm.consistency.commitintegration.CommitIntegrationState;
import cipm.consistency.commitintegration.lang.lua.LuaModelFacade;
import java.io.IOException;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.junit.Test;

public class ModelLoadingTest extends AppSpaceCITest {
    @Test
    public void testModelLoading() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
        state = new CommitIntegrationState<LuaModelFacade>();
        // create state overwriting a possibly existing state
        state.initialize(this, true);
        state.dispose();

        // load the created state without overwriting
        state.initialize(this, false);
        state.dispose();
    }
}
