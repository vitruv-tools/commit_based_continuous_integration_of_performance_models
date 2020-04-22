package tools.vitruv.applications.pcmjava.integrationFromGit;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.text.edits.TextEdit;
import org.emftext.language.java.containers.Package;

import tools.vitruv.applications.pcmjava.tests.util.CompilationUnitManipulatorHelper;
import tools.vitruv.applications.pcmjava.tests.util.Java2PcmTransformationTest;
import tools.vitruv.applications.pcmjava.tests.util.SynchronizationAwaitCallback;
import tools.vitruv.framework.util.datatypes.VURI;
import tools.vitruv.framework.vsum.VirtualModel;

public class GitChangeApplier implements SynchronizationAwaitCallback {

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
	 * @throws JavaModelException
	 */
	public void applyChangesFromCommit(RevCommit oldCommit, RevCommit newCommit, IProject currentProject) throws JavaModelException {
		
		List<DiffEntry> diffs = gitRepository.computeDiffsBetweenTwoCommits(oldCommit, newCommit, false, true);
		
		for (DiffEntry diff : diffs) {
			
			ICompilationUnit iCu;
			
			switch (diff.getChangeType()) {
			case ADD:
				String pathToAddedFile = diff.getNewPath();
				String fileContent = gitRepository.getNewContentOfFileFromDiffEntry(diff);
				//JGit returns the content of files and uses within the content "\n" as line separator.
				//Therefore, replace all occurrences of "\n" with the system line separator.
				fileContent = gitRepository.replaceAllLineDelimitersWithSystemLineDelimiters(fileContent);
				createICompilationUnitFromPath(pathToAddedFile, fileContent, currentProject);
				break;
			case COPY:
				break;
			case DELETE:
				String nameOfDeletedFile = getNameOfFileFromPath(diff.getOldPath());
				iCu = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName(nameOfDeletedFile, currentProject);
				iCu.delete(true, null);
				break;
			case MODIFY:
				String nameOfChangedFile = getNameOfFileFromPath(diff.getOldPath());
				iCu = CompilationUnitManipulatorHelper.findICompilationUnitWithClassName(nameOfChangedFile, currentProject);
				//EditList from JGit Bib
				EditList editList = gitRepository.computeEditListFromDiffEntry(diff);
				//TextEdit from Eclipse text.edits
				String oldContent = gitRepository.getOldContentOfFileFromDiffEntry(diff);
				String newContent = gitRepository.getNewContentOfFileFromDiffEntry(diff);
				List<TextEdit> textEdits = gitRepository.transformEditListIntoTextEdits(editList, oldContent, newContent);
				CompilationUnitManipulatorHelper.editCompilationUnit(iCu, this, textEdits.toArray(new TextEdit[textEdits.size()]));
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
	
	
	private ICompilationUnit createICompilationUnitFromPath(String pathToCompilationUnit, String content, IProject currentProject) throws JavaModelException {
		String iCompilationUnitName = getNameOfFileFromPath(pathToCompilationUnit) + ".java";
		//Remove Compilation Unit name from path
		String pathToIPackageFragment = pathToCompilationUnit.substring(0, pathToCompilationUnit.lastIndexOf("/"));
		IPackageFragment iPf = findIPackageFragmentFromPath(pathToIPackageFragment, currentProject);
		//TODO: parameter "force": true or false?
		return iPf.createCompilationUnit(iCompilationUnitName, content, true/*false*/, null);
	}
	
	
	private IPackageFragment findIPackageFragmentFromPath(final String pathToIPackageFragment, IProject currentProject) throws JavaModelException {
		//For Testing
		//******
		List<String> packageFragmentsNames = new ArrayList<>();
		//******
		final IJavaProject javaProject = JavaCore.create(currentProject);
		for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getPackageFragmentRoots()) {
			final IJavaElement[] children = packageFragmentRoot.getChildren();
			for (final IJavaElement iJavaElement : children) {
				if (iJavaElement instanceof IPackageFragment) {
					final IPackageFragment fragment = (IPackageFragment) iJavaElement;
					String pathToCurrentIPackageFragment = fragment.getPath().makeRelativeTo(javaProject.getPath()).toString()/*toOSString()*/;
					//For Testing
					//******
					packageFragmentsNames.add(pathToCurrentIPackageFragment);
					//******
					if (pathToCurrentIPackageFragment.equals(pathToIPackageFragment)) {
						return fragment;
					}
				}
			}
		}

		throw new RuntimeException("Could not find a IPackageFragment with path " + pathToIPackageFragment);
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
	
}
