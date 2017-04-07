package tools.vitruv.applications.pcmjava.reconstructionintegration.strategies;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.pcm.system.System;

import tools.vitruv.applications.pcmjava.reconstructionintegration.composite.SystemTraversalStrategy;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.InvariantEnforcerFacadeBuilder;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.PcmSystemElementSelector;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.PcmSystemExtractor;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmtoJavaBeginChar;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaKeywords;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaSpecialChars;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaVitruviusKeywords;
import tools.vitruv.applications.pcmjava.reconstructionintegration.invariantcheckers.pcmjamoppenforcer.PcmToJavaWhiteSpace;
import tools.vitruv.domains.pcm.util.RepositoryModelLoader;
import tools.vitruv.extensions.constructionsimulation.invariantcheckers.IMModelImplExtractor;
import tools.vitruv.extensions.constructionsimulation.invariantcheckers.InvariantEnforcer;
import tools.vitruv.extensions.constructionsimulation.traversal.ITraversalStrategy;
import tools.vitruv.framework.change.description.VitruviusChange;

/**
 * This integration strategy is used for PCM system models. It checks only JaMoPP invariants as
 * "target" meta model.
 *
 * @author Sven Leonhardt
 */
public class PcmSystemIntegrationStrategy extends PcmIntegrationStrategy {

    /**
     * Instantiates a new PCM system integration strategy.
     */
    public PcmSystemIntegrationStrategy() {
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

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmSystemElementSelector(),
                new PcmToJavaKeywords()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmSystemElementSelector(),
                new PcmToJavaSpecialChars()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmSystemElementSelector(),
                new PcmToJavaVitruviusKeywords()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmSystemElementSelector(),
                new PcmToJavaWhiteSpace()));

        enforcers.add(InvariantEnforcerFacadeBuilder.buildInvariantEnforcerFacade(new PcmSystemElementSelector(),
                new PcmtoJavaBeginChar()));

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
    protected EList<VitruviusChange> createChangeModels(final IResource resource, final Resource validModel) {

        // create correct URI for valid model
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IPath workspaceDir = workspace.getRoot().getLocation();
        String workspaceString = workspaceDir.toString();
        workspaceString = workspaceString.replace("/", File.separator);

        final String platRelativeModelLoc = validModel.getURI().toFileString().replace(workspaceString, "");
        final URI modelUri = URI.createPlatformResourceURI(platRelativeModelLoc, true);

        // traverse model and get ordered list of changes
        EList<VitruviusChange> changes = null;

        final IMModelImplExtractor<?> extractor = new PcmSystemExtractor();
        final System system = (System) extractor.getImpl(validModel);

        final ITraversalStrategy<System> systemTraversal = new SystemTraversalStrategy();

        try {
            changes = systemTraversal.traverse(system, modelUri, null);
        } catch (final UnsupportedOperationException e) {
            this.logger.error(e.getMessage());
        }
        return changes;

    }

}
