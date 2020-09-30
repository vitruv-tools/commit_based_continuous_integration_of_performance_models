package tools.vitruv.applications.pcmjava.integrationFromGit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IClasspathEntry;
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
import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.applications.pcmjava.tests.util.Java2PcmTransformationTest;
import tools.vitruv.applications.pcmjava.tests.util.SynchronizationAwaitCallback;
import tools.vitruv.framework.util.bridges.EcoreResourceBridge;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.vsum.InternalVirtualModel;
import tools.vitruv.framework.vsum.modelsynchronization.ChangePropagationAbortCause;
import tools.vitruv.framework.vsum.modelsynchronization.ChangePropagationListener;
//import tools.vitruv.domains.java.util.gitchangereplay.extractors.GumTreeChangeExtractor;


/**
 * Class for applying changes contained in git commits on a project. The commits must be contained in {@link #gitRepository}. 
 * The changes are applied on the JDT Model of the given {@link IProject}.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 *
 */
public class GitChangeApplier implements SynchronizationAwaitCallback, ChangePropagationListener {

	private GitRepository gitRepository;
	private static final Logger logger = Logger.getLogger(Java2PcmTransformationTest.class.getSimpleName());
	private static int MAXIMUM_SYNC_WAITING_TIME = 10000;
	private AtomicInteger expectedNumberOfSyncs = new AtomicInteger(0);
	
	
	public GitChangeApplier(GitRepository git) {
		this.gitRepository = git;
	}
	

	/**
	 * Applies changes from commits contained in <code>commits</code> on the given <code>currentProject</code>
	 * 
	 * @param commits - commits that contain changes
	 * @param currentProject - project which the changes are applied on
	 */
	public void applyChangesFromCommits(List<RevCommit> commits, IProject currentProject) {
		Collections.reverse(commits); 
		for (int i = 0; i < commits.size() - 1; i++) {
			applyChangesFromCommit(commits.get(i), commits.get(i + 1), currentProject);
		}
	}

	/**
	 * Applies changes detected between <code>oldCommit</code> and <code>newCommit</code> on the <code>currentProject</code>.
	 * Please note that git does not detect creating of folders if this folders do not contain any files. Therefore we assume 
	 * that a path to a changed file always ends with a point followed by a file extension, for example: ".java" or ".txt"
	 *  
	 * @param oldCommit - commit that contains an older state of the project  
	 * @param newCommit - commit that contains an newer state of the project  
	 * @param currentProject - project which the changes are applied on
	 *
	 */
	public void applyChangesFromCommit(RevCommit oldCommit, RevCommit newCommit, IProject currentProject) {
		
		//Compute changes between two commits
		ArrayList<DiffEntry> diffs = new ArrayList<>(gitRepository.computeDiffsBetweenTwoCommits(oldCommit, newCommit, /*true*/false, true));
		//Sort changes. Necessary to avoid some problems like adding a reference in an existing class to a new class. 
		//In this case, the new class must be created before the reference to it. Thus ADD new class before MODIFY in the existing class.
		diffs = sortDiffs(diffs);

		for (DiffEntry diff : diffs) {
			//Classify changes and call an appropriate routine
			switch (diff.getChangeType()) {
			//Add a new file
			case ADD:
				String pathToAddedFile = diff.getNewPath();
				String fileContent = gitRepository.getNewContentOfFileFromDiffEntry(diff);
				//JGit returns the content of files and uses within the content "\n" as line separator.
				//Therefore, replace all occurrences of "\n" with the system line separator.
				fileContent = gitRepository.replaceAllLineDelimitersWithSystemLineDelimiters(fileContent);
				addElementToProject(currentProject, pathToAddedFile, fileContent);
				break;
			//Copy an existing file
			case COPY:
				copyElementInProject(diff.getOldPath(), diff.getNewPath(), currentProject);
				break;
			//Remove an existing file
			case DELETE:
				removeElementFromProject(currentProject, diff.getOldPath());
				break;
			//Modify an existing file
			case MODIFY:
				OutputStream oldElementContent = gitRepository.getOldContentOfFileFromDiffEntryInOutputStream(diff);
				OutputStream newElementContent = gitRepository.getNewContentOfFileFromDiffEntryInOutputStream(diff);
				String oldElementPath = diff.getOldPath();
				String newElementPath = diff.getNewPath();
				//Compute changed lines in the given file 
				EditList editList = gitRepository.computeEditListFromDiffEntry(diff);
				modifyElementInProject(currentProject, oldElementContent, newElementContent,
						oldElementPath, newElementPath, editList);
				break;
			//Rename an existing file
			case RENAME:
				renameElementInProject(diff.getOldPath(), diff.getNewPath(), currentProject);
				break;
			default:
				//Error
				System.out.println("Changes for the DiffEntry " + diff + "could not be classified");
				break;
			}	
		}
	}

	
	/**
	 * Creates a copy of an existing file with <code>oldPath</code> in <code>newPath</code> in <code>project</code>
	 * 
	 * @param oldPath path to the original file
	 * @param newPath path to the copy
	 * @param project project that contains the original file and its copy
	 * @exception CoreException if this resource could not be copied. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> This resource or one of its descendents is not local.</li>
	 * <li> The source or destination is the workspace root.</li>
	 * <li> The source is a project but the destination is not.</li>
	 * <li> The destination is a project but the source is not.</li>
	 * <li> The resource corresponding to the parent destination path does not exist.</li>
	 * <li> The resource corresponding to the parent destination path is a closed project.</li>
	 * <li> A resource at destination path does exist.</li>
	 * <li> This resource or one of its descendents is out of sync with the local file
	 *      system and <code>force</code> is <code>false</code>.</li>
	 * <li> The workspace and the local file system are out of sync
	 *      at the destination resource or one of its descendents.</li>
	 * <li> The source resource is a file and the destination path specifies a project.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 * @exception OperationCanceledException if the operation is canceled.
	 * Cancellation can occur even if no progress monitor is provided.
	 */
	private void copyElementInProject(String oldPath, String newPath, IProject project) {
		//Convert old path from String into Path. For convenience only.
		IPath tempOldPath = new Path(oldPath);
		//Get rid of the project name in the path
		tempOldPath = tempOldPath.removeFirstSegments(1);
		
		//Convert new path from String into Path. For convenience only.
		IPath tempNewPath = new Path(newPath);
		//Get rid of the project name in the path
		tempNewPath = tempNewPath.removeFirstSegments(1);
		
		IFile originalFile = project.getFile(tempOldPath);
		if (originalFile.exists()) {		
			IFile copiedFile = project.getFile(tempNewPath);
			if (!copiedFile.exists()) {
				try {
					originalFile.copy(copiedFile.getFullPath(), true, new NullProgressMonitor());
				} catch (CoreException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}	
			}	
		}
	}


	/**
	 * Sorts changes <code>DiffEntry</code> in a particular order:
	 * [COPY,...,RENAME,...,DELETE,...,ADD,...,MODIFY,...]
	 * 
	 * 
	 * @param diffs unsorted changes
	 * @return <code>ArrayList</code>
	 */
	private ArrayList<DiffEntry> sortDiffs(ArrayList<DiffEntry> diffs) {
		
		//temp lists for all diff types
		ArrayList<DiffEntry> copies = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> renames = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> deletes = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> adds = new ArrayList<DiffEntry>();
		ArrayList<DiffEntry> modifies = new ArrayList<DiffEntry>();
		
		for(DiffEntry diff : diffs) {
			switch (diff.getChangeType()) {
			case COPY:
				copies.add(diff);
				break;
			case RENAME:
				renames.add(diff);
				break;
			case DELETE:
				deletes.add(diff);
				break;
			case ADD:
				adds.add(diff);
				break;
			case MODIFY:
				modifies.add(diff);
				break;
			}
		}
		ArrayList<DiffEntry> result = new ArrayList<DiffEntry>();
		
		result.addAll(copies);
		result.addAll(renames);
		result.addAll(deletes);
		result.addAll(adds);
		result.addAll(modifies);
		
		return result;
	}


	/**
	 * Computes the name (including the file extension) of the file from the given path to this file
	 * 
	 * @param path path to the file
	 * @return file name
	 */
	public String getNameOfFileFromPath(String path) {
		return  path.substring(path.lastIndexOf("/") + 1);
	}
	
	
	/**
	 * Apples changes on the give file. If the file is a java file, only the changed lines will be replaced. 
	 * For all other file types the entire old content will be replaced with the <code>newContent</code>. 
	 * 
	 * @param project
	 * @param oldElementContent
	 * @param newElementContent
	 * @param oldElementPath
	 * @param newElementPath
	 * @param editList 
 	 *
 	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> The corresponding location in the local file system
	 *       is occupied by a directory.</li>
	 * <li> The workspace is not in sync with the corresponding location
	 *       in the local file system and <code>FORCE</code> is not specified.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * <li> The file modification validator disallowed the change.</li>
	 * </ul>
	 * 
	 * @exception JavaModelException a failure in the Java model. See: 
	 * 	{@link ICompilationUnit#becomeWorkingCopy}
	 * 	{@link ICompilationUnit#applyTextEdit}
	 * 	{@link ICompilationUnit#reconcile}
	 * 	{@link ICompilationUnit#commitWorkingCopy}
	 * 	{@link ICompilationUnit#discardWorkingCopy}
	 * 
	 * @exception  IOException  if an I/O error occurs.
	 */
	private void modifyElementInProject(IProject project,
			OutputStream oldElementContent, OutputStream newElementContent, 
			String oldElementPath, String newElementPath,
			EditList editList) {
		
		//Check if the modified file is a java file
		if (oldElementPath.endsWith(".java")) {
			//Find the compilation unit
			ICompilationUnit compilationUnit = findICompilationUnitInProject(oldElementPath, project);
			String oldContent = oldElementContent.toString();
			String newContent = newElementContent.toString();
			try {
				oldElementContent.close();
				newElementContent.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			//Convert EditList into List<TextEdit>. Necessary because EditList is from JGit (see: org.eclipse.jgit.diff.EditList),
			//but changes must be applied on a JDT Model, what is only possible with TextEdit (see: org.eclipse.jdt.core.ICompilationUnit.applyTextEdit(TextEdit edit, IProgressMonitor monitor))	
			List<TextEdit> textEdits = gitRepository.transformEditListIntoTextEdits(editList, oldContent, newContent);
			//Apply changes on the given compilation unit
			try {
				CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, this, textEdits.toArray(new TextEdit[textEdits.size()]));
			} catch (JavaModelException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		//Find the non-java file and replace its entire content with the new one
		else {
			try {
				oldElementContent.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			IFile file = project.getFile(oldElementPath.substring(oldElementPath.indexOf("/") + 1));
			if (file.exists()) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) newElementContent).toByteArray());
				try {
					file.setContents(inputStream, IFile.FORCE, null);
				} catch (CoreException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				try {
					newElementContent.close();
					//inputStream.close(); //has no effect
				} catch (IOException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}	
		}
		
	}
	
	
	/**
	 * Create a new file in the <code>project</code>.
	 * The <code>pathToElement</code> contains the path to the new file, but some folders on the path to the new file
	 * may not exist yet. The non-existing parent folders will also be created.
	 * 
	 * @param project project which the new file or folder will be created in
	 * @param pathToElement path to the new file
	 * @param elementContent file content
	 * 
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul> 
	 * 
	 * 	 * @exception JavaModelException a failure in the Java model. See: 
	 * 	{@link ICompilationUnit#becomeWorkingCopy}
	 * 	{@link ICompilationUnit#applyTextEdit}
	 * 	{@link ICompilationUnit#reconcile}
	 * 	{@link ICompilationUnit#commitWorkingCopy}
	 * 	{@link ICompilationUnit#discardWorkingCopy}
	 * 
	 */
	private void addElementToProject(IProject project, String pathToElement, String elementContent) {
		//Refresh the project to avoid inconsistency
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		//Convert path from String into Path. For convenience only.
		IPath tempPath = new Path(pathToElement);
		//Get rid of the project name in the path
		tempPath = tempPath.removeFirstSegments(1);
		//Get number of segments in the path
		int segmentCounter = tempPath.segmentCount();
		//Check if the path to the file is valid
		if (segmentCounter == 0) {
			System.out.println("Path: " + pathToElement + " is not valid");
			return;
		}
		//Check if the file to be created is a java file
		if (!pathToElement.endsWith(".java")) {
			createNonJavaFile(tempPath, elementContent, project);
		}
		else {
			String firstSegment = tempPath.segment(0);
			String lastSegment = tempPath.lastSegment();
			
			IJavaProject javaProject = null;
			IPackageFragmentRoot packageFragmentRoot = null;
			IPackageFragment packageFragment = null;
			
			//Check if we need to handle a IJavaProject
			//For more details, what a java project in JDT looks like, see https://www.vogella.com/tutorials/EclipseJDT/article.html
			//if (firstSegment.equals("src") || firstSegment.equals("bin") 
			//		|| fileExtension.equals("jar") || fileExtension.equals("zip")) {
			
			//Check if the project is a java project
			boolean javaNature = false;
			try {
				javaNature = project.hasNature(JavaCore.NATURE_ID);
			} catch (CoreException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			if (javaNature) {
				javaProject = JavaCore.create(project);
				//A Java File must not be directly in the src folder. Instead it must be in a package, that is contained in the src folder.
				//Therefore, a path to a new java file must contain at least three segments.
				if (segmentCounter > 2) {
					//Find the package fragment root. If it does not exist, create it
					IPath packageFragmentRootPath = javaProject.getPath().append("/" + firstSegment);
					try {
						packageFragmentRoot = javaProject.findPackageFragmentRoot(packageFragmentRootPath);
					} catch (JavaModelException e) {
						System.err.println(e.getMessage());
						e.printStackTrace();
					}//javaProject.getPackageFragmentRoot(firstSegment);
					if (!packageFragmentRoot.exists()) {
						packageFragmentRoot = createPacakgeFragmentRoot(firstSegment, javaProject);
					}
					//Find PackageFragment
					String packageFragmentName = tempPath.removeFirstSegments(1).removeLastSegments(1).toString().replace("/", ".");
					packageFragment = packageFragmentRoot.getPackageFragment(packageFragmentName);
					//Check if the PackageFragment exists. If not, create it
					if (!packageFragment.exists()) {
						createPackageWithPackageInfo(project, packageFragmentRoot, tempPath.removeFirstSegments(1).removeLastSegments(1).segments());
					}
					//packageFragment.makeConsistent(new NullProgressMonitor());
					ICompilationUnit compilationUnit = packageFragment.getCompilationUnit(lastSegment);
					//The new file must not exist yet.
					if (!compilationUnit.exists()) {
						//Create java file per JDT
						try {
							compilationUnit = packageFragment.createCompilationUnit(lastSegment, "", false/*true*/, new NullProgressMonitor());
						} catch (JavaModelException e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						//Thread.sleep(5000);
						//Set empty content on the new file. Necessary to inform Vitruv about the new created java file.
						InsertEdit edit = new InsertEdit(0, elementContent);
						try {
							CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, this, edit);
						} catch (JavaModelException e) {
							System.err.println(e.getMessage());
							e.printStackTrace();
						}
						//project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					}
					else {
						System.out.println("The compilation unit with name " + lastSegment + " in package fragment "+ packageFragmentName + " already existed and therefore could not be created");
					}
				}
				else {
					System.out.println("The java file " + lastSegment + " could not be created because it's not in a Package Fragment");
				}
			}
		}
	}
	
	
	
	/**
	 * Creates a {@link IPackageFragmentRoot} with <code>packageFragmentRootPath</code> in <code>javaProject</code>
	 * 
	 * @param packageFragmentRootPath path to the package fragment root
	 * @param javaProject project which the package fragment root must be created in
	 * @return created package fragment root
	 * 
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource already exists in the workspace.</li>
	 * <li> The workspace contains a resource of a different type
	 *      at the same path as this resource.</li>
	 * <li> The parent of this resource does not exist.</li>
	 * <li> The parent of this resource is a project that is not open.</li>
	 * <li> The parent contains a resource of a different type
	 *      at the same path as this resource.</li>
	 * <li> The parent of this resource is virtual, but this resource is not.</li>
	 * <li> The name of this resource is not valid (according to
	 *    <code>IWorkspace.validateName</code>).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a file (as opposed to a directory).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a folder and <code>FORCE</code> is not specified.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification.  See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 * 
	 * @exception JavaModelException if this element does not exist or if an
	 *	exception occurs while accessing its corresponding resource
	 */
	private IPackageFragmentRoot createPacakgeFragmentRoot(String packageFragmentRootPath, IJavaProject javaProject) {
		IFolder rootFolder = javaProject.getProject().getFolder(packageFragmentRootPath);
		//Check if the PackageFragmentRoot exists. If not, create it
		if (!rootFolder.exists()) {
			try {
				rootFolder.create(false/*true*/, false/*true*/, new NullProgressMonitor());
			} catch (CoreException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}		
		}

		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(rootFolder);
		
		IClasspathEntry[] oldEntries = null;
		try {
			oldEntries = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
		try {
			javaProject.setRawClasspath(newEntries, null);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return root;
	}
	
	
	
	/**
	 * Creates a non-java {@link IFile} with <code>elementContent</code> in <code>project</code>.
	 * If there are file's parents folders that not exist yet, they will also be created.
	 * 
	 * @param pathToFile path to the file
	 * @param elementContent file content 
	 * @param project project which the file must be created in
	 
	 * @exception CoreException if this method fails. Reasons include:
	 * <ul>
	 * <li> This resource already exists in the workspace.</li>
	 * <li> The parent of this resource does not exist.</li>
	 * <li> The parent of this resource is a virtual folder.</li>
	 * <li> The project of this resource is not accessible.</li>
	 * <li> The parent contains a resource of a different type
	 *      at the same path as this resource.</li>
	 * <li> The name of this resource is not valid (according to
	 *    <code>IWorkspace.validateName</code>).</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a directory.</li>
	 * <li> The corresponding location in the local file system is occupied
	 *    by a file and <code>force </code> is <code>false</code>.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 */
	private void createNonJavaFile(IPath pathToFile, String elementContent, IProject project) {
		int segmentCounter = pathToFile.segmentCount();
		if(segmentCounter < 1) {
			System.out.println("Could not create a new file because of an invalid file path");
			return;
		}
		//Check if the file has no parent folders
		if (pathToFile.segmentCount() > 1) {
			//The file has parent folders. Create all parent folders if they are not exist yet
			for(int i = segmentCounter - 1; i > 0; i--) {
				IFolder folder = project.getFolder(pathToFile.removeLastSegments(i));
				if (!folder.exists()) {
					try {
						folder.create(false, true, new NullProgressMonitor());
					} catch (CoreException e) {
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		//Create the file if it is not exist yet
		IFile file = project.getFile(pathToFile);
		if (!file.exists()) {
			ByteArrayInputStream elementContentStream = new ByteArrayInputStream(elementContent.getBytes());
			try {
				file.create(elementContentStream, false, new NullProgressMonitor());
				//elementContentStream.close(); //has no effect
			} catch (CoreException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Renames an existing {@link IFile} in the <code>project</code>
	 * 
	 * @param oldPath path to the file (including file name) before renaming
	 * @param newPath path to the file (including file name) after renaming
	 * @param project project which the file must be renamed in
	 *
	 * @exception CoreException if this resource could not be moved. Reasons include:
	 * <ul>
	 * <li> This resource does not exist.</li>
	 * <li> This resource or one of its descendents is not local.</li>
	 * <li> The source or destination is the workspace root.</li>
	 * <li> The source is a project but the destination is not.</li>
	 * <li> The destination is a project but the source is not.</li>
	 * <li> The resource corresponding to the parent destination path does not exist.</li>
	 * <li> The resource corresponding to the parent destination path is a closed
	 *      project.</li>
	 * <li> The source is a linked resource, but the destination is not a project
	 *      and  {@link #SHALLOW} is specified.</li>
	 * <li> A resource at destination path does exist.</li>
	 * <li> A resource of a different type exists at the destination path.</li>
	 * <li> This resource or one of its descendents is out of sync with the local file system
	 *      and <code>force</code> is <code>false</code>.</li>
	 * <li> The workspace and the local file system are out of sync
	 *      at the destination resource or one of its descendents.</li>
	 * <li> The source resource is a file and the destination path specifies a project.</li>
	 * <li> The location of the source resource on disk is the same or a prefix of
	 * the location of the destination resource on disk.</li>
	 * <li> Resource changes are disallowed during certain types of resource change
	 * event notification. See <code>IResourceChangeEvent</code> for more details.</li>
	 * </ul>
	 */
	private void renameElementInProject(String oldPath, String newPath, IProject project) {
		//Convert old path from String into Path. For convenience only.
		IPath tempOldPath = new Path(oldPath);
		//Get rid of the project name in the path
		tempOldPath = tempOldPath.removeFirstSegments(1);
		
		//Convert new path from String into Path. For convenience only.
		IPath tempNewPath = new Path(newPath);
		//Get rid of the project name in the path
		tempNewPath = tempNewPath.removeFirstSegments(1);
		
		IFile fileBeforeRename = project.getFile(tempOldPath);
		if (fileBeforeRename.exists()) {		
			IFile fileAfterRename = project.getFile(tempNewPath);
			if (!fileAfterRename.exists()) {
				try {
					fileBeforeRename.move(fileAfterRename.getFullPath()/*tempNewPath*/, true, new NullProgressMonitor());
				} catch (CoreException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}	
			}	
		}
	}
	
	
	/**
	 * Removes file or folder from the given <code>project</code>
	 * 
	 * @param project given project which the file or folder must be removed from
	 * @param pathToElement path to the file or folder which must be removed
	 *
	 * @exception JavaModelException if this element could not be deleted. Reasons include:
	 * <ul>
	 * <li> This Java element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while updating an underlying resource (CORE_EXCEPTION)</li>
	 * <li> This element is read-only (READ_ONLY)</li>
	 * </ul>
	 */
	private void removeElementFromProject(IProject project, String pathToElement) {
		//Determine if a package, a compilation unit or a non-java file must be removed
		if (pathToElement.endsWith("package-info.java")) {
			//Do we need to delete package-info.java explicitly before we delete package?
			//ICompilationUnit compilationUnit = findICompilationUnitInProject(pathToElement, project);
			//compilationUnit.delete(true, null);
			//Find and delete the package
			IPackageFragment packageFragment = findIPackageFragmentInProject(pathToElement, project);
			try {
				packageFragment.delete(true, null);
			} catch (JavaModelException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		else if (pathToElement.endsWith(".java")) {
			ICompilationUnit compilationUnit = findICompilationUnitInProject(pathToElement, project);
			try {
				compilationUnit.delete(true/*false*/, null);
			} catch (JavaModelException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		else {
			IPath pathToElementInProject = new Path(pathToElement).removeFirstSegments(1);
			IFile fileToRemove = project.getFile(pathToElementInProject);
			if (fileToRemove.exists()) {
				try {
					fileToRemove.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}	
			}
		}

	}
	
	
	/**
	 * Creates a new package with a containing file 'package-info.java' in <code>project</code> 
	 * 
	 * @param project project which a new package must be created in
	 * @param namespace package name space
	 * @return JaMoPP Model of the created package
	 *
	 * @exception IOException if an error occurred during saving
	 */
	protected Package createPackageWithPackageInfo(final IProject project, final IPackageFragmentRoot packageFragmentRoot, final String... namespace) {
		assert (namespace.length > 0);
		Package jaMoPPPackage = null;
		//Check if there are parent subpackages on the path, which don't exist yet. If so, create them before creating the target package.
		String parentPackage = namespace[0];
		for (int i = 0; i < namespace.length; i++) {
			if (!packageFragmentRoot.getPackageFragment(parentPackage).exists()) {
				String currentPackage = parentPackage.replace(".", "/") + "/package-info.java";
				jaMoPPPackage = ContainersFactory.eINSTANCE.createPackage();
				List<String> namespaceList = Arrays.asList(namespace);
				jaMoPPPackage.setName(namespaceList.get(namespaceList.size() - 1));
				jaMoPPPackage.getNamespaces().addAll(namespaceList.subList(0, namespaceList.size() - 1));
				
				ResourceSet resourceSet = new ResourceSetImpl();
				VURI vuri = VURI.getInstance(project.getName() + "/src/" + currentPackage);
				final Resource resource = resourceSet.createResource(vuri.getEMFUri());
				
				try {
					EcoreResourceBridge.saveEObjectAsOnlyContent(jaMoPPPackage, resource);
				} catch (IOException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				waitForSynchronization(1);
			}
			
			parentPackage = i + 1 < namespace.length ? parentPackage + "." + namespace[i + 1] : parentPackage  ;
		}
		
		return jaMoPPPackage;
		
		/* TODO delete
		String packageFile = StringUtils.join(namespace, "/");
		packageFile = packageFile + "/package-info.java";
		Package jaMoPPPackage = ContainersFactory.eINSTANCE.createPackage();
		List<String> namespaceList = Arrays.asList(namespace);
		jaMoPPPackage.setName(namespaceList.get(namespaceList.size() - 1));
		jaMoPPPackage.getNamespaces().addAll(namespaceList.subList(0, namespaceList.size() - 1));
		
		ResourceSet resourceSet = new ResourceSetImpl();
		VURI vuri = VURI.getInstance(project.getName() + "/src/" + packageFile);
		final Resource resource = resourceSet.createResource(vuri.getEMFUri());
		
		EcoreResourceBridge.saveEObjectAsOnlyContent(jaMoPPPackage, resource);
		waitForSynchronization(1);
		return jaMoPPPackage;
		*/
	}
	
	
	/**
	 * Computes a {@link VURI} for the path to the file <code>elementName</code>.
	 * 
	 * @param packageFragment package that contains file <code>elementName</code>
	 * @param elementName file
	 * @return computed {@link VURI}
	 */
	public VURI getVURIForElementInPackage(final IPackageFragment packageFragment, final String elementName) {
		String vuriKey = packageFragment.getResource().getFullPath().toString() + "/" + elementName;
		if (vuriKey.startsWith("/")) {
			vuriKey = vuriKey.substring(1, vuriKey.length());
		}
		final VURI vuri = VURI.getInstance(vuriKey);
		return vuri;
	}
	
	/**
	 * Returns a JaMoPP {@link ConcreteClassifier} for the given {@link VURI}
	 * 
	 * @param vuri given {@link VURI}
	 * @return found {@link ConcreteClassifier}
	 */
	protected ConcreteClassifier getJaMoPPClassifierForVURI(final VURI vuri) {
		final CompilationUnit cu = this.getJaMoPPRootForVURI(vuri);
		final Classifier jaMoPPClassifier = cu.getClassifiers().get(0);
		return (ConcreteClassifier) jaMoPPClassifier;
	}

	/**
	 * Returns a JaMoPP {@link JavaRoot} for the given {@link VURI}
	 * 
	 * @param <T>
	 * @param vuri given {@link VURI}
	 * @return found {@link JavaRoot}
	 */
	private <T extends JavaRoot> T getJaMoPPRootForVURI(final VURI vuri) {
		final Resource resource = URIUtil.loadResourceAtURI(vuri.getEMFUri(), new ResourceSetImpl());
		// unchecked is OK for the test.
		@SuppressWarnings("unchecked")
		final T javaRoot = (T) resource.getContents().get(0);
		return javaRoot;
	}

	
	

    /**
     * Finds a {@link ICompilationUnit} in the given project <code>project</code> based on <code>pathToCompilationUnit</code>
     * 
     * @param pathToCompilationUnit path to the compilation unit
     * @param project given project
     * @return found {@link ICompilationUnit}
     * 
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
     */
    public static ICompilationUnit findICompilationUnitInProject(String pathToCompilationUnit,
            final IProject project) {
    	//Convert path to the compilation unit from Sting to Path
    	IPath path = new Path(pathToCompilationUnit);
    	//Get rid of the project name
    	path = path.removeFirstSegments(1);
    	//Determine names for the package fragment root, package fragment and compilation unit
    	String packageFragmentRootName = path.segment(0).replace("/", ".");
    	String packageFragmentName = path.removeFirstSegments(1).removeLastSegments(1).toString().replace("/", ".");
    	String compilationUnitName = path.lastSegment().replace("/", ".");
    	
        final IJavaProject javaProject = JavaCore.create(project);
        //Iterate over all package fragment roots
        try {
			for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getElementName().equals(packageFragmentRootName)) {
					final IJavaElement[] children = packageFragmentRoot.getChildren();
			        //Iterate over all package fragments
					for (final IJavaElement iJavaElement : children) {
			            if (iJavaElement instanceof IPackageFragment && iJavaElement.getElementName().equals(packageFragmentName)) {
			                final IPackageFragment fragment = (IPackageFragment) iJavaElement;
			                final IJavaElement[] javaElements = fragment.getChildren();
			                //Iterate over all compilation units
			                for (int k = 0; k < javaElements.length; k++) {
			                    final IJavaElement javaElement = javaElements[k];
			                    if (javaElement.getElementType() == IJavaElement.COMPILATION_UNIT && javaElement.getElementName().equals(compilationUnitName)) {
			                        final ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
			                            return compilationUnit;
			                    }
			                }
			            }
			        }
				} 
			}
		} catch (JavaModelException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        throw new RuntimeException("Could not find a compilation unit with name " + pathToCompilationUnit);
    }
    
    /**
     * Finds a {@link IPackageFragment} in the given project <code>project</code> based on <code>pathToPackageFragment</code>
     * 
     * @param pathToPackageFragment path to the package fragment
     * @param project given project
     * @return found {@link IPackageFragment}
     *
     * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
     */
    public static IPackageFragment findIPackageFragmentInProject(String pathToPackageFragment,
            final IProject project){
    	//Convert path to the compilation unit from Sting to Path
    	IPath path = new Path(pathToPackageFragment);
    	//Get rid of the project name
    	path = path.removeFirstSegments(1);
    	//Determine names for the package fragment root and package fragment
    	String packageFragmentRootName = path.segment(0).replace("/", ".");
    	String packageFragmentName = path.removeFirstSegments(1).removeLastSegments(1).toString().replace("/", ".");
    	
        final IJavaProject javaProject = JavaCore.create(project);
        //Iterate over all package fragment roots
        try {
			for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getPackageFragmentRoots()) {
				if (packageFragmentRoot.getElementName().equals(packageFragmentRootName)) {
					final IJavaElement[] children = packageFragmentRoot.getChildren();
					//Iterate over all package fragments
					for (final IJavaElement iJavaElement : children) {
			            if (iJavaElement instanceof IPackageFragment && iJavaElement.getElementName().equals(packageFragmentName)) {
			                final IPackageFragment fragment = (IPackageFragment) iJavaElement;
			                return fragment;
			            }
			        }
				} 
			}
		} catch (JavaModelException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        throw new RuntimeException("Could not find a compilation unit with name " + pathToPackageFragment);
    }
	
	
	/**
	 * Waits for Vitruv reaction on changes. This method is used for synchronization when a {@link ICompilationUnit} is changed.
	 * See {@link CompilationUnitManipulatorHelper#editCompilationUnit(ICompilationUnit, SynchronizationAwaitCallback, TextEdit...)}
	 *
	 * @param numberOfExpectedSynchronizationCalls usually equals 1
	 */
	@Override
	public synchronized void waitForSynchronization(int numberOfExpectedSynchronizationCalls) {
		expectedNumberOfSyncs.addAndGet(numberOfExpectedSynchronizationCalls);
		logger.debug("Starting to wait for finished synchronization. Expected syncs: "
				+ numberOfExpectedSynchronizationCalls + ", remaining syncs: " + expectedNumberOfSyncs);
		try {
			int wakeups = 0;
			while (expectedNumberOfSyncs.get() > 0) {
				wait(MAXIMUM_SYNC_WAITING_TIME);
				wakeups++;
				// If we had more wakeups than expected sync calls, we had a
				// timeout
				// and so the synchronization was not finished as expected
				if (wakeups > numberOfExpectedSynchronizationCalls) {
					System.out.println("Waiting for synchronization timed out. Continue the programm anyway.");
					expectedNumberOfSyncs.addAndGet(-numberOfExpectedSynchronizationCalls);
					break;
				}
				System.out.println("Waiting for synchronization timed out. Please check if there is an opened user dialog.\nTry to wait one more time");
			}
		} catch (InterruptedException e) {
			System.out.println("An interrupt occurred unexpectedly");
		} finally {
			logger.debug("Finished waiting for synchronization");
		}
	}
	
	
	@Override
	public synchronized void startedChangePropagation() {
		// Not needed yet
	}
	
	
	/**
	 * Notifies all waiting threads when change propagation is done.
	 * This method is called from Vitruv.
	 *
	 */
	@Override
	public synchronized void finishedChangePropagation() {
		expectedNumberOfSyncs.decrementAndGet();
		logger.debug("Reducing number of expected syncs to: " + expectedNumberOfSyncs);
		this.notifyAll();
	}

	
	/**
	 * Notifies all waiting threads when change propagation is aborted.
	 * This method is called from Vitruv.
	 */
	@Override
	public synchronized void abortedChangePropagation(ChangePropagationAbortCause cause) {
		expectedNumberOfSyncs.decrementAndGet();
		logger.debug("Reducing number of expected syncs to: " + expectedNumberOfSyncs);
		this.notifyAll();
	}

	
	/**
	 * Updates the project state in Virtual Model using State Based Propagation Strategy 
	 * 
	 * @param newProjectState
	 * @param oldProjectState
	 * @param newCompilationUnitState
	 * @param virtualModel
	 */
	public void applyChangesFromCommitUsingStateBasedChangePropagation(IProject newProjectState, IProject oldProjectState, ICompilationUnit newCompilationUnitState, InternalVirtualModel virtualModel) {
		VURI vuriNew = VURI.getInstance(newProjectState);
		final Resource resourceNew = URIUtil.loadResourceAtURI(URI.createPlatformResourceURI(newProjectState.getLocationURI().toString())/*vuri.getEMFUri()*//*newProjectState.getLocationURI()*/, new ResourceSetImpl());
		
		VURI vuriOld = VURI.getInstance(oldProjectState);
		final Resource resourceOld = URIUtil.loadResourceAtURI(URI.createPlatformResourceURI(oldProjectState.getLocationURI().toString())/*vuri.getEMFUri()*//*newProjectState.getLocationURI()*/, new ResourceSetImpl());
		
		VURI vuriCompilationUnitOld = VURI.getInstance(newCompilationUnitState.getResource());
		final Resource resourceCompilationUnitOld = URIUtil.loadResourceAtURI(vuriCompilationUnitOld.getEMFUri(), new ResourceSetImpl());
		
		virtualModel.propagateChangedState(resourceCompilationUnitOld);
		
	}
	
	
//***************************************************************************************************************************
//***************************************************************************************************************************
//***************************************************************************************************************************

	
	
	

	//TODO: Delete method applyChangesFromCommitWithGumTree(...)
	/**
	 * @param oldCommit
	 * @param newCommit
	 * @param currentProject
	 * @throws CoreException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
/*	public void applyChangesFromCommitWithGumTree (RevCommit oldCommit, RevCommit newCommit, IProject currentProject) throws CoreException, InterruptedException, IOException {
		
		ArrayList<DiffEntry> diffs = new ArrayList<>(gitRepository.computeDiffsBetweenTwoCommits(oldCommit, newCommit, false, true));
		diffs = sortDiffs(diffs);
		//Collections.reverse(diffs); 

		for (DiffEntry diff : diffs) {
			
			ICompilationUnit iCu;
			
			switch (diff.getChangeType()) {
			case ADD:
				String pathToAddedFile = diff.getNewPath();
				String fileContent = gitRepository.getNewContentOfFileFromDiffEntry(diff);
				//JGit returns the content of files and uses within the content "\n" as line separator.
				//Therefore, replace all occurrences of "\n" with the system line separator.
				fileContent = gitRepository.replaceAllLineDelimitersWithSystemLineDelimiters(fileContent);
				addElementToProjectWithGumTree(currentProject, pathToAddedFile, fileContent);
				break;
			case COPY:
				
				break;
			case DELETE:
				
				iCu = findICompilationUnitInProject(diff.getOldPath(), currentProject);
				//deleteCompilationUnit(iCu, this);
				
				iCu.delete(true, null);
				
				//EcoreUtil.delete(iCu.);
				//waitForSynchronization(1);
				//Thread.sleep(20000);
				break;
			case MODIFY:
				OutputStream oldElementContent = gitRepository.getOldContentOfFileFromDiffEntryInOutputStream(diff);
				OutputStream newElementContent = gitRepository.getNewContentOfFileFromDiffEntryInOutputStream(diff);
				String oldElementPath = diff.getOldPath();
				String newElementPath = diff.getNewPath();
				EditList editList = gitRepository.computeEditListFromDiffEntry(diff);
				modifyElementInProjectWithGumTree(currentProject, oldElementContent, newElementContent,
						oldElementPath, newElementPath, editList);
				break;
			case RENAME:
				
				break;
			default:
				//error
				break;
			}	
		}
	}
	
*/
/*	
	//TODO: Delete method modifyElementInProjectWithGumTree(...)
	private void modifyElementInProjectWithGumTree(IProject project,
			OutputStream oldElementContent, OutputStream newElementContent, 
			String oldElementPath, String newElementPath,
			EditList editList) throws CoreException {
		
		//Check if the modified file is a java file
		if (oldElementPath.endsWith(".java")) {	
			ICompilationUnit compilationUnit = findICompilationUnitInProject(oldElementPath, project);
			//EditList from JGit Bib
			//TextEdit from Eclipse text.edits
			String oldContent = oldElementContent.toString();
			//new String(((ByteArrayOutputStream) oldElementContent).toByteArray());
			String newContent = newElementContent.toString();
			URI uri = URI.createPlatformResourceURI(compilationUnit.getPath().toString(), true);
			GumTreeChangeExtractor atomicChangeExtractor = new GumTreeChangeExtractor(oldContent, newContent, uri);
			List<String> contentStatesAfterApplyingAtomicChanges = atomicChangeExtractor.extract();
			contentStatesAfterApplyingAtomicChanges.remove(0);
			int currentContentLength = compilationUnit.getBuffer().getLength(); 
					//oldContent.length();
			for (String contentState : contentStatesAfterApplyingAtomicChanges) {
				TextEdit replaceEdit = new ReplaceEdit(0, currentContentLength, contentState);
				CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, this, replaceEdit);
				currentContentLength = compilationUnit.getBuffer().getLength(); 
						//contentState.length();
			}
		}
		else {
			IFile file = project.getFile(oldElementPath.substring(oldElementPath.indexOf("/") + 1));
			if (file.exists()) {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(((ByteArrayOutputStream) newElementContent).toByteArray());
				file.setContents(inputStream, IFile.FORCE, null);
			}	
		}
	}
*/
/*	
	//TODO: Delete method addElementToProjectWithGumTree(...)
	private void addElementToProjectWithGumTree(IProject project, String pathToElement, String elementContent) throws CoreException, InterruptedException, IOException {
			
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
					file.create(new ByteArrayInputStream(elementContent.getBytes()), false , new NullProgressMonitor());
					System.out.println("Begin to wait for syncronization at the place 0");
					//waitForSynchronization(1);
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				}
				else {
					//Check if the PackageFragmentRoot exists. If not, create it
					IFolder packageFragmentRootFolder = project.getFolder(tempPath.segment(0));
					
					if (!packageFragmentRootFolder.exists()) {
						packageFragmentRootFolder.create(false, false, new NullProgressMonitor());
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
						//packageFragment = packageFragmentRoot.createPackageFragment(packageFragmentName, false , new NullProgressMonitor());
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
							compilationUnit = packageFragment.createCompilationUnit(lastSegment, "", false, new NullProgressMonitor());
							Thread.sleep(5000);
							
							//EditList from JGit Bib
							//TextEdit from Eclipse text.edits
							String oldContent = "";
							//new String(((ByteArrayOutputStream) oldElementContent).toByteArray());
							String newContent = elementContent;
							URI uri = URI.createPlatformResourceURI(compilationUnit.getPath().toString(), true);
							GumTreeChangeExtractor atomicChangeExtractor = new GumTreeChangeExtractor(oldContent, newContent, uri);
							List<String> contentStatesAfterApplyingAtomicChanges = atomicChangeExtractor.extract();
							contentStatesAfterApplyingAtomicChanges.remove(0);
							int currentContentLength = compilationUnit.getBuffer().getLength();
									//oldContent.length();
							for (String contentState : contentStatesAfterApplyingAtomicChanges) {
								TextEdit replaceEdit = new ReplaceEdit(0, currentContentLength, contentState);
								CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, this, replaceEdit);
								currentContentLength = compilationUnit.getBuffer().getLength();
										//contentState.length();
							}
							
							
							//InsertEdit edit = new InsertEdit(0, elementContent);
							//CompilationUnitManipulatorHelper.editCompilationUnit(compilationUnit, this, edit);
							
							//VURI vuri = getVURIForElementInPackage(packageFragment, compilationUnit.getElementName());
							//getJaMoPPClassifierForVURI(vuri);
							
							//System.out.println("Begin to wait for syncronization at the place 3");
							//waitForSynchronization(1);
							project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						}
					}
					else {
						IFile file = project.getFile(tempPath.toString());
						if (!file.exists()) {
							file.create(new ByteArrayInputStream(elementContent.getBytes()), false , new NullProgressMonitor());
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
					file.create(new ByteArrayInputStream(elementContent.getBytes()), false , new NullProgressMonitor());	
					System.out.println("Begin to wait for syncronization at the place 5");
					//waitForSynchronization(1);
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				}
			}
		}
*/
    	
}
