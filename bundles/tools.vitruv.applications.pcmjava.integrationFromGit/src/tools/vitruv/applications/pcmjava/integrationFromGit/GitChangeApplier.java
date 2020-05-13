package tools.vitruv.applications.pcmjava.integrationFromGit;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.containers.Package;

import edu.kit.ipd.sdq.commons.util.org.eclipse.emf.common.util.URIUtil;
import tools.vitruv.applications.pcmjava.linkingintegration.tests.util.DoneFlagProgressMonitor;
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.applications.pcmjava.tests.util.Java2PcmTransformationTest;
import tools.vitruv.applications.pcmjava.tests.util.SynchronizationAwaitCallback;
import tools.vitruv.framework.util.bridges.EMFBridge;
import tools.vitruv.framework.util.bridges.EcoreResourceBridge;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.vsum.VirtualModel;
import tools.vitruv.framework.vsum.modelsynchronization.ChangePropagationAbortCause;
import tools.vitruv.framework.vsum.modelsynchronization.ChangePropagationListener;

public class GitChangeApplier implements SynchronizationAwaitCallback, ChangePropagationListener {

	private GitRepository gitRepository;
	
	private static final Logger logger = Logger.getLogger(Java2PcmTransformationTest.class.getSimpleName());
	private static int MAXIMUM_SYNC_WAITING_TIME = 10000;
	private int expectedNumberOfSyncs = 0;
	
	
	public GitChangeApplier(GitRepository git) {
		this.gitRepository = git;
	}
	
	
	//TODO: Not only for Java Files, but also for other file types and packages
	/**
	 * @param oldCommit
	 * @param newCommit
	 * @param currentProject
	 * @throws CoreException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void applyChangesFromCommit(RevCommit oldCommit, RevCommit newCommit, IProject currentProject) throws CoreException, InterruptedException, IOException {
		
		ArrayList<DiffEntry> diffs = new ArrayList<>(gitRepository.computeDiffsBetweenTwoCommits(oldCommit, newCommit, /*true*/false, true));
		Collections.reverse(diffs); 

		for (DiffEntry diff : diffs) {
			
			ICompilationUnit iCu;
			
			switch (diff.getChangeType()) {
			case ADD:
				String pathToAddedFile = diff.getNewPath();
				String fileContent = gitRepository.getNewContentOfFileFromDiffEntry(diff);
				//JGit returns the content of files and uses within the content "\n" as line separator.
				//Therefore, replace all occurrences of "\n" with the system line separator.
				fileContent = gitRepository.replaceAllLineDelimitersWithSystemLineDelimiters(fileContent);
				addElementToProject(currentProject, pathToAddedFile, fileContent);
				break;
			case COPY:
				break;
			case DELETE:
				String nameOfDeletedFile = getNameOfFileFromPath(diff.getOldPath());
				iCu = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName(nameOfDeletedFile, currentProject);
				iCu.delete(true, null);			
				//waitForSynchronization(1);
				//Thread.sleep(20000);
				break;
			case MODIFY:
				OutputStream oldElementContent = gitRepository.getOldContentOfFileFromDiffEntryInOutputStream(diff);
				OutputStream newElementContent = gitRepository.getNewContentOfFileFromDiffEntryInOutputStream(diff);
				String oldElementPath = diff.getOldPath();
				String newElementPath = diff.getNewPath();
				EditList editList = gitRepository.computeEditListFromDiffEntry(diff);
				modifyElementInProject(currentProject, oldElementContent, newElementContent,
						oldElementPath, newElementPath, editList);
				break;
			case RENAME:
				break;
			default:
				break;
			}	
		}
	}

	
	public String getNameOfFileFromPath(String path) {
		String fileName = path.substring(path.lastIndexOf("/") + 1);
		//Get rid of file extention
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		return fileName;
	}
	
	

	/**
	 * @param project
	 * @param oldElementContent
	 * @param newElementContent
	 * @param oldElementPath
	 * @param newElementPath
	 * @param editList 
	 * @throws CoreException 
	 */
	private void modifyElementInProject(IProject project,
			OutputStream oldElementContent, OutputStream newElementContent, 
			String oldElementPath, String newElementPath,
			EditList editList) throws CoreException {
		
		//Check if the modified file is a java file
		//if ((new Path(oldElementPath).getFileExtension().equals("java")))
		if (oldElementPath.endsWith(".java")) {
			String nameOfChangedFile = getNameOfFileFromPath(oldElementPath);
			ICompilationUnit compilationUnit = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName(nameOfChangedFile, project);
			//EditList from JGit Bib
			//TextEdit from Eclipse text.edits
			String oldContent = oldElementContent.toString();
			//new String(((ByteArrayOutputStream) oldElementContent).toByteArray());
			String newContent = newElementContent.toString();
            //new String(((ByteArrayOutputStream) newElementContent).toByteArray());
			List<TextEdit> textEdits = gitRepository.transformEditListIntoTextEdits(editList, oldContent, newContent);
			CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, this, textEdits.toArray(new TextEdit[textEdits.size()]));
		}
		else {
			IFile file = project.getFile(oldElementPath.substring(oldElementPath.indexOf("/") + 1));
			if (file.exists()) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) newElementContent).toByteArray());
				file.setContents(inputStream, IFile.FORCE, null);
			}	
		}
	}
	
	
	/**
	 * Create new element in the project. A new element can be either folder or file.
	 * The pathToElement contains the path to the new element, but the other elements on the path
	 * may not exist yet. Therefore they also will be created.
	 * 
	 * @param project
	 * @param pathToElement
	 * @param elementContent might be null
	 * @throws CoreException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void addElementToProject(IProject project, String pathToElement, String elementContent) throws CoreException, InterruptedException, IOException {
		
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		
		IPath tempPath = new Path(pathToElement);
		//Get rid of the project name in the path
		tempPath = tempPath.removeFirstSegments(1);
		
		String[] segments = tempPath.segments();
		
		if (segments.length == 0) {
			System.out.println("Path: " + pathToElement + " is not valid");
			return;
		}
		
		//int segmentCounter = 0;
		String firstSegment = tempPath.segment(0);
		String lastSegment = tempPath.lastSegment();
		String fileExtension = tempPath.getFileExtension();
		
		IJavaProject javaProject;
		IPackageFragmentRoot packageFragmentRoot;
		IPackageFragment packageFragment;
		
		
		//Check if we need to handle IJavaProject
		//For more details, what a java project in JDT looks like, see https://www.vogella.com/tutorials/EclipseJDT/article.html
		if (firstSegment.equals("src") || firstSegment.equals("bin") 
				|| fileExtension.equals("jar") || fileExtension.equals("zip")) {
			
			javaProject = JavaCore.create(project);
			
			if (segments.length == 2) {
				IFile file = project.getFile(tempPath.toString());
				file.create(new ByteArrayInputStream(elementContent.getBytes()), false /*true*/, new NullProgressMonitor());
				System.out.println("Begin to wait for syncronization at the place 0");
				//waitForSynchronization(1);
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}
			else {
				//Check if the PackageFragmentRoot exists. If not, create it
				IFolder packageFragmentRootFolder = project.getFolder(tempPath.segment(0));
				
				if (!packageFragmentRootFolder.exists()) {
					packageFragmentRootFolder.create(false/*true*/, false/*true*/, new NullProgressMonitor());
					System.out.println("Begin to wait for syncronization at the place 1");
					//waitForSynchronization(1);
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());				
				}
				
				packageFragmentRoot = javaProject.getPackageFragmentRoot(packageFragmentRootFolder);
				String packageFragmentName = tempPath.removeFirstSegments(1).removeLastSegments(1).toString();
				packageFragmentName = packageFragmentName.replace("/", ".");
				packageFragment = packageFragmentRoot.getPackageFragment(packageFragmentName);
				
				if (!packageFragment.exists()) {
					
					//Create package per EMF and JaMoPP
					createPackageWithPackageInfo(project, tempPath.removeFirstSegments(1).removeLastSegments(1).segments());
				
					//Create packaga per JDT
					//packageFragment = packageFragmentRoot.createPackageFragment(packageFragmentName, false /*true*/, new NullProgressMonitor());
					////packageFragment.makeConsistent(new NullProgressMonitor());
					//System.out.println("Begin to wait for syncronization at the place 2");
					////waitForSynchronization(1);
					//project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					
				}
				
				packageFragment.makeConsistent(new NullProgressMonitor());
				
				if (fileExtension.equals("java")) {
					//For testing:
					ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
					
					
					ICompilationUnit compilationUnit = packageFragment.getCompilationUnit(lastSegment);
					if (!compilationUnit.exists()) {
						//Create java file per JDT
						compilationUnit = packageFragment.createCompilationUnit(lastSegment, "", false/*true*/, new NullProgressMonitor());
						Thread.sleep(5000);
						InsertEdit edit = new InsertEdit(0, elementContent);
						CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, this, edit);
						//VURI vuri = getVURIForElementInPackage(packageFragment, compilationUnit.getElementName());
						//getJaMoPPClassifierForVURI(vuri);
						
						System.out.println("Begin to wait for syncronization at the place 3");
						//waitForSynchronization(1);
						project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					}
				}
				else {
					IFile file = project.getFile(tempPath.toString());
					if (!file.exists()) {
						file.create(new ByteArrayInputStream(elementContent.getBytes()), false /*true*/, new NullProgressMonitor());
						System.out.println("Begin to wait for syncronization at the place 4");
						//waitForSynchronization(1);
						project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					}
				}		
			}
			
		}
		//We only have to handle IProject
		else {
			//String tempPathString = pathToElement.substring(pathToElement.indexOf("/") + 1);
			(new File(tempPath.toString())).mkdirs();
			//IFile file = project.getFile(tempPath);
			IFile file = project.getFile(tempPath.toString());
			if (!file.exists()) {
				file.create(new ByteArrayInputStream(elementContent.getBytes()), false /*true*/, new NullProgressMonitor());	
				System.out.println("Begin to wait for syncronization at the place 5");
				//waitForSynchronization(1);
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}
		}
	}
	
	
	protected Package createPackageWithPackageInfo(final IProject project, final String... namespace) throws IOException {
		String packageFile = StringUtils.join(namespace, "/");
		packageFile = packageFile + "/package-info.java";
		final Package jaMoPPPackage = ContainersFactory.eINSTANCE.createPackage();
		List<String> namespaceList = Arrays.asList(namespace);
		jaMoPPPackage.setName(namespaceList.get(namespaceList.size() - 1));
		jaMoPPPackage.getNamespaces().addAll(namespaceList.subList(0, namespaceList.size() - 1));
		
		ResourceSet resourceSet = new ResourceSetImpl();
		VURI vuri = VURI.getInstance(project.getName() + "/src/" + packageFile);
		final Resource resource = resourceSet.createResource(vuri.getEMFUri());
		
		EcoreResourceBridge.saveEObjectAsOnlyContent(jaMoPPPackage, resource);
		waitForSynchronization(1);
		return jaMoPPPackage;
	}
	
	
	private VURI getVURIForElementInPackage(final IPackageFragment packageFragment, final String elementName) {
		String vuriKey = packageFragment.getResource().getFullPath().toString() + "/" + elementName;
		if (vuriKey.startsWith("/")) {
			vuriKey = vuriKey.substring(1, vuriKey.length());
		}
		final VURI vuri = VURI.getInstance(vuriKey);
		return vuri;
	}
	
	protected ConcreteClassifier getJaMoPPClassifierForVURI(final VURI vuri) {
		final CompilationUnit cu = this.getJaMoPPRootForVURI(vuri);
		final Classifier jaMoPPClassifier = cu.getClassifiers().get(0);
		return (ConcreteClassifier) jaMoPPClassifier;
	}

	private <T extends JavaRoot> T getJaMoPPRootForVURI(final VURI vuri) {
		final Resource resource = URIUtil.loadResourceAtURI(vuri.getEMFUri(), new ResourceSetImpl());
		// unchecked is OK for the test.
		@SuppressWarnings("unchecked")
		final T javaRoot = (T) resource.getContents().get(0);
		return javaRoot;
	}
	
	
	
	@Override
	public synchronized void waitForSynchronization(int numberOfExpectedSynchronizationCalls) {
		expectedNumberOfSyncs += numberOfExpectedSynchronizationCalls;
		logger.debug("Starting to wait for finished synchronization. Expected syncs: "
				+ numberOfExpectedSynchronizationCalls + ", remaining syncs: " + expectedNumberOfSyncs);
		try {
			int wakeups = 0;
			while (expectedNumberOfSyncs > 0) {
				wait(MAXIMUM_SYNC_WAITING_TIME);
				wakeups++;
				// If we had more wakeups than expected sync calls, we had a
				// timeout
				// and so the synchronization was not finished as expected
				if (wakeups > numberOfExpectedSynchronizationCalls) {
					System.out.println("Waiting for synchronization timed out");
				}
			}
		} catch (InterruptedException e) {
			System.out.println("An interrupt occurred unexpectedly");
		} finally {
			logger.debug("Finished waiting for synchronization");
		}
	}
	
	
	@Override
	public synchronized void startedChangePropagation() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public synchronized void finishedChangePropagation() {
		expectedNumberOfSyncs--;
		logger.debug("Reducing number of expected syncs to: " + expectedNumberOfSyncs);
		this.notifyAll();
	}

	
	@Override
	public synchronized void abortedChangePropagation(ChangePropagationAbortCause cause) {
		expectedNumberOfSyncs--;
		logger.debug("Reducing number of expected syncs to: " + expectedNumberOfSyncs);
		this.notifyAll();
	}
	
	
	
	/*
	private ICompilationUnit createICompilationUnitFromPath(String pathToCompilationUnit, String content, IProject currentProject) throws CoreException {
		String iCompilationUnitName = getNameOfFileFromPath(pathToCompilationUnit) + ".java";
		//Remove Compilation Unit name from path
		String pathToIPackageFragment = pathToCompilationUnit.substring(0, pathToCompilationUnit.lastIndexOf("/"));
		IPackageFragment iPf = findIPackageFragmentFromPath(pathToIPackageFragment, currentProject);
		//TODO: parameter "force": true or false?
		return iPf.createCompilationUnit(iCompilationUnitName, content, true, null);
	}
	
	
	private IPackageFragment findIPackageFragmentFromPath(final String pathToIPackageFragment, IProject currentProject) throws CoreException {
		//For Testing
		//------
		List<String> packageFragmentsNames = new ArrayList<>();
		//------
		final IJavaProject javaProject = JavaCore.create(currentProject);
		for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getPackageFragmentRoots()) {
			final IJavaElement[] children = packageFragmentRoot.getChildren();
			for (final IJavaElement iJavaElement : children) {
				if (iJavaElement instanceof IPackageFragment) {
					final IPackageFragment fragment = (IPackageFragment) iJavaElement;
					String pathToCurrentIPackageFragment = fragment.getPath().makeRelativeTo(javaProject.getPath()).toString();//toOSString();
					//For Testing
					//------
					packageFragmentsNames.add(pathToCurrentIPackageFragment);
					//------
					if (pathToCurrentIPackageFragment.equals(pathToIPackageFragment)) {
						return fragment;
					}
				}
			}
		}
		//If the package fragment does not exist, create a new one
		String tempPath = pathToIPackageFragment.substring(pathToIPackageFragment.indexOf("/") + 1);
		tempPath = tempPath.substring(tempPath.indexOf("/") + 1);
		//IFolder newPackage = currentProject.getFolder(tempPath);
		IFolder srcFolder = currentProject.getFolder("src");
		//newPackage.create(true, true, null);
		IPackageFragmentRoot fragmentRoot = javaProject.getPackageFragmentRoot(srcFolder);//findPackageFragmentRoot(new Path(tempPath));//getPackageFragmentRoot(newPackage)
		String pathDotted = tempPath.replace("/", ".");//StringUtils.join(tempPath, ".");
		return fragmentRoot.createPackageFragment(pathDotted, true, null);
		//fragmentRoot.createPackageFragment(, true, null);
	
		//throw new RuntimeException("Could not find a IPackageFragment with path " + pathToIPackageFragment);
	}
	*/
	

	
}
