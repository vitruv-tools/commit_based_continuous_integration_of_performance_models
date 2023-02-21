package cipm.consistency.vsum;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import cipm.consistency.models.ModelFacade;
import tools.vitruv.change.composite.description.PropagatedChange;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.framework.views.changederivation.StateBasedChangeResolutionStrategy;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

@SuppressWarnings("restriction")
public interface VsumFacade {

    void initialize(Path rootPath, List<ModelFacade> models, List<ChangePropagationSpecification> changeSpecs,
            StateBasedChangeResolutionStrategy stateBasedChangeResolutionStrategy) throws IOException;

    List<PropagatedChange> loadModels(List<ModelFacade> model);

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @return The propagated changes
     */
    Propagation propagateResource(Resource resource);

    /**
     * Propagate a resource into the underlying vsum
     * 
     * @param resource
     *            The propagated resource
     * @param targetUri
     *            The uri where vitruv persists the propagated resource
     * @return The propagated changes
     */
    Propagation propagateResource(Resource resource, URI targetUri);

    InternalVirtualModel getVsum();

    EditableCorrespondenceModelView<Correspondence> getCorrespondenceView();

    VsumDirLayout getDirLayout();
}