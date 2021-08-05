package cipm.consistency.commitintegration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.NullOutputStream;

import cipm.consistency.tools.evaluation.data.EvaluationDataContainer;

/**
 * Wraps and represents a Git repository.
 * 
 * @author Ilia Chupakhin
 * @author Manar Mazkatli (advisor)
 * @author Martin Armbruster
 */
public class GitRepositoryWrapper {
	private Git git;
	private RevCommit latestCommit;
	private File rootDirectory;
	private String defaultBranch;
	
	/**
	 * Creates a new Git repository wrapper with a local copy in <code>rootDirectory</code>.
	 * 
	 * @param rootDirectory local directory for a copy of the Git repository.
	 */
	public GitRepositoryWrapper(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	/**
	 * Initializes the Git repository from the given local directory.
	 * 
	 * @throws IOException if the given local directory does not contain a Git repository.
	 * @throws GitAPIException if the result cannot be computed.
	 * @throws NoHeadException if the repository has no head.
	 */
	public void initFromRootDirectory() throws IOException, NoHeadException, GitAPIException {
		this.git = Git.open(this.rootDirectory);
		readInitialState();
	}
	
	/**
	 * Clones an existing remote Git repository into the given local repository.
	 * 
	 * @param uriToRemoteRepository URI to the remote repository.
     * @throws GitAPIException if unable to compute a result.
     * @throws InvalidRemoteException thrown when the remote repository is invalid.
     * @throws TransportException thrown when the transport operation failed.
	 * @throws IOException if the cloned repository cannot be read.
	 */
	public void initFromRemoteRepository(String uriToRemoteRepository)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		this.git = Git.cloneRepository().setURI(uriToRemoteRepository)
		  .setDirectory(rootDirectory).setCloneAllBranches(true).call();
		readInitialState();
	}
	
	/**
	 * Clones an existing local Git repository into the given local directory as a copy.
	 * 
	 * @param localRepository the local Git repository.
	 * @throws GitAPIException if unable to compute a result.
     * @throws InvalidRemoteException thrown when the local Git repository is invalid.
     * @throws TransportException thrown when the transport operation failed.
	 * @throws IOException if the cloned repository cannot be read.
	 */
	public void initFromLocalRepository(File localRepository)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		initFromRemoteRepository(localRepository.getAbsolutePath());
	}
	
	private void readInitialState() throws IOException, NoHeadException, GitAPIException {
		defaultBranch = git.getRepository().getBranch();
		git.log().setMaxCount(1).call().forEach(c -> latestCommit = c);
	}
	
	public boolean isInitialized() {
		return git != null;
	}
	
	/**
	 * Closes the Git repository.
	 */
	public void closeRepository() {
		git.close();
	}
	
	/**
	 * Performs the <code>git checkout</code> command. 
	 * 
	 * @param id the commit id or branch to checkout.
	 * 
	 * @exception RefAlreadyExistsException thrown when trying to create a Ref with the same name as an existing one.
	 * @exception RefNotFoundException thrown when a Ref cannot be resolved.
	 * @exception InvalidRefNameException thrown when an invalid Ref name was encountered.
	 * @exception CheckoutConflictException thrown when a command cannot succeed because of unresolved conflicts.
	 * @exception GitAPIException if unable to compute a result.
	 */
	public void checkout(String id) throws RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		git.checkout().setName(id).call();
		git.log().setMaxCount(1).call().forEach(c -> latestCommit = c);
	}
	
	/**
	 * Returns the commit for a commit id.
	 * 
	 * @param commitId the commit id.
	 * @return the commit.
	 * @throws GitAPIException if the commit id is invalid.
	 * @throws IOException if the repository cannot be read.
	 */
	public RevCommit getCommitForId(String commitId) throws GitAPIException, IOException {
		if (commitId == null) {
			return null;
		}
		return git.getRepository().parseCommit(git.getRepository().resolve(commitId));
	}
	
	/**
	 * Returns all commits in the Git repository.
	 * 
	 * @return all commits.
	 */
	public List<RevCommit> getAllCommits() {
		List<RevCommit> listOfCommits = new ArrayList<>();
		try {
			git.log().all().call().forEach(listOfCommits::add);
		} catch (GitAPIException | IOException e) {
		}
		Collections.reverse(listOfCommits);
		return listOfCommits;
	}
	
	/**
	 * Returns all commits from a given branch.
	 * 
	 * @param branchName the given branch.
	 * @return all commits from the given branch.
	 */
	public List<RevCommit> getAllCommitsFromBranch(String branchName) {
		List<RevCommit> listOfCommits = new ArrayList<>();
		try {
			git.log().add(git.getRepository().resolve(branchName)).call().forEach(listOfCommits::add);
		} catch (RevisionSyntaxException | GitAPIException | IOException e) {
		}
		Collections.reverse(listOfCommits);
		return listOfCommits;
	}
	
	/**
	 * Returns all commits between two particular commits.
	 * 
	 * @param startCommitHash start commit. 
	 * @param endCommitHash end commit.
	 * @return commits between two particular commits.
	 */
	public List<RevCommit> getAllCommitsBetweenTwoCommits(final String startCommitHash, final String endCommitHash) {
		List<RevCommit> listOfCommits = new ArrayList<>();
		try {
			ObjectId refTo = git.getRepository().resolve(endCommitHash);
			if (startCommitHash == null) {
				git.log().add(refTo).call().forEach(listOfCommits::add);
			} else {
				ObjectId refFrom = git.getRepository().resolve(startCommitHash);
				git.log().addRange(refFrom, refTo).call().forEach(listOfCommits::add);
			}
		} catch (IOException | GitAPIException e) {
		}
		Collections.reverse(listOfCommits);
		return listOfCommits;
	}

	/**
	 * Computes all {@link DiffEntry} between <code>oldRevCommit</code> and <code>newRevCommit</code> representing the changes between the two commits.
	 * For more explanation of particular parts of the method <a href="https://www.codeaffine.com/2016/06/16/jgit-diff/">https://www.codeaffine.com</a>
	 * 
	 * @param oldRevCommit start commit (usually an older commit).
	 * @param newRevCommit end commit (usually a newer commit).
	 * @param onlyChangesOnJavaFiles If the flag is true, only changes on Java files will be detected. All changes on other file types will be ignored.
	 * @param detectRenames If the flag is true, renames on files will be detected.
	 * @return computed {@link List} with {@link DiffEntry}.
	 * @throws IOException if an IO operation fails.
	 * @throws IncorrectObjectTypeException if one of the given commits is invalid.
	 */
	public List<DiffEntry> computeDiffsBetweenTwoCommits(RevCommit oldRevCommit, RevCommit newRevCommit, boolean onlyChangesOnJavaFiles,
			boolean detectRenames) throws IncorrectObjectTypeException, IOException {

		ObjectReader treeReader = git.getRepository().newObjectReader();
		
		AbstractTreeIterator oldParser;
		if (oldRevCommit != null) {
			ObjectId oldTreeId = oldRevCommit.getTree().getId();
			CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
			oldTreeParser.reset(treeReader, oldTreeId);
			oldParser = oldTreeParser;
		} else {
			oldParser = new EmptyTreeIterator();
		}
		
		ObjectId newTreeId = newRevCommit.getTree().getId();
		CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
		newTreeParser.reset(treeReader, newTreeId);

		OutputStream outputStream = NullOutputStream.INSTANCE;
		DiffFormatter df = new DiffFormatter(outputStream) {
			@Override
			protected void writeAddedLine(RawText text, int line) {
				var cs = EvaluationDataContainer.getGlobalContainer().getChangeStatistic();
				cs.setNumberAddedLines(cs.getNumberAddedLines()+1);
			}
			
			@Override
			protected void writeRemovedLine(RawText text, int line) {
				var cs = EvaluationDataContainer.getGlobalContainer().getChangeStatistic();
				cs.setNumberRemovedLines(cs.getNumberRemovedLines()+1);
			}
		};
		df.setRepository(git.getRepository());
		
		// Set filter to detect only changes on Java files if necessary. 
		if (onlyChangesOnJavaFiles) {
			TreeFilter treeFilter = PathSuffixFilter.create(".java");
			df.setPathFilter(treeFilter);
		}
		// Compute diffs between the commits.
		List<DiffEntry> diffs = df.scan(oldParser, newTreeParser);
		
		// Detect renames on changed files if necessary.
		if (detectRenames) {
			 RenameDetector rd = new RenameDetector(git.getRepository());
			 rd.addAll(diffs);
			 diffs = rd.compute();
		}
		
		for (DiffEntry diff : diffs) {
			df.format(diff);
		}
		df.close();
		
		EvaluationDataContainer.getGlobalContainer().getChangeStatistic().setNumberChangedJavaFiles(diffs.size());
		
		return diffs;
	}

	/**
	 * Computes changes from the given {@link DiffEntry}.
	 * An {@link EditList} contain numbers of lines which have to be added, removed, or replaced 
	 * in the older file version in order to obtain the same content as in the newer file version. 
	 * 
	 * @param diff contains information about the changes on a file.
	 * @return {@link EditList}
	 * @throws MissingObjectException if the DiffEntry is missing.
	 * @throws CorruptObjectException if the DiffEntry is invalid.
	 * @throws IOException if an I/O exception occurs.
	 */
	public EditList computeEditListFromDiffEntry(DiffEntry diff)
			throws CorruptObjectException, MissingObjectException, IOException {
		OutputStream outputStream = NullOutputStream.INSTANCE;
		DiffFormatter diffFormatter = new DiffFormatter(outputStream);
		diffFormatter.setRepository(git.getRepository());
		try {
			FileHeader fileHeader = diffFormatter.toFileHeader(diff);
			return fileHeader.toEditList();
		} finally {
			diffFormatter.close();
		}
	}
	
	/**
	 * Returns the older version of file content in {@link String} format.
	 * 
	 * @param diff contains information about changes on a file.
	 * @return older version of the file content.
	 * @throws IOException if the diff cannot be read.
	 * @throws MissingObjectException if the diff is missing.
	 */
	public String getOldContentOfFileFromDiffEntry(DiffEntry diff) throws MissingObjectException, IOException {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
        return readObjectToString(oldObjectId);
	}
	
	/**
	 * Returns the newer version of file content in {@link String} format.
	 * 
	 * @param diff contains information about changes on a file.
	 * @return newer version of file content.
	 * @throws IOException if the diff cannot be read.
	 * @throws MissingObjectException if the diff is missing.
	 */
	public String getNewContentOfFileFromDiffEntry(DiffEntry diff) throws MissingObjectException, IOException {
		ObjectId newObjectId = diff.getNewId().toObjectId();
        return readObjectToString(newObjectId);
	}
	
	private String readObjectToString(ObjectId objId) throws MissingObjectException, IOException {
		ObjectLoader loader = git.getRepository().open(objId);
		return new String(loader.getBytes());
	}
	
	/**
	 * Returns the older version of file content in an {@link OutputStream}.
	 * 
	 * @param diff contains information about changes on a file.
	 * @return older version of file content.
	 * @throws IOException if the diff cannot be read.
	 * @throws MissingObjectException if the diff cannot be found.
	 */
	public OutputStream getOldContentOfFileFromDiffEntryInOutputStream(DiffEntry diff) throws MissingObjectException, IOException {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
        return readObjectAsOutputStream(oldObjectId);
 	}
	
 	/**
	 * Returns the newer version of file content in an {@link OutputStream}.
 	 * 
	 * @param diff contains information about changes on a file.
	 * @return older version of file content.
 	 * @throws IOException if the diff cannot be read.
 	 * @throws MissingObjectException if the diff cannot be found.
	 */
	public OutputStream getNewContentOfFileFromDiffEntryInOutputStream(DiffEntry diff) throws MissingObjectException, IOException {
		ObjectId newObjectId = diff.getNewId().toObjectId();
        return readObjectAsOutputStream(newObjectId);
	}
	
	private OutputStream readObjectAsOutputStream(ObjectId objId) throws MissingObjectException, IOException {
		OutputStream oldContent = new ByteArrayOutputStream();
		git.getRepository().open(objId).copyTo(oldContent);
        return oldContent;
	}
	
	/**
	 * Returns the {@link FileHeader} from the given <code>diff</code>.
	 * 
	 * @param diff the diff to obtain the {@link FileHeader} from.
	 * @return the {@link FileHeader}.
	 * @throws IOException if the diff cannot be read.
	 */
	public FileHeader getFileHeaderFromDiffEntry(DiffEntry diff) throws IOException {
		OutputStream outputStream = NullOutputStream.INSTANCE;
		DiffFormatter diffFormatter = new DiffFormatter(outputStream);
		diffFormatter.setRepository(git.getRepository());
		
		try {
			return diffFormatter.toFileHeader(diff);
		} finally {
			diffFormatter.close();
		}
	}

	public RevCommit getLatestCommit() {
		return latestCommit;
	}
	
	public File getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * Performs a <code>git fetch</code> in order to receive all new commits between the latest local commit
	 * and the latest commit in the remote repository.
	 * 
	 * @return the list of all new commits.
	 */
	public List<RevCommit> fetchAndGetNewCommits() {
		List<RevCommit> result = new ArrayList<>();
		try {
			git.fetch().call();
			ObjectId curCommit = latestCommit.getId();
			ObjectId lastCommit = git.getRepository().resolve("origin/" + defaultBranch);
			git.log().addRange(curCommit, lastCommit).call().forEach(result::add);
		} catch (GitAPIException | IOException e) {
		}
		Collections.reverse(result);
		return result;
	}
	
	/**
	 * Performs a complete cleaning of the git repository, i. e., all untracked and ignored files are removed,
	 * and all changes are reset to the last commit.
	 */
	public void performCompleteClean() {
		try {
			git.clean().setIgnore(true).setCleanDirectories(true).setForce(true).call();
			git.reset().setMode(ResetType.HARD).call();
		} catch (NoWorkTreeException | GitAPIException e) {
		}
	}
}
