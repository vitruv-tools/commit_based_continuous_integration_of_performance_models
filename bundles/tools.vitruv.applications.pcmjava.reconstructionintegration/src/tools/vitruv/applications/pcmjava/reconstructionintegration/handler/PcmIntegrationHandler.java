package tools.vitruv.applications.pcmjava.reconstructionintegration.handler;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;

import edu.kit.ipd.sdq.commons.util.org.eclipse.core.resources.IResourceUtil;
import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.PcmRepositoryExtractor;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.PcmSystemExtractor;
import tools.vitruv.applications.pcmjava.reconstructionintegration.strategies.PcmRepositoryIntegrationStrategy;
import tools.vitruv.applications.pcmjava.reconstructionintegration.strategies.PcmSystemIntegrationStrategy;
import tools.vitruv.applications.pcmjava.reconstructionintegration.util.PcmMetaModelConverter;
import tools.vitruv.applications.pcmjava.util.PcmJavaRepositoryCreationUtil;
import tools.vitruv.domains.pcm.util.RepositoryModelLoader;
import tools.vitruv.extensions.constructionsimulation.handler.IntegrationHandler;
import tools.vitruv.extensions.constructionsimulation.invariantcheckers.IMModelImplExtractor;
import tools.vitruv.extensions.constructionsimulation.traversal.util.UnorderedReferencesRespectingEqualityHelper;
import tools.vitruv.extensions.constructionsimulation.util.IntegrationUtil;
import tools.vitruv.extensions.constructionsimulation.util.ResourceHelper;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.util.datatypes.ModelInstance;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModel;

public class PcmIntegrationHandler extends IntegrationHandler<IFile> {

    public PcmIntegrationHandler() {
        super(IFile.class);
    }

    private static final Logger logger = Logger.getLogger(PcmIntegrationHandler.class.getSimpleName());

    /**
     * Integrates a PCM respository model into Vitruvius.
     *
     * @param resource
     *            : the model which the user selected in eclipse
     * @param changeSynchronizing
     *            : the synchronization provider used for synchronizing the generated changes
     */
    private void integratePCMRepository(IResource resource, VirtualModel vmodel) {

        Logger.getRootLogger().setLevel(Level.ALL);

        // get URI from resource
        final URI uri = IResourceUtil.getEMFPlatformURI(resource);

        // update the PCM parameter definitions
        resource = this.updateParameterDef(resource, uri);

        if (this.keepOldModel) {
            this.saveOldModel(resource);
        }

        final PcmRepositoryIntegrationStrategy integrator = new PcmRepositoryIntegrationStrategy();

        if (vmodel == null) {
        	// TODO No change2command transformers are added here
        	final Iterable<VitruvDomain> metamodels = PcmJavaRepositoryCreationUtil.createPcmJamoppMetamodels();
            vmodel = IntegrationUtil.createVsum(metamodels);
        }

        try {
            integrator.integrateModel(resource, vmodel);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (this.keepOldModel) {
            this.compareWithNewModel(resource, uri);
        }

    }

    /**
     * Integrates a PCM System model into Vitruvius.
     *
     * @param resource
     *            : the model which the user selected in eclipse
     */
    private void integratePCMSystem(final IResource resource) {

        Logger.getRootLogger().setLevel(Level.ALL);

        if (this.keepOldModel) {
            this.saveOldModel(resource);
        }

        // create underlying elements (MetaRepo, VSUM,...)
        final Iterable<VitruvDomain> metamodels = PcmJavaRepositoryCreationUtil.createPcmJamoppMetamodels();
        final InternalVirtualModel vmodel = IntegrationUtil.createVsum(metamodels);

        // find all referenced repositories and integrate them first
        final EList<Repository> linkedRepositories = this.extractRepositories(resource);

        for (final Repository repository : linkedRepositories) {

            // convert EMF resource -> Eclipse IResource
            final IResource repositoryResource = ResourceHelper
                    .absoluteEmfResourceToWorkspaceRelativeIResource(repository.eResource());

            this.integratePCMRepository(repositoryResource, vmodel);
        }

        final PcmSystemIntegrationStrategy integrator = new PcmSystemIntegrationStrategy();

        try {
            integrator.integrateModel(resource, vmodel);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Extracts all repositories referenced by a system model.
     *
     * @param resource
     *            : PCM System
     * @return : List of referenced repositories
     */
    private EList<Repository> extractRepositories(final IResource resource) {

        final IMModelImplExtractor<org.palladiosimulator.pcm.system.System> extractor = new PcmSystemExtractor();
        final Resource model = RepositoryModelLoader.loadPcmResource(resource.getLocation().toString());

        final org.palladiosimulator.pcm.system.System system = extractor.getImpl(model);

        final EList<Repository> linkedRepositories = new UniqueEList<Repository>();
        final EList<AssemblyContext> contexts = system.getAssemblyContexts__ComposedStructure();

        for (final AssemblyContext context : contexts) {

            final RepositoryComponent component = context.getEncapsulatedComponent__AssemblyContext();
            final Repository repository = component.getRepository__RepositoryComponent();

            linkedRepositories.add(repository);
        }
        return linkedRepositories;
    }

    /**
     * Updates PCM parameter definitions.
     *
     * @param resource
     *            : PCM repository
     * @param uri
     *            : PCM repository uri
     * @return : IResource to file with new parameter definitions
     */
    protected IResource updateParameterDef(IResource resource, URI uri) {
        // 1. Update model to current PCM version
        final File updatedModel = PcmMetaModelConverter.convertPCM(resource.getLocation(), false);

        // 2. update resource if model got updated
        if (PcmMetaModelConverter.isModelUpdated()) {

            // create new uri
            uri = uri.trimSegments(1);
            final String fileName = updatedModel.getName();
            uri = uri.appendSegment(fileName);

            // set resource to new file
            final IProject project = resource.getProject();
            IPath iPathForEMFUri = URIUtil.getIPathForEMFUri(uri);

            // remove project folder from path
            iPathForEMFUri = iPathForEMFUri.removeFirstSegments(1);
            final IFile file = project.getFile(iPathForEMFUri);
            resource = file;
        }
        return resource;
    }

    protected void compareWithNewModel(final IResource resource, final URI uri) {
        // get integrated pcm model instance
        final ModelInstance integratedModelInstance = this.vsum.getModelInstance(VURI.getInstance(uri));
        final IPath oldModelPath = this.createPathToOldModel(resource.getLocation());

        // load copy of the model before integration
        final Resource originalResource = RepositoryModelLoader.loadPcmResource(oldModelPath.toString());

        // load the two root objects from the model
        final PcmRepositoryExtractor pre = new PcmRepositoryExtractor();
        final Repository integratedRepo = pre.getImpl(integratedModelInstance.getResource());
        final Repository originalRepo = pre.getImpl(originalResource);

        // compare
        final UnorderedReferencesRespectingEqualityHelper comparator = new UnorderedReferencesRespectingEqualityHelper();

        logger.info(
                "Original Model and Integrated Model identical? " + comparator.equals(originalRepo, integratedRepo));
    }

    @Override
    protected void handleSelectedElement(final IFile firstElement) {
        if (firstElement.getFileExtension().equals("repository")) {
            this.integratePCMRepository(firstElement, null);
        }
        if (firstElement.getFileExtension().equals("system")) {
            this.integratePCMSystem(firstElement);
        }
    }

}
