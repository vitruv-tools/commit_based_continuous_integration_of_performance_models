package tools.vitruv.applications.pcmjava.integrationFromGit.propagation;

import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import tools.vitruv.framework.change.description.CompositeChange;
import tools.vitruv.framework.change.description.CompositeContainerChange;
import tools.vitruv.framework.change.description.TransactionalChange;
import tools.vitruv.framework.change.description.VitruviusChange;
import tools.vitruv.framework.change.description.VitruviusChangeFactory;
import tools.vitruv.framework.change.recording.AtomicEmfChangeRecorder;
import tools.vitruv.framework.domains.StateBasedChangeResolutionStrategy;
import tools.vitruv.framework.uuid.UuidGeneratorAndResolver;
import tools.vitruv.framework.uuid.UuidGeneratorAndResolverImpl;

/**
 * This default strategy for diff based state changes uses EMFCompare to resolve a
 * diff to a sequence of individual changes.
 * @author Timur Saglam
 * @author changed by Ilia Chupakhin
 */
@SuppressWarnings("all")
public class ResolutionStrategy implements StateBasedChangeResolutionStrategy {
  private final VitruviusChangeFactory changeFactory;
  
  /**
   * Creates the strategy.
   */
  public ResolutionStrategy() {
    this.changeFactory = VitruviusChangeFactory.getInstance();
  }
  
  @Override
  public CompositeChange<VitruviusChange> getChangeSequences(final Resource newState, final Resource currentState, final UuidGeneratorAndResolver resolver) {
    return this.resolveChangeSequences(newState, currentState, resolver);
  }
  
  @Override
  public CompositeChange<VitruviusChange> getChangeSequences(final EObject newState, final EObject currentState, final UuidGeneratorAndResolver resolver) {
    Resource _eResource = null;
    if (newState!=null) {
      _eResource=newState.eResource();
    }
    Resource _eResource_1 = null;
    if (currentState!=null) {
      _eResource_1=currentState.eResource();
    }
    return this.resolveChangeSequences(_eResource, _eResource_1, resolver);
  }
  
  private CompositeContainerChange resolveChangeSequences(final Resource newState, final Resource currentState, final UuidGeneratorAndResolver resolver) {
    if ((resolver == null)) {
      throw new IllegalArgumentException("UUID generator and resolver cannot be null!");
    } else {
      if (((newState == null) || (currentState == null))) {
        return this.changeFactory.createCompositeChange(Collections.<VitruviusChange>emptyList());
      }
    }
    ResourceSet _resourceSet = resolver.getResourceSet();
    final UuidGeneratorAndResolverImpl uuidGeneratorAndResolver = new UuidGeneratorAndResolverImpl(resolver, _resourceSet, true);
    final Resource currentStateCopy = this.copy(currentState);
    final List<Diff> diffs = this.compareStates(newState, currentStateCopy);
    final List<TransactionalChange> vitruvDiffs = this.replayChanges(diffs, currentStateCopy, uuidGeneratorAndResolver);
    return this.changeFactory.createCompositeChange(vitruvDiffs);
  }
  
  /**
   * Compares states using EMFCompare and returns a list of all differences.
   */
  private List<Diff> compareStates(final Notifier newState, final Notifier currentState) {
    DefaultComparisonScope scope = new DefaultComparisonScope(newState, currentState, null);
    Comparison comparison = EMFCompare.builder().build().compare(scope);
    return comparison.getDifferences();
  }
  
  /**
   * Replays a list of of EMFCompare differences and records the changes to receive Vitruv change sequences.
   */
  private List<TransactionalChange> replayChanges(final List<Diff> changesToReplay, final Notifier currentState, final UuidGeneratorAndResolver resolver) {
    final AtomicEmfChangeRecorder changeRecorder = new AtomicEmfChangeRecorder(resolver);
    changeRecorder.addToRecording(currentState);
    changeRecorder.beginRecording();
    final IMerger.Registry mergerRegistry = IMerger.RegistryImpl.createStandaloneInstance();
    final BatchMerger merger = new BatchMerger(mergerRegistry);
    BasicMonitor _basicMonitor = new BasicMonitor();
    merger.copyAllLeftToRight(changesToReplay, _basicMonitor);
    changeRecorder.endRecording();
    return changeRecorder.getChanges();
  }
  
  /**
   * Creates a new resource set, creates a resource and copies the content of the orignal resource.
   */
  private Resource copy(final Resource resource) {
    final ResourceSetImpl resourceSet = new ResourceSetImpl();
    final Resource copy = resourceSet.createResource(resource.getURI());
    copy.getContents().addAll(EcoreUtil.<EObject>copyAll(resource.getContents()));
    return copy;
  }
}

