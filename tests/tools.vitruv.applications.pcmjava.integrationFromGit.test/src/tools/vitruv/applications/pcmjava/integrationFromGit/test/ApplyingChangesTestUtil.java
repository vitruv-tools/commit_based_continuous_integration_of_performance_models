package tools.vitruv.applications.pcmjava.integrationFromGit.test;

import static edu.kit.ipd.sdq.commons.util.java.lang.IterableUtil.claimOne;

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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.EList;
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
import org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.classifiers.impl.ClassImpl;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.impl.CompilationUnitImpl;
import org.emftext.language.java.members.ClassMethod;
import org.emftext.language.java.members.Field;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.impl.NamespaceClassifierReferenceImpl;
import org.junit.Assert;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.repository.Parameter;
import org.palladiosimulator.pcm.repository.PrimitiveDataType;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.RepositoryFactory;
import org.palladiosimulator.pcm.repository.RequiredRole;
import org.palladiosimulator.pcm.repository.impl.RepositoryFactoryImpl;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.SeffFactory;
import org.somox.gast2seff.visitors.AbstractFunctionClassificationStrategy;
import org.somox.gast2seff.visitors.FunctionCallClassificationVisitor;
import org.somox.gast2seff.visitors.InterfaceOfExternalCallFindingFactory;
import org.somox.gast2seff.visitors.MethodCallFinder;
import org.somox.gast2seff.visitors.ResourceDemandingBehaviourForClassMethodFinding;
import org.somox.gast2seff.visitors.VisitorUtils;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitChangeApplier;
import tools.vitruv.applications.pcmjava.integrationFromGit.GitRepository;
import tools.vitruv.applications.pcmjava.linkingintegration.PcmJavaCorrespondenceModelTransformation;
import tools.vitruv.applications.pcmjava.seffstatements.code2seff.BasicComponentFinding;
import tools.vitruv.applications.pcmjava.seffstatements.pojotransformations.code2seff.PojoJava2PcmCodeToSeffFactory;
import tools.vitruv.domains.java.JavaDomainProvider;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilder;
import tools.vitruv.domains.java.builder.VitruviusJavaBuilderApplicator;
import tools.vitruv.domains.pcm.PcmDomainProvider;
import tools.vitruv.domains.pcm.PcmNamespace;
import tools.vitruv.framework.change.processing.ChangePropagationSpecification;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.CorrespondenceModelUtil;
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

/**
 * Contains many convenient methods used for testing. 
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 *
 */
public class ApplyingChangesTestUtil {

	/**
	 * Copies the given project with <code>projectPath</code> and <code>projectName</code> into <code>workspace</code>
	 * and creates a JDT model of the copied project.
	 * 
	 * @param workspace current workspace
	 * @param projectName project name
	 * @param projectPath path to the project
	 * @return JDT model of the project
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public static IProject importAndCopyProjectIntoWorkspace(IWorkspace workspace, String projectName,
			String projectPath)
			throws IOException, URISyntaxException, InvocationTargetException, InterruptedException {
		IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
			public String queryOverwrite(String file) {
				return ALL;
			};
		};
		IPath workspacePath = workspace.getRoot().getFullPath().append("/" + projectName);
		//Copies the project into the workspace
		ImportOperation importOperation = new ImportOperation(workspacePath, new File(projectPath/* baseDir */),
				FileSystemStructureProvider.INSTANCE, overwriteQuery);
		importOperation.setCreateContainerStructure(false);
		importOperation.run(null);
		return workspace.getRoot().getProject(projectName);
	}

	
	//
	/**
	 * Creates a JDT Model of the project with <code>pathToProject</code>. The created project can be then found in <code>workspace</code>
	 * 
	 * WARNING: the created project will probably overwrite an another project in the workspace, if both projects have the same name.
	 * 
	 * @param workspace current workspace
	 * @param pathToProject path to the project
	 * @return created JDT model of the project
	 * @throws CoreException
	 */
	public static IProject createIProject(IWorkspace workspace, String pathToProject) throws CoreException {
		IPath projectDotProjectFile = new Path(pathToProject + "/.project");
		IProjectDescription projectDescription = workspace.loadProjectDescription(projectDotProjectFile);
		IProject project = workspace.getRoot().getProject(projectDescription.getName());
		JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), null);
		return project;
	}

	
	/**
	 * Integrates the given <code>project</code> in Vitruv.
	 * 
	 * @param project project that will be integrated in Vitruv
	 * @param changePropagationSpecifications change propagation specifications which should define reactions for changes on the project
	 * @param listener listener for the changes on the <code>project</code>
	 * @return Vitruv Internal Virtual Model which contains all meta data for the integrated <code>project</code>
	 */
	public static InternalVirtualModel integrateProjectWithChangePropagationSpecification(final IProject project,
			final ChangePropagationSpecification[] changePropagationSpecifications,
			final ChangePropagationListener listener) {
		// workspace relative Path
		// IPath projectPath = project.getFullPath(); 
		//Absolute path to the project
		final IPath projectPath = project.getLocation();
		//Path to the PCM Repository corresponding to the project. The PCM Repository name must be "internal_architecture_model"
		final IPath models = projectPath.append("model").addTrailingSeparator().append("internal_architecture_model");
		//File extension for the Source Code Decorator Model
		final IPath scdmPath = models.addFileExtension("sourcecodedecorator");
		//File extension for the PCM Repository
		final IPath pcmPath = models.addFileExtension("repository");
		//Pathes to all source folders
		List<IPath> srcPaths = new ArrayList<IPath>();
		//Create a java project for the given project
		IJavaProject javaProject = JavaCore.create(project);
		try {
			//Find pathes to all source folders
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
		//Make pathes to the source folders absolute
		List<IPath> jamoppPaths = new ArrayList<>();
		for (IPath path : srcPaths) {
			IPath projectRelative = path.removeFirstSegments(1);
			IPath abs = project.getLocation().append(projectRelative);
			jamoppPaths.add(abs);
		}
		//Absolute path to the folder, that contains the project
		final IPath projectBase = projectPath.removeLastSegments(1);
		//Check if a Source Code Decorator Model exists 
		final File f = scdmPath.toFile();
		if (!f.exists()) {
			throw new IllegalArgumentException("Run SoMoX first!");
		}
		//Create Vitruv configuration for Java und PCM Metamodels
		List<VitruvDomain> metamodels = new ArrayList<VitruvDomain>();
		metamodels.add(new PcmDomainProvider().getDomain());
		metamodels.add(new JavaDomainProvider().getDomain());
		VirtualModelConfiguration config = new VirtualModelConfiguration();
		for (VitruvDomain metamodel : metamodels) {
			config.addMetamodel(metamodel);
		}
		//Add change propagation specifications to the Virtuv configuration
		for (int i = 0; i < changePropagationSpecifications.length; i++) {
			config.addChangePropagationSpecification(changePropagationSpecifications[i]);
		}
		//Create user dialog
		InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();
		//Create forlder for Vitruv meta data
		File vsumFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "vitruvius.meta");
		//Create Vitruv Virtual Model
		final InternalVirtualModel vsum = new VirtualModelImpl(vsumFolder, userInteractor, config);		
		//Transform Source Code Decarator Model into Vitruv Correspondences
		final PcmJavaCorrespondenceModelTransformation transformation = new PcmJavaCorrespondenceModelTransformation(
				scdmPath.toString(), pcmPath.toString(), jamoppPaths, vsum, projectBase);
		transformation.createCorrespondences();
		//Add change propagation listener to the project that will be notified when the project changes
		vsum.addChangePropagationListener(listener);
		//Build project. It creates a Vitruv monitor to detect and propagate changes
		VitruviusJavaBuilderApplicator pcmJavaBuilder = new VitruviusJavaBuilderApplicator();
		pcmJavaBuilder.addToProject(project, vsumFolder,
				Collections.singletonList(PcmNamespace.REPOSITORY_FILE_EXTENSION));
		try {
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, VitruviusJavaBuilder.BUILDER_ID,
					new HashMap<String, String>(), new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return vsum;
	}
	
	
	/**
	 * Integrates the given <code>project</code> with an already existing folder, that contains Vitruv meta data for the project, in Vitruv.
	 * 
	 * @param project project that will be integrated in Vitruv
	 * @param changePropagationSpecifications change propagation specifications which should define reactions for changes on the project
	 * @param listener listener for the changes on the <code>project</code>
	 * @param pathToExistingVSUM path to the folder that contains Vitruv meta data like Correspondence Model and virutal single underlying model
	 * @param workspace current workspace
	 * @return internal Virtual Model which contains all meta data for the integrated <code>project</code>
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 * 
	 */
	public static InternalVirtualModel integrateProjectWithChangePropagationSpecificationWithExistingVSUM(final IProject project,
			final ChangePropagationSpecification[] changePropagationSpecifications,
			final ChangePropagationListener listener, final String pathToExistingVSUM, final IWorkspace workspace) throws InvocationTargetException, IOException, URISyntaxException, InterruptedException {
		
		//Copy VSUM folder into workspace
		importAndCopyProjectIntoWorkspace(workspace, "vitruvius.meta", pathToExistingVSUM);
		//Create Vitruv configuration for Java und PCM Metamodels
		List<VitruvDomain> metamodels = new ArrayList<VitruvDomain>();
		metamodels.add(new PcmDomainProvider().getDomain());
		metamodels.add(new JavaDomainProvider().getDomain());
		VirtualModelConfiguration config = new VirtualModelConfiguration();
		for (VitruvDomain metamodel : metamodels) {
			config.addMetamodel(metamodel);
		}
		//Add change propagation specifications to the Virtuv configuration
		for (int i = 0; i < changePropagationSpecifications.length; i++) {
			config.addChangePropagationSpecification(changePropagationSpecifications[i]);
		}
		//Create user dialog
		InternalUserInteractor userInteractor = UserInteractionFactory.instance.createDialogUserInteractor();
		
		File vsumFolder = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "vitruvius.meta");
		final InternalVirtualModel vsum = new VirtualModelImpl(vsumFolder,
				userInteractor, config);
		//Add change propagation listener to the project that will be notified when the project changes
		vsum.addChangePropagationListener(listener);
		//Build project. It creates a Vitruv monitor to detect and propagate changes
		VitruviusJavaBuilderApplicator pcmJavaBuilder = new VitruviusJavaBuilderApplicator();
		pcmJavaBuilder.addToProject(project, vsumFolder,
				Collections.singletonList(PcmNamespace.REPOSITORY_FILE_EXTENSION));
		try {
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, VitruviusJavaBuilder.BUILDER_ID,
					new HashMap<String, String>(), new NullProgressMonitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return vsum;
	}
	

	/**
	 * Copies the given git repository with <code>originGitRepositoryPath</code> into <code>workspace</code>
	 * 
	 * @param workspace current workspace
	 * @param originGitRepositoryPath path to the git repository
	 * @return copied git repository
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static GitRepository copyGitRepositoryIntoWorkspace(IWorkspace workspace, String originGitRepositoryPath) throws IOException, InterruptedException {
		//Create a new folder for git repository
		File clonedGitRepository = (new File(workspace.getRoot().getLocation().toFile(), "clonedGitRepositories"));
		if (clonedGitRepository.exists()) {
			FileUtils.deleteDirectory(clonedGitRepository);
		}
		clonedGitRepository.mkdir();
		try {
			clonedGitRepository.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Create a file object for the origin git repository
		File originGitRepository = new File(originGitRepositoryPath);
		GitRepository gitRepository = null;
		//Copie git repository
		try {
			gitRepository = new GitRepository(clonedGitRepository, originGitRepository.getAbsolutePath());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

		return gitRepository;
	}
	
	
	/**
	 * Compares Compilation Units buffers of the <code>firstCompilationUnit</code> and <code>secondCompilationUnit</code>.
	 * The buffers contain source java code.
	 * 
	 * @param firstCompilationUnit first compilation unit
	 * @param secondCompilationUnit second compilation unit
	 * @param checkNameEquality if true, the names of the compilation units will be compared first.
	 * @return true if the buffers are equal. If the flag <code>checkNameEquality</code> is true, the names of the compilation units will be compared first.
	 */
	public static boolean compareCompilationUnitsBuffers(ICompilationUnit firstCompilationUnit, ICompilationUnit secondCompilationUnit, boolean checkNameEquality) {
		//If the flag checkNameEquality is true, compare the compilation units names
		if (checkNameEquality) {
			boolean sameName = firstCompilationUnit.getElementName().equals(secondCompilationUnit.getElementName());
			if (!sameName) {
				System.out.println("The compilation units " + firstCompilationUnit.getElementName() + " and " + secondCompilationUnit.getElementName() + " are not equal");
				return false;
			}				
		}
		
		boolean sameContent = false;
		//Compare the compilation units buffers
		try {
			String firstCompUnitContent = firstCompilationUnit.getBuffer().toString();
			String secondCompUnitContent = secondCompilationUnit.getBuffer().toString();
			sameContent = firstCompUnitContent.equals(secondCompUnitContent);
			//If not equal, maybe the compilation units have different line separators.
			//Therefore try to replace line separator in both compilation units and compare them again. 
			if (!sameContent) {
				String lineSeparator = System.lineSeparator();
				firstCompUnitContent = firstCompUnitContent.replaceAll("\r\n|\n|\r", lineSeparator);
				secondCompUnitContent = secondCompUnitContent.replaceAll("\r\n|\n|\r", lineSeparator);
				sameContent = firstCompUnitContent.equals(secondCompUnitContent);
			}
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		System.out.println("Equality of the compilation units buffers  = " + sameContent);
		return sameContent;
		
	}
		
	
	/**
	 * Compares JaMoPP Models of the two compilation units.
	 * 
	 * @param changedCompilationUnit compilation unit which was changed using {@link tools.vitruv.applications.pcmjava.integrationFromGit.GitChangeApplier.java}
	 * @param gitCompilationUnit reference compilation unit taken from {@link GitRepository} 
	 * @param virtualModel Vitruv Internal Virtual Model that contains the JaMoPP model for <code>changedCompilationUnit</code>
	 * @return true if the JaMoPP models of <code>changedCompilationUnit</code> and <code>gitCompilationUnit</code> are equal
	 * @throws IOException 
	 * @throws CoreException 
	 * @throws InterruptedException 
	 */
	public static boolean compareJaMoPPCompilationUnits(ICompilationUnit changedCompilationUnit, ICompilationUnit gitCompilationUnit, InternalVirtualModel virtualModel) throws IOException, CoreException, InterruptedException {
		//synchronization problems may occur. If so, the EqualityHelper returns the result, that the models are not equal.
		//Thread.sleep(5000);
		//changedCompilationUnit.getResource().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		//Find the JaMoPP model for the changedCompilationUnit
		ModelInstance modelInstance = virtualModel.getModelInstance(VURI.getInstance(changedCompilationUnit.getResource()));
		modelInstance.load(new HashMap(), true);
		CompilationUnit changedCompilationUnit_JaMoPP = (CompilationUnit) modelInstance.getResource().getContents().get(0);

		
		OutputStream changedCompilationUnitStream = new ByteArrayOutputStream();
		OutputStream gitCompilationUnitStream = new ByteArrayOutputStream();

        //Save the JaMoPP model content for changedCompilationUnit
		virtualModel.executeCommand(new Callable<Void>() {

            @Override
            public Void call() {
                try {
                	//Prints the JaMoPP model
                	//System.out.println("changedCompilationUnit_JaMoPP:\n");
            		//changedCompilationUnit_JaMoPP.eResource().unload();
            		changedCompilationUnit_JaMoPP.eResource().load(new HashMap()); 
                	changedCompilationUnit_JaMoPP.eResource().save(changedCompilationUnitStream, new HashMap());
                	//System.out.println(changedCompilationUnitStream);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });
		 
		//Create a temporary JaMoPP model for the gitCompilationUnit
		CompilationUnit gitCompilationUnit_JaMoPP = getJaMoPPRootForVURI(VURI.getInstance(gitCompilationUnit.getResource()));
		//Save the JaMoPP model content for gitCompilationUnit
		virtualModel.executeCommand(new Callable<Void>() {

            @Override
            public Void call() {
                
                try {
                	//Prints the JaMoPP model
                	//System.out.println("gitClassifier:\n");
                	gitCompilationUnit_JaMoPP.eResource().save(gitCompilationUnitStream, new HashMap());
                	//System.out.println(gitCompilationUnitStream);
                } catch (final Throwable e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });

		//Compare the JaMoPP models using EMFCompare
		Comparison comparison = compareTwoModels(changedCompilationUnit_JaMoPP, gitCompilationUnit_JaMoPP);
		//Get found matches from comparison
		//List<Match> matches = comparison.getMatches();
		//Get found differences from comparison
		List<Diff> differences = comparison.getDifferences();
		//If no differences were found, the JaMoPP models are equal
		if (differences.size() == 0) {
			System.out.println("EMFCompare returned the result, that the JaMoPP-Models are equal.");
			//Close streams
			try {
				changedCompilationUnitStream.close();
				gitCompilationUnitStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		else {
			//Try to compare the JaMoPP-Models with EqualityHelper
			System.out.println("EMFCompare returned the result, that the JaMoPP-Models are NOT equal. Now compare the JaMoPP-Models with EMF EqualityHelper:");
			EqualityHelper equalityHelper = new EqualityHelper();
			boolean modelsAreEqual = equalityHelper.equals(changedCompilationUnit_JaMoPP, gitCompilationUnit_JaMoPP);
			if (modelsAreEqual) {
				System.out.println("EMF EqualityHelper returned the result, that the JaMoPP-Models are equal.");
				//Close streams
				try {
					changedCompilationUnitStream.close();
					gitCompilationUnitStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
			else {
				//Try to compare the containing code of the JaMoPP models
				System.out.println("EMF EqualityHelper returned the result, that the JaMoPP-Models are NOT equal. Now compare the containing code of the JaMoPP-Models with String.equals:");
				boolean containingCodeIsEqual =  changedCompilationUnitStream.toString().equals(gitCompilationUnitStream.toString());
				if (containingCodeIsEqual) {
					System.out.println("The containing code of the JaMoPP-Models is equal.");
					//Close streams
					try {
						changedCompilationUnitStream.close();
						gitCompilationUnitStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
				else {
					System.out.println("The containing code of the JaMoPP-Models is not equal.");
					//Close streams
					try {
						changedCompilationUnitStream.close();
						gitCompilationUnitStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			}
		}
	}
	
	
	/**
	 * Merges the compared JaMoPP Models so that they are equal afterwards
	 * 
	 * @param comparison the comparison result, which contains the compared models as well as the differences and matches between them.
	 */
	public void mergeModels(Comparison comparison) {
	    IMerger.Registry mergerRegistry = new IMerger.RegistryImpl().createStandaloneInstance();
	    IBatchMerger merger = new BatchMerger(mergerRegistry);
	    merger.copyAllLeftToRight(comparison.getDifferences(), new BasicMonitor());
	}
	
	
	
	
	/**
	 * Compares two JaMoPP Models <code>firstModel</code> and <code>secondModel</code> using {@link EMFCompare}
	 *  @see <a href="https://www.eclipse.org/emf/compare/documentation/latest/developer/developer-guide.html">http://eclipse.org</a>
	 * 
	 * @param firstModel first JaMoPP Model
	 * @param secondModel second JaMoPP Model
	 * @return comparison result
	 */
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

	
	/**
	 * Creates a JaMoPPRoot model for the given <code>vuri</code>. The <code>vuri</code> must be valid und point to a java file.
	 * 
	 * @param <T>
	 * @param vuri vuri that points to a java file
	 * @return Created JaMoPPRoot model for the given <code>vuri</code>
	 */
	private static <T extends JavaRoot> T getJaMoPPRootForVURI(final VURI vuri) {
		final Resource resource = URIUtil.loadResourceAtURI(vuri.getEMFUri(), new ResourceSetImpl());
		// unchecked is OK for the test.
		@SuppressWarnings("unchecked")
		final T javaRoot = (T) resource.getContents().get(0);
		return javaRoot;
	}
	
	
	/**
	 * Creates an empty {@link BasicComponent} for the given JaMoPP <code>concreteClassifier</code>
	 * 
	 * @param concreteClassifier JaMoPP {@link ConcreteClassifier}
	 * @return created empty {@link BasicComponent}
	 */
	public static BasicComponent createPCMRepositoryForConcreteClassifier(ConcreteClassifier concreteClassifier) {
		
		RepositoryFactory repoFactory = RepositoryFactoryImpl.init();
		BasicComponent basicComponent = (BasicComponent) repoFactory.create(concreteClassifier.eClass());
		return basicComponent;
	}
	
	
	/**
	 * Creates a {@link ResourceDemandingSEFF} for the given <code>abstractActions</code>
	 * 
	 * @param abstractActions
	 * @return Created {@link ResourceDemandingSEFF}
	 */
	public static ResourceDemandingSEFF createSEFFWithAbstractActions(final AbstractAction... abstractActions) {
		final ResourceDemandingSEFF expectedSeff = SeffFactory.eINSTANCE.createResourceDemandingSEFF();
		addActionsToBehaviour(expectedSeff, abstractActions);
		return expectedSeff;
	}

	
	/**
	 * Creates a {@link ResourceDemandingBehaviour} for the given <code>abstractActions</code>
	 * 
	 * @param abstractActions
	 * @return Created {@link ResourceDemandingBehaviour}
	 */
	public static ResourceDemandingBehaviour createResourceBehaviourWithAbstractActions(
			final AbstractAction... abstractActions) {
		final ResourceDemandingBehaviour behaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
		addActionsToBehaviour(behaviour, abstractActions);
		return behaviour;
	}

	
	/**
	 * Adds <code>abstractActions</code> to the {@link ResourceDemandingBehaviour} <code>behaviour</code> 
	 * 
	 * @param behaviour A {@link ResourceDemandingBehaviour} which the <code>abstractActions</code> will be added to
	 * @param abstractActions abstract actions to add
	 */
	public static void addActionsToBehaviour(final ResourceDemandingBehaviour behaviour,
			final AbstractAction... abstractActions) {
		behaviour.getSteps_Behaviour().add(SeffFactory.eINSTANCE.createStartAction());

		for (final AbstractAction abstractAction : abstractActions) {
			behaviour.getSteps_Behaviour().add(abstractAction);
		}

		behaviour.getSteps_Behaviour().add(SeffFactory.eINSTANCE.createStopAction());
	}

	//TODO javadoc
	public static boolean compareAllChangedJaMoPPModels(IProject changedProject, IProject projectFromGitRepository, ArrayList<DiffEntry> changes, InternalVirtualModel virtualModel) throws CoreException, IOException, InterruptedException {
		
		boolean allModelsAreEqual = true;
		
		for (DiffEntry change : changes) {
			//Classify changes and call an appropriate routine
			switch (change.getChangeType()) {
			//Add a new file
			case ADD:
				allModelsAreEqual = compareTwoFiles(changedProject, projectFromGitRepository, change.getNewPath(), virtualModel);
				break;
			//Copy an existing file
			case COPY:
				allModelsAreEqual = compareTwoFiles(changedProject, projectFromGitRepository, change.getNewPath(), virtualModel);
				break;
			//Remove an existing file
			case DELETE:
				//TODO: Vitruv can not propagate change of type "remove Class or Interface" yet
				break;
			//Modify an existing file
			case MODIFY:
				allModelsAreEqual = compareTwoFiles(changedProject, projectFromGitRepository, change.getOldPath(), virtualModel);
				break;
			//Rename an existing file
			case RENAME:
				allModelsAreEqual = compareTwoFiles(changedProject, projectFromGitRepository, change.getNewPath(), virtualModel);
				break;
			default:
				//Error
				System.out.println("Changes for the DiffEntry " + change + "could not be classified");
				break;
			}
			
			if (!allModelsAreEqual) {
				System.out.println("Equality failed for  " + change);
				return false;
			}
		}
		
		return true;
	}
	
	//TODO javadoc
	private static boolean compareTwoFiles(IProject changedProject, IProject projectFromGitRepository, String pathToFile, InternalVirtualModel virtualModel) throws CoreException, IOException, InterruptedException {
		
		if (pathToFile.endsWith(".java")) {
			ICompilationUnit changedCompilationUnit = GitChangeApplier.findICompilationUnitInProject(pathToFile, changedProject);
			ICompilationUnit referenceCompilationUnit = GitChangeApplier.findICompilationUnitInProject(pathToFile, projectFromGitRepository);
			return compareJaMoPPCompilationUnits(changedCompilationUnit, referenceCompilationUnit, virtualModel);
		}
		else {
			IFile changedFile = changedProject.getFile(pathToFile);
			IFile referenceFile = projectFromGitRepository.getFile(pathToFile);
			return changedFile.getContents().toString().equals(referenceFile.getContents().toString());
		}

	}

	//TODO javadoc
	 public static boolean assertRepositoryComponentWithName(String nameOfComponent, InternalVirtualModel virtualModel) throws Throwable {
		    final Set<RepositoryComponent> repoComponents = virtualModel.getCorrespondenceModel().<RepositoryComponent>getAllEObjectsOfTypeInCorrespondences(RepositoryComponent.class);
		    for (final RepositoryComponent repoComponent : repoComponents) {
		    	if (repoComponent.getEntityName().contains(nameOfComponent)) {
		    		return true;
		    	}  
			}
		    
		    final Set<Repository> repos = virtualModel.getCorrespondenceModel().<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class);
		    for (final Repository repo : repos) {
		    	if (repo.getEntityName().contains(nameOfComponent)) {
		    		return true;
		    	}  
			}	    

		    return false;
	 }
		  
	//TODO javadoc
	  public static boolean assertNoRepositoryComponentWithName(String nameOfComponent, InternalVirtualModel virtualModel) throws Throwable {
		    final Set<RepositoryComponent> repoComponents = virtualModel.getCorrespondenceModel().<RepositoryComponent>getAllEObjectsOfTypeInCorrespondences(RepositoryComponent.class);
		    for (final RepositoryComponent repoComponent : repoComponents) {
			      boolean _contains = repoComponent.getEntityName().contains(nameOfComponent);
			      if (_contains) {
			    	  System.out.println("basic component with name " + nameOfComponent + " found: " + repoComponent);
			    	  return false;
			      }
			}

		    final Set<Repository> repos = virtualModel.getCorrespondenceModel().<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class);
		    for (final Repository repo : repos) {
			      boolean _contains = repo.getEntityName().contains(nameOfComponent);
			      if (_contains) {
			    	  System.out.println("repository with name " + nameOfComponent + " found: " + repo);
			    	  return false;
			      }
			}
		    
		    return true;
	  }
	  
	  
	//TODO javadoc
	 public static boolean assertOperationInterfaceWithName(String nameOfComponent, InternalVirtualModel virtualModel) throws Throwable {
		//Get rid of the file extension ".java". The length of ".java" is 5.
		String nameWithoutFileExtention = nameOfComponent.substring(0, nameOfComponent.length() - 5);
	    final Set<OperationInterface> repoComponents = virtualModel.getCorrespondenceModel().<OperationInterface>getAllEObjectsOfTypeInCorrespondences(OperationInterface.class);;
	    for (final OperationInterface repoComponent : repoComponents) {
	    	if (repoComponent.getEntityName().contains(nameWithoutFileExtention)) {
	    		return true;
	    	}  
		}

	    return false;
	 }
		  
	//TODO javadoc
	  public static boolean assertNoOperationInterfaceWithName(String nameOfComponent, InternalVirtualModel virtualModel) throws Throwable {   
		// Get rid of the file extension ".java". The length of ".java" is 5.
		String nameWithoutFileExtention = nameOfComponent.substring(0, nameOfComponent.length() - 5);
		final Set<OperationInterface> repoComponents = virtualModel.getCorrespondenceModel()
				.<OperationInterface>getAllEObjectsOfTypeInCorrespondences(OperationInterface.class);
		for (final OperationInterface repoComponent : repoComponents) {
			boolean _contains = repoComponent.getEntityName().contains(nameWithoutFileExtention);
			if (_contains) {
				System.out.println("basic component with name " + nameOfComponent + " found: " + repoComponent);
				return false;
			}
		}

		return true;
	  }
  
	  
	  public static boolean assertOneInternalAction(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}

			Set<InternalAction> internalActions_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  InternalAction.class);
			if (internalActions_PCM.size() == 1) {
				return true;
			}
			
			return false;
	  }
	  
	  
	  public static boolean assertNoInternalAction(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}

			Set<InternalAction> internalActions_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  InternalAction.class);
			if (internalActions_PCM.size() == 0) {
				return true;
			}
			
			return false;
	  }
	  
	  
	  public static boolean assertOneLoopWithOneExternalCall(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}

			Set<LoopAction> loopActions_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  LoopAction.class);
			if (loopActions_PCM.size() == 1) {
				Iterator<LoopAction> iterator = loopActions_PCM.iterator();
				EList<AbstractAction> actionsInLoop = iterator.next().getBodyBehaviour_Loop().getSteps_Behaviour();
				if (actionsInLoop.size() == 3 && actionsInLoop.get(1) instanceof ExternalCallAction) {
					return true;	
				}
			}
			
			return false;
	  }
	  
	  
	  public static boolean assertNoLoop(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}

			Set<LoopAction> loopActions_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  LoopAction.class);
			if (loopActions_PCM.size() == 0) {
				return true;
			}
			
			return false;
	  }
	  
	  
	  public static boolean assertOneBranchWithOneExternalCallAndOneInternalAction(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}

			Set<BranchAction> branchActions_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  BranchAction.class);
			if (branchActions_PCM.size() == 1) {
				Iterator<BranchAction> iterator = branchActions_PCM.iterator();
				EList<AbstractBranchTransition> ifElseBranches = iterator.next().getBranches_Branch();
				if (ifElseBranches.size() == 2) {
					EList<AbstractAction> stepsBehaviour_if =  ifElseBranches.get(0).getBranchBehaviour_BranchTransition().getSteps_Behaviour();
					EList<AbstractAction> stepsBehaviour_else =  ifElseBranches.get(1).getBranchBehaviour_BranchTransition().getSteps_Behaviour();
					if (stepsBehaviour_if.size() == 3 && stepsBehaviour_if.get(1) instanceof InternalAction
						&& stepsBehaviour_else.size() == 3 && stepsBehaviour_else.get(1) instanceof ExternalCallAction) {
						return true;
					}
					//Maybe wrong order. Swap if and else branches.
					if (stepsBehaviour_if.size() == 3 && stepsBehaviour_if.get(1) instanceof ExternalCallAction
							&& stepsBehaviour_else.size() == 3 && stepsBehaviour_else.get(1) instanceof InternalAction) {
							return true;
					}
				}
			}
			
			return false;
	  }
	  
	  
	  public static boolean assertNoBranch(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}

			Set<BranchAction> branchActions_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  BranchAction.class);
			if (branchActions_PCM.size() == 0) {
				return true;
			}
			
			return false;
	  }
	  
	  
	  
	  public static boolean assertClassMethodWithName(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}
			//Find the corresponding PCM SEFF. If not found, look for an InternalAction or an ExternalCall corresponding to method_JaMoPP
			Set<ResourceDemandingSEFF> SEFFs_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  ResourceDemandingSEFF.class);
			if (!SEFFs_PCM.isEmpty()) {
				return true;
			}

			return false;
	  }
	  
	  public static boolean assertInterfaceMethodWithName(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}
			//Find the corresponding PCM OperationRequiredRole
			//claimOne throws an exception if no or many correspondences was found
			OperationSignature method_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  OperationSignature.class));
			
			return true;
	  }
	  
	  
	  public static boolean assertInterfaceMethodPrimitiveReturnTypeWithName(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}
			//Find the corresponding PCM OperationRequiredRole
			//claimOne throws an exception if no or many correspondences was found
			OperationSignature method_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  OperationSignature.class));
			DataType returnType = method_PCM.getReturnType__OperationSignature();
			if (returnType instanceof PrimitiveDataType) {
				return true;
			}
			else {
				return false;
			}
	  }
	  
	
	  
	  public static boolean assertInterfaceMethodParameterWithName(String methodName, String parameterName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return false;
			}
			//Find the corresponding PCM OperationRequiredRole
			//claimOne throws an exception if no or many correspondences was found
			OperationSignature method_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  OperationSignature.class));
			EList <Parameter> parameters = method_PCM.getParameters__OperationSignature();
			for (Parameter parameter : parameters) {
				if (parameter.getParameterName().equals(parameterName)) {
					return true;
				}
			}
			
			return false;
	  }
	  
	  
	  public static boolean assertNoClassMethodWithName(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return true;
			}
			//Find the corresponding PCM SEFF. If not found, look for an InternalAction or an ExternalCall corresponding to method_JaMoPP
			Set<ResourceDemandingSEFF> SEFFs_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  ResourceDemandingSEFF.class);
			if (!SEFFs_PCM.isEmpty()) {
				return false;
			}
			/*TODO Remove
			Set<InternalAction> internalActions_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  InternalAction.class);
			if (!internalActions_PCM.isEmpty()) {
				return false;
			}
			Set<ExternalCallAction> externalCalls_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  ExternalCallAction.class);
			if (!externalCalls_PCM.isEmpty()) {
				return false;
			}
			*/
			return true;
	  }
	  
	  
	  public static boolean assertNoInterfaceMethodWithName(String methodName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Method method_JaMoPP = getJaMoPPMethodFromClass(compilationUnit, methodName, virtualModel);
			if (method_JaMoPP == null) {
				return true;
			}
			//Find the corresponding PCM OperationRequiredRole
			//claimOne throws an exception if no or many correspondences was found
			Set<OperationSignature> methods_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), method_JaMoPP,  OperationSignature.class);
			if (methods_PCM.isEmpty()) {
				return true;
			}
			else {
				return false;
			}
	  }
	  
	  
	  private static Method getJaMoPPMethodFromClass(ICompilationUnit compilationUnit, String methodName, InternalVirtualModel virtualModel) {
			//Find the JaMoPP model for the changedCompilationUnit
			ModelInstance modelInstance = virtualModel.getModelInstance(VURI.getInstance(compilationUnit.getResource()));
			modelInstance.load(new HashMap(), true);
			CompilationUnit compilationUnit_JaMoPP = (CompilationUnit) modelInstance.getResource().getContents().get(0);
			String classifierName = compilationUnit.getElementName();
			//Get rid of the file extension ".java". The length of ".java" is 5.
			classifierName = classifierName.substring(0, classifierName.length() - 5);
			EList<Member> method_JaMoPP = compilationUnit_JaMoPP.getContainedClassifier(classifierName).getMembersByName(methodName);
			if (method_JaMoPP.isEmpty()) {
				System.out.println("Method " + methodName + " was not found in compilation unit " + compilationUnit.getElementName());
				return null;
			}
			
			return (Method) method_JaMoPP.get(0);
			
	  }
	  
	  
	public static boolean assertFieldWithName(String fieldName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
		Field field_JaMoPP = getJaMoPPFieldFromClass(compilationUnit, fieldName, virtualModel);
		if (field_JaMoPP == null) {
			return false;
		}
		//Find the corresponding PCM OperationRequiredRole
		//claimOne throws an exception if no or many correspondences was found
		OperationRequiredRole field_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
				virtualModel.getCorrespondenceModel(), field_JaMoPP,  OperationRequiredRole.class));
		
		return true;
	}
	
	
	public static boolean assertOperationProvidedRole(String classifierName, String providedInterfaceName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
		ConcreteClassifier concreteClassifier_JaMoPP = getJaMoPPClassifier(compilationUnit, classifierName, virtualModel);
		if (concreteClassifier_JaMoPP == null) {
			return false;
		}
		for (TypeReference reference : ((ClassImpl) concreteClassifier_JaMoPP).getImplements()) {
			for (ClassifierReference classifierReference : ((NamespaceClassifierReferenceImpl) reference).getClassifierReferences()) {
				if (classifierReference.getTarget().getName().equals(providedInterfaceName)) {
					//Find the corresponding PCM OperationProvidedRole
					Set<OperationProvidedRole> providedRoles = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
							virtualModel.getCorrespondenceModel(), reference,  OperationProvidedRole.class);
					for (OperationProvidedRole providedRole : providedRoles) {
						if (providedRole.getEntityName().contains(providedInterfaceName)) {
							return true;
						}
					}
					return false;
				}
			}
		}
		
		return false;
	}
	
	
	public static boolean assertNoOperationProvidedRole(String classifierName, String providedInterfaceName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
		ConcreteClassifier concreteClassifier_JaMoPP = getJaMoPPClassifier(compilationUnit, classifierName, virtualModel);
		if (concreteClassifier_JaMoPP == null) {
			return false;
		}
		for (TypeReference reference : ((ClassImpl) concreteClassifier_JaMoPP).getImplements()) {
			for (ClassifierReference classifierReference : ((NamespaceClassifierReferenceImpl) reference).getClassifierReferences()) {
				if (classifierReference.getTarget().getName().equals(providedInterfaceName)) {
					//Find the corresponding PCM OperationProvidedRole
					Set<OperationProvidedRole> providedRoles = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
							virtualModel.getCorrespondenceModel(), reference,  OperationProvidedRole.class);
					for (OperationProvidedRole providedRole : providedRoles) {
						if (providedRole.getEntityName().contains(providedInterfaceName)) {
							return false;
						}
					}
					return true;
				}
			}
		}
		
		return true;
	}
	
	  
	private static Field getJaMoPPFieldFromClass(final ICompilationUnit compilationUnit, final String fieldName, final InternalVirtualModel virtualModel) {
		//Find the JaMoPP model for the changedCompilationUnit
		ModelInstance modelInstance = virtualModel.getModelInstance(VURI.getInstance(compilationUnit.getResource()));
		modelInstance.load(new HashMap(), true);
		CompilationUnit compilationUnit_JaMoPP = (CompilationUnit) modelInstance.getResource().getContents().get(0);
		String classifierName = compilationUnit.getElementName();
		//Get rid of the file extension ".java". The length of ".java" is 5.
		classifierName = classifierName.substring(0, classifierName.length() - 5);
		EList<Member> field_JaMoPP = compilationUnit_JaMoPP.getContainedClassifier(classifierName).getMembersByName(fieldName);
		if (field_JaMoPP.isEmpty()) {
			System.out.println("Field " + fieldName + " was not found in compilation unit " + compilationUnit.getElementName());
			return null;
		}
		
		return (Field) field_JaMoPP.get(0);
		
	}
	
	
	private static ConcreteClassifier getJaMoPPClassifier(final ICompilationUnit compilationUnit, final String classifierName, final InternalVirtualModel virtualModel) {
		//Find the JaMoPP model for the changedCompilationUnit
		ModelInstance modelInstance = virtualModel.getModelInstance(VURI.getInstance(compilationUnit.getResource()));
		modelInstance.load(new HashMap(), true);
		CompilationUnit compilationUnit_JaMoPP = (CompilationUnit) modelInstance.getResource().getContents().get(0);
		//Get rid of the file extension ".java". The length of ".java" is 5.
		String classifierNameWithoutJavaExtention = classifierName.substring(0, classifierName.length() - 5);
		
		return compilationUnit_JaMoPP.getContainedClassifier(classifierNameWithoutJavaExtention);	
	}
	
	
	public static boolean assertNoFieldWithName(String fieldName, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
		final Set<RepositoryComponent> repoComponents = virtualModel.getCorrespondenceModel().<RepositoryComponent>getAllEObjectsOfTypeInCorrespondences(RepositoryComponent.class);
	    //Find a corresponding PCM model to the compilationUnit
		for (final RepositoryComponent repoComponent : repoComponents) {
	    	if (repoComponent.getEntityName().contains(compilationUnit.getElementName())) {
	    		//Make sure there is no corresponding PCM models to the fieldName
	    		EList<RequiredRole> requiredRoles = repoComponent.getRequiredRoles_InterfaceRequiringEntity();
	    		for (RequiredRole requiredRole : requiredRoles) {
	    			if (requiredRole.getEntityName().equals(fieldName)) {
	    				return false;
	    			}
	    		}
	    		return true;
	    	}  
		}
	    
	    return false;
	}
	

	public static boolean assertFieldTypeWithName(String fieldName, String fieldType, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
		Field field_JaMoPP = getJaMoPPFieldFromClass(compilationUnit, fieldName, virtualModel);
		if (field_JaMoPP == null) {
			return false;
		}
		//Find the corresponding PCM OperationRequiredRole to the JaMoPP Field.
		//claimOne throws an exception if no or many correspondences was found
		OperationRequiredRole field_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
				virtualModel.getCorrespondenceModel(), field_JaMoPP, OperationRequiredRole.class));
		if (field_PCM.getRequiredInterface__OperationRequiredRole().getEntityName().equals(fieldType)) {
			return true;
		}
		else {
			return false;	
		}
	}
	
	
	public static boolean assertNoFieldTypeWithName(String fieldName, String fieldType, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
		Field field_JaMoPP = getJaMoPPFieldFromClass(compilationUnit, fieldName, virtualModel);
		if (field_JaMoPP == null) {
			return false;
		}
		//Find the corresponding PCM InnerDeclaraion to the JaMoPP Field.
		//claimOne throws an exception if no or many correspondences was found
		OperationRequiredRole field_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
				virtualModel.getCorrespondenceModel(), field_JaMoPP, OperationRequiredRole.class));
		if (field_PCM.getRequiredInterface__OperationRequiredRole().getEntityName().equals(fieldType)) {
			return false;
		}
		else {
			return true;	
		}
	}
	
	
	
	/*
	  public static boolean assertSEFFsForCompilationUnits(String methodName, ICompilationUnit changedCompilationUnit,  ICompilationUnit referenceCompilationUnit, InternalVirtualModel virtualModel) {
			Method changed_method_JaMoPP = getJaMoPPMethodFromClass(changedCompilationUnit, methodName, virtualModel);
			if (changed_method_JaMoPP == null) {
				return false;
			}
			//Find the corresponding PCM SEFF in Virtual Model
			Set<ResourceDemandingSEFF> SEFFs_PCM = CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), changed_method_JaMoPP,  ResourceDemandingSEFF.class);
			if (SEFFs_PCM.isEmpty()) {
				return false;
			}
			
			//Create a temporary JaMoPP model for the referenceCompilationUnit
			CompilationUnit referenceCompilationUnit_JaMoPP = getJaMoPPRootForVURI(VURI.getInstance(referenceCompilationUnit.getResource()));
			String classifierName = referenceCompilationUnit.getElementName();
			//Get rid of the file extension ".java". The length of ".java" is 5.
			classifierName = classifierName.substring(0, classifierName.length() - 5);
			ConcreteClassifier reference_classifier_JaMoPP = referenceCompilationUnit_JaMoPP.getContainedClassifier(classifierName);
			EList<Member> method_JaMoPP = reference_classifier_JaMoPP.getMembersByName(methodName);
			if (method_JaMoPP.isEmpty()) {
				System.out.println("Method " + methodName + " was not found in compilation unit " + referenceCompilationUnit.getElementName());
				return false;
			}
			Method reference_method_JaMoPP = (Method) method_JaMoPP.get(0);
			final ResourceDemandingBehaviour resourceDemandingBehaviour = SeffFactory.eINSTANCE.createResourceDemandingBehaviour();
			executeSoMoXForMethod(resourceDemandingBehaviour, changed_method_JaMoPP, reference_method_JaMoPP, virtualModel.getCorrespondenceModel());
			System.out.println("dummy line");
			return false;
	  }
	  
	  
	  private static void executeSoMoXForMethod(final ResourceDemandingBehaviour targetResourceDemandingBehaviour, final Method changedMethod_JaMoPP, final Method referenceMethod_JaMoPP, final CorrespondenceModel correspondenceModel) {
			final MethodCallFinder methodCallFinder = new MethodCallFinder();

			if (referenceMethod_JaMoPP instanceof ClassMethod) {
				// check whether the newMethod is a class method is done here. Could
				// be done eariler,
				// i.e. the class could only deal with ClassMethods, but this caused
				// problems when
				// changing an abstract method to a ClassMethod
				PojoJava2PcmCodeToSeffFactory code2SeffFactory = new PojoJava2PcmCodeToSeffFactory();
				BasicComponentFinding basicComponentFinding = code2SeffFactory.createBasicComponentFinding();
				BasicComponent basicComponent = basicComponentFinding.findBasicComponentForMethod(changedMethod_JaMoPP, correspondenceModel);
				AbstractFunctionClassificationStrategy classification = code2SeffFactory.createAbstractFunctionClassificationStrategy(basicComponentFinding,
						correspondenceModel, basicComponent);
				final FunctionCallClassificationVisitor functionCallClassificationVisitor = new FunctionCallClassificationVisitor(
						classification, methodCallFinder);
				ResourceDemandingBehaviourForClassMethodFinding resourceDemandingBehaviourForClassMethodFinding = code2SeffFactory.
						createResourceDemandingBehaviourForClassMethodFinding(correspondenceModel);
				InterfaceOfExternalCallFindingFactory interfaceOfExternalCallFinderFactory = code2SeffFactory.
						createInterfaceOfExternalCallFindingFactory(correspondenceModel,
								basicComponent);
				VisitorUtils.visitJaMoPPMethod(targetResourceDemandingBehaviour, basicComponent,
						(StatementListContainer) referenceMethod_JaMoPP, null, functionCallClassificationVisitor,
						interfaceOfExternalCallFinderFactory, resourceDemandingBehaviourForClassMethodFinding,
						methodCallFinder);
			} else {
				System.out.println("No SEFF recreated for method " + referenceMethod_JaMoPP.getName()
						+ " because it is not a class method. Method " + referenceMethod_JaMoPP);
			}

		}
*/
	 
	  
	/*  //The methods 
	 * assertFieldModifierWithName 
	 * assertNoFieldModifierWithName
	 * make no sense, because pcm Modells does not contain information about field modifiers 
	 * 
	 * make no sense
		public static boolean assertFieldModifierWithName(String fieldName, String fieldModifier, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Field field_JaMoPP = getJaMoPPFieldFromClass(compilationUnit, fieldName, virtualModel);
			if (field_JaMoPP == null) {
				return false;
			}
			//Find the corresponding PCM InnerDeclaraion to the JaMoPP Field.
			//claimOne throws an exception if no or many correspondences was found
			OperationRequiredRole field_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), field_JaMoPP, OperationRequiredRole.class));
			if (field_PCM.getEntityName().contains(fieldModifier)) {
				return true;
			}
			else {
				return false;	
			}
		}
		
		
		public static boolean assertNoFieldModifierWithName(String fieldName, String fieldModifier, ICompilationUnit compilationUnit, InternalVirtualModel virtualModel) {
			Field field_JaMoPP = getJaMoPPFieldFromClass(compilationUnit, fieldName, virtualModel);
			if (field_JaMoPP == null) {
				return false;
			}
			//Find the corresponding PCM InnerDeclaraion to the JaMoPP Field.
			//claimOne throws an exception if no or many correspondences was found
			InnerDeclaration field_PCM = claimOne(CorrespondenceModelUtil.getCorrespondingEObjectsByType(
					virtualModel.getCorrespondenceModel(), field_JaMoPP, InnerDeclaration.class));
			if (field_PCM.getEntityName().contains(fieldModifier)) {
				return false;
			}
			else {
				return true;	
			}
		}
		
	*/	  
	  
	  
	  /*
	  public static void assertNoComponentWithName(String nameOfComponent, InternalVirtualModel virtualModel) throws Throwable {
		    final Set<RepositoryComponent> repoComponents = virtualModel.getCorrespondenceModel().<RepositoryComponent>getAllEObjectsOfTypeInCorrespondences(RepositoryComponent.class);
		    assertNoBasicComponentWithName(nameOfComponent, repoComponents);
		    final Set<Repository> repos = virtualModel.getCorrespondenceModel().<Repository>getAllEObjectsOfTypeInCorrespondences(Repository.class);
		    final Consumer<Repository> _function = (Repository it) -> {
		      assertNoBasicComponentWithName(nameOfComponent, it.getComponents__Repository());
		    };
		    repos.forEach(_function);
	  }
		  
	  private static void assertNoBasicComponentWithName(final String nameOfComponent, final Iterable<RepositoryComponent> repoComponents) {
	    for (final RepositoryComponent repoComponent : repoComponents) {
	      boolean _contains = repoComponent.getEntityName().contains(nameOfComponent);
	      boolean _not = (!_contains);
	      Assert.assertTrue(((("basic component with name " + nameOfComponent) + " found: ") + repoComponent), _not);
	    }
	  }  
	  */
	  
	  
	//Does not work appropriate, because it returns empty BasicComponents. Therefore find an another approach to create a PCM Model
	/*
	public static boolean comparePCMBasicComponents(ICompilationUnit firstCompilationUnit, ICompilationUnit secondCompilationUnit) {
		ConcreteClassifier firstClassifier = getJaMoPPClassifierForICompilationUnit(firstCompilationUnit);
		ConcreteClassifier secondClassifier = getJaMoPPClassifierForICompilationUnit(secondCompilationUnit);
		
		BasicComponent firstBasicComponent = createPCMRepositoryForConcreteClassifier(firstClassifier);
		BasicComponent secondBasicComponent = createPCMRepositoryForConcreteClassifier(secondClassifier);
		
		return false;
	}
	*/  
	
	/*
	public static <T> boolean ensureExistenceOfCorrespondingPcmForCompilationUnit(InternalVirtualModel virtualModel,
			ICompilationUnit compilationUnit, Class<T> correspondingPcmElementType) {
		//Find the JaMoPP model for the JDT model of compilationUnit
		ModelInstance modelInstance = virtualModel.getModelInstance(VURI.getInstance(compilationUnit.getResource()));
		modelInstance.load(new HashMap(), true);
		CompilationUnit compilationUnit_JaMoPP = (CompilationUnit) modelInstance.getResource().getContents().get(0);
		//Try to get a corresponding PCM model
		final T correspondingPcm = claimOne(
				CorrespondenceModelUtil.getCorrespondingEObjectsByType(virtualModel.getCorrespondenceModel(), compilationUnit_JaMoPP, correspondingPcmElementType));
		//If no exception was thrown in the previous operations, a corresponding PCM model was found
		return true;
	}
	*/
	
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

	/*
	private static ConcreteClassifier getJaMoPPClassifierForICompilationUnit(ICompilationUnit compilationUnit) {
		return getJaMoPPClassifierForVURI(VURI.getInstance(compilationUnit.getResource()));
	}
	
	public static ConcreteClassifier getJaMoPPClassifierForVURI(final VURI vuri) {
		final CompilationUnit cu = getJaMoPPRootForVURI(vuri);
		final Classifier jaMoPPClassifier = cu.getClassifiers().get(0);
		return (ConcreteClassifier) jaMoPPClassifier;
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
	*/
	
	
}
