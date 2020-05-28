package tools.vitruv.applications.pcmjava.integrationFromGit.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.merge.BatchMerger;
import org.eclipse.emf.compare.merge.IBatchMerger;
import org.eclipse.emf.compare.merge.IMerger;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.impl.CompilationUnitImpl;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.impl.RepositoryFactoryImpl;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitRepository;
import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaCorrespondenceModelTransformation;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilder;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.domains.pcm.PcmNamespace;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.userinteraction.InteractionResultProvider;
import tools.vitruv.framework.userinteraction.InternalUserInteractor;
import tools.vitruv.framework.userinteraction.UserInteractionFactory;
import tools.vitruv.framework.util.datatypes.ModelInstance;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.VirtualModelConfiguration;
import tools.vitruv.framework.vsum.VirtualModelImpl;
import tools.vitruv.framework.vsum.modelsynchronization.ChangePropagationListener;

public class ApplyingChangesTestUtil {

	public static IProject importAndCopyProjectIntoWorkspace(IWorkspace workspace, String projectName,
			String projectPath)
			throws IOException, URISyntaxException, InvocationTargetException, InterruptedException {
		IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
			public String queryOverwrite(String file) {
				return ALL;
			};
		};
		IPath workspacePath = workspace.getRoot().getFullPath().append("/" + projectName);

		ImportOperation importOperation = new ImportOperation(workspacePath, new File(projectPath/* baseDir */),
				FileSystemStructureProvider.INSTANCE, overwriteQuery);
		importOperation.setCreateContainerStructure(false);
		//DoneFlagProgressMonitor monitor = new DoneFlagProgressMonitor();//probably
		// importOperation does not work correctly with DoneFlagProgressMonitor, because progress.isDone() is never true.
		//Therefore use NullProgressMonitor()
		importOperation.run(/*new NullProgressMonitor()*/ /*monitor*/ null );
		//boolean importDone = monitor.isDone();
		return workspace.getRoot().getProject(projectName);
	}

	//WARNING: the project is probably overwritten, if a project with the same name is already in the workspace
	public static IProject createIProject(IWorkspace workspace, String pathToProject) throws CoreException {
		/*
		IPath projectDotProjectFile = new Path(pathToProject + "/.project");
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("TESTJDT");
		project.create(null);
		project.open(null);
		 
		//create the project
		IProjectDescription projectDescription = workspace.loadProjectDescription(projectDotProjectFile);
		project.setDescription(projectDescription, null);
		JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), null);
		return project;
		*/
		
		IPath projectDotProjectFile = new Path(pathToProject + "/.project");
		IProjectDescription projectDescription = workspace.loadProjectDescription(projectDotProjectFile);
		IProject project = workspace.getRoot().getProject(projectDescription.getName());
		JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), null);
		return project;
		

	}

	public static InternalVirtualModel integrateProjectWithChangePropagationSpecification(final IProject project,
			final ChangePropagationSpecification[] changePropagationSpecifications,
			final ChangePropagationListener listener) {
		// IPath projectPath = project.getFullPath(); // workspace relative Path
		final IPath projectPath = project.getLocation(); // absolute path
		final IPath models = projectPath.append("model").addTrailingSeparator().append("internal_architecture_model");

		final IPath scdmPath = models.addFileExtension("sourcecodedecorator");
		final IPath pcmPath = models.addFileExtension("repository");

		List<IPath> srcPaths = new ArrayList<IPath>();
		IJavaProject javaProject = JavaCore.create(project);
		try {
			IPackageFragmentRoot[] packageFragmentRoot = javaProject.getAllPackageFragmentRoots();
			for (int i = 0; i < packageFragmentRoot.length; i++) {
				if (packageFragmentRoot[i].getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT
						&& !packageFragmentRoot[i].isArchive()) {
					srcPaths.add(packageFragmentRoot[i].getPath());
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		List<IPath> jamoppPaths = new ArrayList<>();
		for (IPath path : srcPaths) {
			IPath projectRelative = path.removeFirstSegments(1);
			IPath abs = project.getLocation().append(projectRelative);
			jamoppPaths.add(abs);
		}

		final IPath projectBase = projectPath.removeLastSegments(1);

		final File f = scdmPath.toFile();
		if (!f.exists()) {
			throw new IllegalArgumentException("Run SoMoX first!");
		}

		List<VitruvDomain> metamodels = new ArrayList<VitruvDomain>();
		metamodels.add(new PcmDomainProvider().getDomain());
		metamodels.add(new JavaDomainProvider().getDomain());
		VirtualModelConfiguration config = new VirtualModelConfiguration();
		for (VitruvDomain metamodel : metamodels) {
			config.addMetamodel(metamodel);
		}

		for (int i = 0; i < changePropagationSpecifications.length; i++) {
			config.addChangePropagationSpecification(changePropagationSpecifications[i]);
		}

		InteractionResultProvider interactionResultProvider = UserInteractionFactory.instance
				.createDialogInteractionResultProvider();
		InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();// createUserInteractor(interactionResultProvider);

		/*
		 * PredefinedInteractionResultProvider interactionProvider =
		 * UserInteractionFactory.instance.createPredefinedInteractionResultProvider(
		 * null); this.testUserInteractor = new
		 * TestUserInteraction(interactionProvider); InternalUserInteractor
		 * userInteractor =
		 * UserInteractionFactory.instance.createUserInteractor(interactionProvider);
		 */

		File vsumFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "vitruvius.meta");
		final InternalVirtualModel vsum = new VirtualModelImpl(vsumFolder,
				userInteractor/* UserInteractionFactory.instance.createDialogUserInteractor() */, config);
		// one other method for creating vsum
		// TestUtil.createVirtualModel(vsumFile, false, metamodels,
		// Collections.singletonList(new Java2PcmChangePropagationSpecification()),
		// UserInteractionFactory.instance.createDialogUserInteractor());

		final PcmJavaCorrespondenceModelTransformation transformation = new PcmJavaCorrespondenceModelTransformation(
				scdmPath.toString(), pcmPath.toString(), jamoppPaths, vsum, projectBase);

		transformation.createCorrespondences();

		// Create correspondences for SEFF
		/*
		 * final PcmJavaCorrespondenceModelForSeffTransformation transformationForSeff =
		 * new PcmJavaCorrespondenceModelForSeffTransformation();
		 * transformationForSeff.afterBasicTransformations(transformation);
		 */

		// add change propagation listener to the project
		vsum.addChangePropagationListener(listener);
		

		// build project
		VitruviusJavaBuilderApplicator pcmJavaBuilder = new VitruviusJavaBuilderApplicator();
		pcmJavaBuilder.addToProject(project, vsumFolder,
				Collections.singletonList(PcmNamespace.REPOSITORY_FILE_EXTENSION));

		try {
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, VitruviusJavaBuilder.BUILDER_ID,
					new HashMap<String, String>(), new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// one other method for building a project
		// ProjectBuildUtils.issueIncrementalBuild(testProject,
		// VitruviusJavaBuilder.BUILDER_ID);

		return vsum;
	}

	public static GitRepository copyGitRepositoryIntoWorkspace(IWorkspace workspace, String originGitRepositoryPath) {

		File clonedGitRepository = (new File(workspace.getRoot().getLocation().toFile(), "clonedGitRepositories"));
		clonedGitRepository.mkdir();
		try {
			clonedGitRepository.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File originGitRepository = new File(originGitRepositoryPath);
		GitRepository gitRepository = null;

		try {
			gitRepository = new GitRepository(clonedGitRepository, originGitRepository.getAbsolutePath());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gitRepository;
	}
	
	
	public static boolean compareCompilationUnitsBuffers(ICompilationUnit firstCompilationUnit, ICompilationUnit secondCompilationUnit, boolean checkNameEquality) {
		if (checkNameEquality) {
			boolean sameName = firstCompilationUnit.getElementName().equals(secondCompilationUnit.getElementName());
			if (!sameName) return false;
		}
		
		boolean sameContent = false;
		
		try {
			sameContent = firstCompilationUnit.getBuffer().toString().equals(secondCompilationUnit.getBuffer().toString());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sameContent;
		
	}
	
/*	
	//TODO:clean up mess
	public static boolean compareJaMoPPClassifier(ICompilationUnit changedCompilationUnit, ICompilationUnit gitCompilationUnit, InternalVirtualModel virtualModel) {
		ModelInstance modelInstance = virtualModel.getModelInstance(VURI.getInstance(changedCompilationUnit.getResource()));
		modelInstance.load(new HashMap(), true);
		CompilationUnitImpl compilationUnitImplBefore = (CompilationUnitImpl) modelInstance.getResource().getContents().get(0);
        ConcreteClassifier changedClassifier = compilationUnitImplBefore.getClassifiers().get(0);
        
        
        virtualModel.executeCommand(new Callable<Void>() {

            @Override
            public Void call() {
                
                try {
                	System.out.println("changedClassifier:\n");
                	changedClassifier.eResource().save(System.out, new HashMap());
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });

        
        
		ConcreteClassifier gitClassifier = getJaMoPPClassifierForICompilationUnit(gitCompilationUnit);

        virtualModel.executeCommand(new Callable<Void>() {

            @Override
            public Void call() {
                
                try {
                	System.out.println("gitClassifier:\n");
                	gitClassifier.eResource().save(System.out, new HashMap());
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
		
		
		Comparison comparison = compareTwoModels(changedClassifier, gitClassifier);
		
		org.eclipse.emf.compare.internal.spec.AttributeChangeSpec s;
		
		List<Match> matches = comparison.getMatches();
		List<Diff> differences = comparison.getDifferences();
		
		if (differences.size() == 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
*/	
	
	public static boolean compareJaMoPPCompilationUnits(ICompilationUnit changedCompilationUnit, ICompilationUnit gitCompilationUnit, InternalVirtualModel virtualModel) {
		
		ModelInstance modelInstance = virtualModel.getModelInstance(VURI.getInstance(changedCompilationUnit.getResource()));
		modelInstance.load(new HashMap(), true);
		//CompilationUnitImpl changedCompilationUnit_JaMoPP = (CompilationUnitImpl) modelInstance.getResource().getContents().get(0);
		CompilationUnit changedCompilationUnit_JaMoPP = (CompilationUnit) modelInstance.getResource().getContents().get(0);

		OutputStream changedCompilationUnitStream = new ByteArrayOutputStream();
		OutputStream gitCompilationUnitStream = new ByteArrayOutputStream();
		

        virtualModel.executeCommand(new Callable<Void>() {

            @Override
            public Void call() {
                
                try {
                	System.out.println("changedCompilationUnit_JaMoPP:\n");
                	changedCompilationUnit_JaMoPP.eResource().save(changedCompilationUnitStream/*System.out*/, new HashMap());
                	System.out.println(changedCompilationUnitStream);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
		 
        
		CompilationUnit gitCompilationUnit_JaMoPP = getJaMoPPRootForVURI(VURI.getInstance(gitCompilationUnit.getResource()));


		virtualModel.executeCommand(new Callable<Void>() {

            @Override
            public Void call() {
                
                try {
                	System.out.println("gitClassifier:\n");
                	gitCompilationUnit_JaMoPP.eResource().save(gitCompilationUnitStream/*System.out*/, new HashMap());
                	System.out.println(gitCompilationUnitStream);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        });
		
        	
		Comparison comparison = compareTwoModels(changedCompilationUnit_JaMoPP, gitCompilationUnit_JaMoPP);
		
		List<Match> matches = comparison.getMatches();
		List<Diff> differences = comparison.getDifferences();
		
		if (differences.size() == 0) {
			return true;
		}
		else {
			System.out.println("JaMoPP-Models are NOT equal. Compare containing code from the models.");
			//virtualModel.propagateChangedState(gitCompilationUnit_JaMoPP.eResource());
			return changedCompilationUnitStream.toString().equals(gitCompilationUnitStream.toString());
		}

	}
	
	
	private void mergeModels(Comparison comparison) {
	    IMerger.Registry mergerRegistry = new IMerger.RegistryImpl().createStandaloneInstance();
	    IBatchMerger merger = new BatchMerger(mergerRegistry);
	    merger.copyAllLeftToRight(comparison.getDifferences(), new BasicMonitor());
	}
	
	

/*
public static void assertStreamEquals(Stream<?> s1, Stream<?> s2) {
    Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
    while(iter1.hasNext() && iter2.hasNext())
        assertEquals(iter1.next(), iter2.next());
    assert !iter1.hasNext() && !iter2.hasNext();
}
*/


	
	//TODO: Does not work appropriate, because it returns empty BasicComponents. Therefore find an another approach to create PCM Model
	public static boolean comparePCMBasicComponents(ICompilationUnit firstCompilationUnit, ICompilationUnit secondCompilationUnit) {
		ConcreteClassifier firstClassifier = getJaMoPPClassifierForICompilationUnit(firstCompilationUnit);
		ConcreteClassifier secondClassifier = getJaMoPPClassifierForICompilationUnit(secondCompilationUnit);
		
		BasicComponent firstBasicComponent = createPCMRepositoryForConcreteClassifier(firstClassifier);
		BasicComponent secondBasicComponent = createPCMRepositoryForConcreteClassifier(secondClassifier);
		
		return false;
	}
	
	public static Comparison compareTwoModels(EObject firstModel, EObject secondModel) {
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
    					return reference == EcorePackage.Literals.ENAMED_ELEMENT__NAME ||
    							super.isIgnoredReference(match, reference);
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
    	//IComparisonScope scope = EMFCompare.createDefaultScope(model1, model2);
    	IComparisonScope scope = new DefaultComparisonScope(firstModel, secondModel, null);
    	return comparator.compare(scope);
    }
	
	
	private static ConcreteClassifier getJaMoPPClassifierForICompilationUnit(ICompilationUnit compilationUnit) {
		return getJaMoPPClassifierForVURI(VURI.getInstance(compilationUnit.getResource()));
	}
	
	
	private static Method findJaMoPPMethodInICU(final ICompilationUnit icu, final String methodName) {
		final ConcreteClassifier cc = getJaMoPPClassifierForVURI(VURI.getInstance(icu.getResource()));
		final List<Member> jaMoPPMethods = cc.getMembersByName(methodName);
		for (final Member member : jaMoPPMethods) {
			if (member instanceof Method && member.getName().equals(methodName)) {
				return (Method) member;
			}
		}
		throw new RuntimeException("No method with name " + methodName + " found in " + icu);
	}
	
	private static ConcreteClassifier getJaMoPPClassifierForVURI(final VURI vuri) {
		final CompilationUnit cu = getJaMoPPRootForVURI(vuri);
		final Classifier jaMoPPClassifier = cu.getClassifiers().get(0);
		return (ConcreteClassifier) jaMoPPClassifier;
	}
	
	private static <T extends JavaRoot> T getJaMoPPRootForVURI(final VURI vuri) {
		final Resource resource = URIUtil.loadResourceAtURI(vuri.getEMFUri(), new ResourceSetImpl());
		// unchecked is OK for the test.
		@SuppressWarnings("unchecked")
		final T javaRoot = (T) resource.getContents().get(0);
		return javaRoot;
	}
	
	
	private static BasicComponent createPCMRepositoryForConcreteClassifier(ConcreteClassifier concreteClassifier) {
		
		RepositoryFactory repoFactory = RepositoryFactoryImpl.init();
		BasicComponent basicComponent = (BasicComponent) repoFactory.create(concreteClassifier.eClass());
		return basicComponent;
	}
	
	
	private ResourceDemandingSEFF createSEFFWithAbstractActions(final AbstractAction... abstractActions) {
		final ResourceDemandingSEFF expectedSeff = SeffFactory.eINSTANCE.createResourceDemandingSEFF();
		this.addActionsToBehaviour(expectedSeff, abstractActions);
		return expectedSeff;
	}

	
	private ResourceDemandingBehaviour createResourceBehaviourWithAbstractActions(
			final AbstractAction... abstractActions) {
		final ResourceDemandingBehaviour behaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
		this.addActionsToBehaviour(behaviour, abstractActions);
		return behaviour;
	}

	private void addActionsToBehaviour(final ResourceDemandingBehaviour behaviour,
			final AbstractAction... abstractActions) {
		behaviour.getSteps_Behaviour().add(SeffFactory.eINSTANCE.createStartAction());

		for (final AbstractAction abstractAction : abstractActions) {
			behaviour.getSteps_Behaviour().add(abstractAction);
		}

		behaviour.getSteps_Behaviour().add(SeffFactory.eINSTANCE.createStopAction());
	}
	
	
	/*
    public Comparison compareTwoModelsFromResourceSets(ResourceSet firstModelResourceSet, ResourceSet secondModelResourceSet) {

    	// Configure EMF Compare
    	IEObjectMatcher matcher = DefaultMatchEngine.createDefaultEObjectMatcher(UseIdentifiers.NEVER);
    	IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
    	IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(matcher, comparisonFactory);
            matchEngineFactory.setRanking(20);
            IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
            matchEngineRegistry.add(matchEngineFactory);
    	EMFCompare comparator = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry).build();

    	// Compare the two models
    	IComparisonScope scope = EMFCompare.createDefaultScope(firstModelResourceSet, secondModelResourceSet);
    	return comparator.compare(scope);
    }
	*/

}
