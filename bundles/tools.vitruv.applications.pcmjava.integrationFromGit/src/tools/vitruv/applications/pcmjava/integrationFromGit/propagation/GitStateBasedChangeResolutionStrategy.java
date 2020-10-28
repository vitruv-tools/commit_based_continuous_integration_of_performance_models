package tools.vitruv.applications.pcmjava.integrationFromGit.propagation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.compare.impl.AttributeChangeImpl;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
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
public class GitStateBasedChangeResolutionStrategy implements StateBasedChangeResolutionStrategy {
  private final VitruviusChangeFactory changeFactory;
  
  /**
   * Creates the strategy.
   */
  public GitStateBasedChangeResolutionStrategy() {
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
    
    
    List<Diff> diffs = this.compareStates(newState, currentStateCopy/*currentState*/);
    //List<Diff> diffs = compareTwoModels(newState, currentStateCopy).getDifferences();
    
    //Get rid of some types of Diffs. For example, layout changes 
    diffs = filterDiffs(diffs);
    
    final List<TransactionalChange> vitruvDiffs = this.replayChanges(diffs, /*currentState*/currentStateCopy, uuidGeneratorAndResolver);
    
    return this.changeFactory.createCompositeChange(vitruvDiffs);
  }
  
  private List<Diff> filterDiffs(List<Diff> diffs) {
	  List<Diff> filteredDiffs = new ArrayList<Diff>();
	  for (Diff d : diffs) {
		if (!(d instanceof AttributeChangeImpl && ((AttributeChangeImpl) d).getAttribute().getContainerClass().getName().equals("org.emftext.commons.layout.LayoutInformation"))) {
			filteredDiffs.add(d);
		}
	}
	return filteredDiffs;
  }
  
  
  /**
	 * Compares two JaMoPP Models <code>firstModel</code> and <code>secondModel</code> using {@link EMFCompare}
	 *  @see <a href="https://www.eclipse.org/emf/compare/documentation/latest/developer/developer-guide.html">http://eclipse.org</a>
	 * 
	 * @param firstModel first JaMoPP Model
	 * @param secondModel second JaMoPP Model
	 * @return comparison result
	 */
	public static Comparison compareTwoModels(Resource firstModel, Resource secondModel) {	
		// Configure EMF Compare
  	IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.NEVER);
  	IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
  	IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
          matchEngineFactory.setRanking(20);
          IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
          matchEngineRegistry.add(matchEngineFactory);
  	//The logic to determine whether a feature should be checked for differences has been extracted into its own class, and is quite easy to alter. 
      //For example, if you would like to ignore the name feature of your elements or never detect any ordering change: 
  	IDiffProcessor diffProcessor = new DiffBuilder();
  	IDiffEngine diffEngine = new DefaultDiffEngine(diffProcessor) {
  		@Override
  		protected FeatureFilter createFeatureFilter() {
  			return new FeatureFilter() {
  				@Override
  				protected boolean isIgnoredReference(Match match, EReference reference) {
  					return reference.getName().equalsIgnoreCase("layoutInformations") ||
  							super.isIgnoredReference(match, reference);
  					/*reference == EcorePackage.Literals.ENAMED_ELEMENT__NAME ||*/
  				}

  				@Override
  				public boolean checkForOrderingChanges(EStructuralFeature feature) {
  					return false;
  				}
  			};
  		}
  	};
  	
  	EMFCompare comparator = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry).setDiffEngine(diffEngine).build();
  	// Compare the two models
  	IComparisonScope scope = new DefaultComparisonScope(firstModel, secondModel, null);
  	return comparator.compare(scope);
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
   /* 
    virtualModel.executeCommand(new Callable<Void>() {

        @Override
        public Void call() {
            try {
            	merger.copyAllLeftToRight(changesToReplay, _basicMonitor);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    });
    */
    
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

