package tools.vitruv.applications.pcmjava.reconstructionintegration.strategies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.pcm.repository.Repository;

import tools.vitruv.applications.pcmjava.reconstructionintegration.composite.CompositeTraversalStrategy;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.InvariantEnforcerFacadeBuilder;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.PcmRepositoryElementSelector;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.PcmRepositoryExtractor;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmtoJavaBeginChar;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaKeywords;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaSameIdentifier;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaSpecialChars;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaVitruviusKeywords;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaWhiteSpace;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.withocl.PJIE_ComponentInterfaceImplementsAmbiguity;
import tools.vitruv.applications.pcmjava.reconstructionintegration.repository.RepositoryTraversalStrategy;
import tools.vitruv.domains.pcm.util.RepositoryModelLoader;
import tools.vitruv.extensions.constructionsimulation.invariantcheckers.IMModelImplExtractor;
import tools.vitruv.extensions.constructionsimulation.invariantcheckers.InvariantEnforcer;
import tools.vitruv.extensions.constructionsimulation.traversal.ITraversalStrategy;
import tools.vitruv.extensions.constructionsimulation.util.ResourceHelper;
import tools.vitruv.framework.change.description.VitruviusChange;

/**
 * This integration strategy is used for PCM repository models. It checks only JaMoPP invariants as
 * "target" meta model.
 *
 * @author Johannes Hoor
 *
 */
public class PcmRepositoryIntegrationStrategy extends PcmIntegrationStrategy {

    /**
     * Instantiates a new PCM standard repository integration strategy.
     */
    public PcmRepositoryIntegrationStrategy() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see tools.vitruv.integration.strategies.IntegrationStategy#loadModel(java.lang.
     * String )
     */
    @Override
    protected Resource loadModel(final String path) {

        return RepositoryModelLoader.loadPcmResource(path);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * tools.vitruv.integration.strategies.IntegrationStategy#checkAndEnforceInvariants
     * (org.eclipse.emf.ecore.resource.Resource)
     */
    @Override
    protected Resource checkAndEnforceInvariants(Resource model) {

        final List<InvariantEnforcer> enforcers = new ArrayList<>();

        // enforcers.add(new PCMtoJaMoPPComponentInterfaceImplementsAmbiguity());

        enforcers.add(new PJIE_ComponentInterfaceImplementsAmbiguity());

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmRepositoryElementSelector(),
                new PcmToJavaKeywords()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmRepositoryElementSelector(),
                new PcmToJavaWhiteSpace()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmRepositoryElementSelector(),
                new PcmToJavaSpecialChars()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmRepositoryElementSelector(),
                new PcmToJavaVitruviusKeywords()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmRepositoryElementSelector(),
                new PcmtoJavaBeginChar()));

        enforcers.add(new PcmToJavaSameIdentifier());

        for (final InvariantEnforcer enf : enforcers) {
            model = enf.loadEnforceReturn(model);
        }

        return model;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * tools.vitruv.integration.strategies.IntegrationStategy#createChangeModels(org
     * .eclipse.core.resources.IResource, org.eclipse.emf.ecore.resource.Resource)
     */
    @Override
    protected EList<VitruviusChange> createChangeModels(final IResource originalResource, final Resource validModel) {

        // create correct URI for valid model
        final URI modelUri = ResourceHelper.createPlatformUriForResource(validModel);

        // traverse model and get ordered list of changes
        EList<VitruviusChange> changes = null;

        final IMModelImplExtractor<Repository> extractor = new PcmRepositoryExtractor();
        final Repository repository = extractor.getImpl(validModel);

        final ITraversalStrategy<Repository> repoTraversal = new RepositoryTraversalStrategy();
        final ITraversalStrategy<Repository> compTraversal = new CompositeTraversalStrategy();

        try {
            changes = repoTraversal.traverse(repository, modelUri, null);
            changes = compTraversal.traverse(repository, modelUri, changes);
        } catch (final UnsupportedOperationException e) {
            this.logger.error(e.getMessage());
        }
        return changes;

    }

}
