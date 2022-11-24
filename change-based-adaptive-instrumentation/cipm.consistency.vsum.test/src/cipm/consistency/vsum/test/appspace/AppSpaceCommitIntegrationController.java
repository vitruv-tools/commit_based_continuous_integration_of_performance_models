package cipm.consistency.vsum.test.appspace;

import cipm.consistency.commitintegration.CommitIntegration;
import cipm.consistency.commitintegration.CommitIntegrationController;
import cipm.consistency.commitintegration.git.GitRepositoryWrapper;
import cipm.consistency.commitintegration.lang.detection.strategy.ComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.lua.LuaModelFacade;
import cipm.consistency.commitintegration.lang.lua.appspace.AppSpaceComponentDetectionStrategy;
import cipm.consistency.commitintegration.lang.lua.appspace.StdLibCrownComponentDetectionStrategy;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.List;
import mir.reactions.luaPcm.LuaPcmChangePropagationSpecification;
import mir.reactions.pcmInit.PcmInitChangePropagationSpecification;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import tools.vitruv.change.propagation.ChangePropagationSpecification;

public abstract class AppSpaceCommitIntegrationController extends CommitIntegrationController<LuaModelFacade>
        implements CommitIntegration<LuaModelFacade> {

    @Override
    public Supplier<LuaModelFacade> getCodeModelFacadeSupplier() {
        return () -> {
            var model = new LuaModelFacade();
            model.setComponentDetectionStrategies(getComponentDetectionStrategies());
            return model;
        };
    }

    protected List<ComponentDetectionStrategy> getComponentDetectionStrategies() {
        return List.of(new AppSpaceComponentDetectionStrategy(), new StdLibCrownComponentDetectionStrategy());
    }

    @Override
    public List<ChangePropagationSpecification> getChangeSpecs() {
        return List.of(new LuaPcmChangePropagationSpecification(), new PcmInitChangePropagationSpecification());
    }

    /*
     * This returns an uninitialized git repo that is initialized by subclasses
     */
    @Override
    public GitRepositoryWrapper getGitRepositoryWrapper()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        return new GitRepositoryWrapper("lua", true);
    }
}
