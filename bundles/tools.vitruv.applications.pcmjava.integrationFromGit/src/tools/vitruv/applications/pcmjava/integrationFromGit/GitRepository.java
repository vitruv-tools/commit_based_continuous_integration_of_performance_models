package tools.vitruv.applications.pcmjava.integrationFromGit;

//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.Iterator;
import java.util.List;
//import java.util.Scanner;
//import java.util.Set;
//import java.util.regex.Pattern;
import java.util.stream.Stream;

//import org.eclipse.jdt.core.ICompilationUnit;
//import org.eclipse.jdt.core.JavaModelException;
//import org.eclipse.jgit.api.BlameCommand;
//import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
//import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
//import org.eclipse.jgit.diff.Edit.Type;
import org.eclipse.jgit.diff.EditList;
//import org.eclipse.jgit.diff.RawText;
//import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
//import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Ref;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.patch.CombinedHunkHeader;
import org.eclipse.jgit.patch.FileHeader;
//import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
//import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
//import org.eclipse.jgit.util.StringUtils;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;


/**
 * Represents a git repository.
 * 
 * @author Ilia Chupakhin
 *
 */
public class GitRepository {

	private final Git git;
	private RevCommit latestCommit;
	private final File rootDirectory;
	private List<DiffEntry> diffs;
	private String lineDelimiter = System.lineSeparator();

	
	/**
	 * Creates a new git repository in <code>rootDirectory</code>
	 * 
	 * @param rootDirectory directory which a new git repository will be created in
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 */
	public GitRepository(File rootDirectory) throws IllegalStateException, GitAPIException {
		this.rootDirectory = rootDirectory;
		this.git = Git.init().setDirectory(rootDirectory).call();
	}
	
	
	/**
	 * Clones an existing remote git repository in <code>rootDirectory</code>
	 * 
	 * @param rootDirectory directory which the cloned git repository will be created in
	 * @param uriToRemoteRepository uri to the remote repository
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 */
	public GitRepository(File rootDirectory, String uriToRemoteRepository) throws IllegalStateException, GitAPIException {
		this.rootDirectory = rootDirectory;
		this.git = Git.cloneRepository()
		  .setURI(uriToRemoteRepository)
		  .setDirectory(rootDirectory)
		  .setCloneAllBranches(true)
		  .call();
	}
	
	
	/**
	 * Clones an existing local git repository in <code>rootDirectory</code>
	 * 
	 * @param rootDirectory directory which the cloned git repository will be created in
	 * @param localRepository local git directory
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 */
	public GitRepository(File rootDirectory, File localRepository) throws IllegalStateException, GitAPIException {
		this.rootDirectory = rootDirectory;
		this.git = Git.cloneRepository()
		  .setGitDir(localRepository)
		  .setDirectory(rootDirectory)
		  .setCloneAllBranches(true)
		  .call();
	}


	/**
	 * Represents "git add <fileName>" command
	 * 
	 * @param fileName
	 * @throws NoFilepatternException
	 * @throws GitAPIException
	 */
	public void addFilePattern(String fileName) throws NoFilepatternException, GitAPIException {
		git.add().addFilepattern(fileName).call();
	}

	
	/**
	 * Represents "git status" command 
	 * 
	 * @return status
	 * @throws NoWorkTreeException
	 * @throws GitAPIException
	 */
	public Status getStatus() throws NoWorkTreeException, GitAPIException {
		return git.status().call();
	}

	
	/**
	 *  Represents "git checkout <commitHash>" command 
	 * 
	 * @param commitId commit hash
	 * @throws RefAlreadyExistsException
	 * @throws RefNotFoundException
	 * @throws InvalidRefNameException
	 * @throws CheckoutConflictException
	 * @throws GitAPIException
	 */
	public void checkoutFromCommitId(String commitId) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		git.checkout().setName(commitId).call();
	}
	
	
	/**
	 * Switches to the branch with <code>branchName</code> and checks it out
	 * 
	 * @param branchName
	 * @throws RefAlreadyExistsException
	 * @throws RefNotFoundException
	 * @throws InvalidRefNameException
	 * @throws CheckoutConflictException
	 * @throws GitAPIException
	 */
	public void checkoutAndTrackBranch(String branchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		 git.pull()/*.setCredentialsProvider(user)*/.call();
		 git.branchCreate().setForce(true).setName(branchName).setStartPoint("origin/" + branchName).call();
		 git.checkout().setName(branchName).call();
		 
	}

	
	/**
	 * Represents "git commit -m '<code>commitMessage</code>'" command
	 * 
	 * @param commitMessage
	 * @return commit
	 * @throws NoHeadException
	 * @throws NoMessageException
	 * @throws UnmergedPathsException
	 * @throws ConcurrentRefUpdateException
	 * @throws WrongRepositoryStateException
	 * @throws AbortedByHookException
	 * @throws GitAPIException
	 */
	public RevCommit commit(String commitMessage) throws NoHeadException, NoMessageException, UnmergedPathsException,
			ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
		latestCommit = git.commit().setMessage(commitMessage).call();
		return latestCommit;
	}


	/**
	 * Returns all commits 
	 * 
	 * @return all commits
	 * @throws NoHeadException
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public List<RevCommit> getAllCommits() throws NoHeadException, GitAPIException, IOException {
		Iterable<RevCommit> commits = git.log().all().call();
		List<RevCommit> listOfCommits = new ArrayList<>();
		commits.forEach(listOfCommits :: add);
		return listOfCommits;
	}
	
	/**
	 * Returns all commits from current branch
	 * 
	 * @return all commits from branch
	 * @throws NoHeadException
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public List<RevCommit> getAllCommitsFromBranch(String branchName) throws NoHeadException, GitAPIException, IOException {
		Iterable<RevCommit> commits = git.log().add(git.getRepository().resolve(branchName)).call();
		List<RevCommit> listOfCommits = new ArrayList<>();
		commits.forEach(listOfCommits :: add);
		return listOfCommits;
	}
	
	
	/**
	 * Returns all commits between two particular commits
	 * 
	 * @param startCommitHash 
	 * @param endCommitHash
	 * @return commits between two particular commits
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public List<RevCommit> getAllCommitsBetweenTwoCommits(final String startCommitHash, final String endCommitHash) throws IOException, GitAPIException {
		Ref refFrom = git.getRepository().findRef(startCommitHash);
		Ref refTo = git.getRepository().findRef(endCommitHash);
		Iterable<RevCommit> commits = git.log().addRange(refFrom.getObjectId(), refTo.getObjectId()).call();
		List<RevCommit> listOfCommits = new ArrayList<>();
		commits.forEach(listOfCommits :: add);
		return listOfCommits;
	}
	
	
	/**
	 * Prints all {@link DiffEntry} from <code>diffs</code>
	 * 
	 * @param diffs list of {@link DiffEntry} to print
	 */
	public void printDiffs(List<DiffEntry> diffs) {
		if (diffs.isEmpty()) {
			System.out.println("No diffs");
			return;
		}
		for (int i = 0; i < diffs.size(); i++) {
			System.out.println("Diff Number " + i + ":\n" + diffs.get(i));
			DiffFormatter formatter = new DiffFormatter(System.out);
			formatter.setRepository(git.getRepository());
			try {
				// The method setContext(0) enables getting the only changed lines
				formatter.setContext(0);
				formatter.format(diffs.get(i));
				formatter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Computes all {@link DiffEntry} between <code>oldRevCommit</code> and <code>newRevCommit</code>.
	 * For more explanation of particular parts of the method @see <a href="https://www.codeaffine.com/2015/12/15/getting-started-with-jgit/">https://www.codeaffine.com</a>
	 * 
	 * @param oldRevCommit start commit (usually an older commit)
	 * @param newRevCommit end commit (usually a newer commit)
	 * @param onlyChangesOnJavaFiles if that flag is true, changes on only java files will be detected. All changes on other file types will be ignored.
	 * @param detectRenames if that flag is true, renames on files will be detected.
	 * @return computed {@link List} with {@link DiffEntry}
	 */
	public List<DiffEntry> computeDiffsBetweenTwoCommits(RevCommit oldRevCommit, RevCommit newRevCommit, boolean onlyChangesOnJavaFiles, boolean detectRenames) {

		//Parse the older commit
		RevWalk oldWalk = new RevWalk(git.getRepository());
		RevCommit oldCommit = null;
		
		try {
			oldCommit = oldWalk.parseCommit(oldRevCommit);
		} catch (MissingObjectException e2) {
			e2.printStackTrace();
		} catch (IncorrectObjectTypeException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		ObjectId oldTreeId = oldCommit.getTree();

		ObjectReader treeReader = git.getRepository().newObjectReader();
		CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
		try {
			oldTreeParser.reset(treeReader, oldTreeId);
		} catch (IncorrectObjectTypeException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//Parse the newer commit
		RevWalk newWalk = new RevWalk(git.getRepository());
		RevCommit newCommit = null;
		try {
			newCommit = newWalk.parseCommit(newRevCommit);
		} catch (MissingObjectException e2) {
			e2.printStackTrace();
		} catch (IncorrectObjectTypeException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		ObjectId newTreeId = newCommit.getTree();
		CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
		
		try {
			newTreeParser.reset(treeReader, newTreeId);
		} catch (IncorrectObjectTypeException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		List<DiffEntry> diffs = null;

		try {
			DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream()); // use NullOutputStream.INSTANCE if you don't need the diff output
			df.setRepository(git.getRepository());
			
			//Set filter to detect only changes on java files, if necessary 
			if (onlyChangesOnJavaFiles) {
				TreeFilter treeFilter = PathSuffixFilter.create(".java");
				df.setPathFilter(treeFilter);
			}
			//Compute diffs between the commits
			diffs = df.scan(oldTreeParser, newTreeParser);
			
			//Detect renames on changed files, if necessary
			if (detectRenames) {
				 RenameDetector rd = new RenameDetector(git.getRepository());
				 rd.addAll(diffs);
				 diffs = rd.compute();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.diffs = diffs;
		return diffs;
	}

	
	/**
	 * Computes changes from the given {@link DiffEntry}.
	 * An {@link EditList} contain numbers of lines which have to be add, remove or replaced 
	 * in the older file version in order to obtain the same content as in the newer file version. 
	 * 
	 * @param diff contains information about changes on a file 
	 * @return {@link EditList}
	 */
	public EditList computeEditListFromDiffEntry(DiffEntry diff) {

		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		diffFormatter.setRepository(git.getRepository());
		FileHeader fileHeader;
		
		try {
			fileHeader = diffFormatter.toFileHeader(diff);
			return fileHeader.toEditList();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	
	/**
	 * Returns the older version of file content in {@link String} format
	 * 
	 * @param diff contains information about changes on a file
	 * @return older version of file content
	 */
	public String getOldContentOfFileFromDiffEntry(DiffEntry diff) {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
        ObjectLoader loader;
        String oldContent = null;
		
        try {
        	//Load old content version
			loader = git.getRepository().open(oldObjectId);
			oldContent = new String(loader.getBytes());
		} catch (MissingObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return oldContent;
	}
	
	/**
	 * Returns the newer version of file content in {@link String} format
	 * 
	 * @param diff contains information about changes on a file
	 * @return newer version of file content
	 */
	public String getNewContentOfFileFromDiffEntry(DiffEntry diff) {
		ObjectId newObjectId = diff.getNewId().toObjectId();
        ObjectLoader loader;
        String newContent = null;
        
		try {
			//Load new content version
			loader = git.getRepository().open(newObjectId);
			newContent = new String(loader.getBytes());
		} catch (MissingObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return newContent;
	}
	
	
	/**
	 * Returns the older version of file content in {@link OutputStream} format
	 * 
	 * @param diff contains information about changes on a file
	 * @return older version of file content
	 */
	public OutputStream getOldContentOfFileFromDiffEntryInOutputStream(DiffEntry diff) {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
        ObjectLoader loader;
        OutputStream oldContent = new ByteArrayOutputStream();
		
        try {
        	//Load old content version
			loader = git.getRepository().open(oldObjectId);
			loader.copyTo(oldContent);
		} catch (MissingObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return oldContent;
	}
	
	/**
	 * Returns the newer version of file content in {@link OutputStream} format
	 * 
	 * @param diff contains information about changes on a file
	 * @return older version of file content
	 */
	public OutputStream getNewContentOfFileFromDiffEntryInOutputStream(DiffEntry diff) {
		ObjectId newObjectId = diff.getNewId().toObjectId();
        ObjectLoader loader;
        OutputStream newContent =  new ByteArrayOutputStream();
		
        try {
			//Load new content version
			loader = git.getRepository().open(newObjectId);
			loader.copyTo(newContent);
		} catch (MissingObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return newContent;
	}
	
	
	/**
	 * Returns {@link FileHeader} from the given <code>diff</code>
	 * 
	 * @param diff
	 * @return {@link FileHeader}
	 */
	public FileHeader getFileHeaderFromDiffEntry(DiffEntry diff) {

		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		diffFormatter.setRepository(git.getRepository());
		FileHeader fileHeader = null;
		
		try {
			fileHeader = diffFormatter.toFileHeader(diff);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileHeader;
	}

	
	
	/**
	 * Replaces all possible kinds of line separators with one particular kind of line separator in <code>fileContent</code>
	 * 
	 * @param fileContent
	 * @return file content with replaced line separators
	 */
	public String replaceAllLineDelimitersWithSystemLineDelimiters(String fileContent) {
		return fileContent.replaceAll("\r\n|\n|\r", lineDelimiter);
	}
	
	
	
	/**
	 * Transforms {@link EditList} into {@link List} of {@link TextEdit}.
	 * There are two main problems to solve:
	 *  
	 * The first one is, an {@link Edit} contains information about content in form of line numbers, 
	 * whereas an {@link TextEdit} deals with offsets of content. Therefore, we have to compute offsets to line numbers.
	 * 
	 * The second problem is, {@link EditList} contains Edits that describe which lines in the old content have to be add/removed/replaced with
	 * lines from the new content or which lines from the new content have to be inserted in the old content. Edits are independent from each other.
	 * That means, all Edits on the old content have to be applied at the same time. Applying Edits one by one would cause shifting in the old content
	 * after each applying and would create wrong content, that does not match to the new content. On the other hand, TextEdits have to be applied one by one. 
	 * Therefore, while computing offset for a TextEdit we have to consider shifting caused by previously applied TextEdits.   
	 * 
	 * @param editList - list of Edits to be applied on the old context to transform the old context into the new context.
	 * @param oldFileContent - old content as plain text
	 * @param newFileContent - new content as plain text
	 * @return list of TextEdits, that have to be applied one by one (the applying order is important) on the old context to transform the old context into the new context.
	 */
	public List<TextEdit> transformEditListIntoTextEdits(EditList editList, String oldFileContent, String newFileContent) {
		
		ArrayList<TextEdit> textEdits = new ArrayList<>();
		
		//Split the content into separated lines
		List<String> oldContentLines = splitFileContentIntoLinesWithLineDelimitors(oldFileContent);
		List<String> newContentLines = splitFileContentIntoLinesWithLineDelimitors(newFileContent);
		//Determine begin offset for each line of contexts 
		List<Integer> oldContentStartOffsetsToLines = computeStartOffsetsToLineNumbers(oldContentLines);
		List<Integer> newContentStartOffsetsToLines = computeStartOffsetsToLineNumbers(newContentLines);
		//Contains shifting that appears while applying Edits one by one
		int shifting = 0;
	
		for(Edit edit : editList) {
			//begin line number in the old content
			int beginPositionOld = oldContentStartOffsetsToLines.get(edit.getBeginA());
			//end line number in the old content
			int endPositionOld = oldContentStartOffsetsToLines.get(edit.getEndA());
			//begin line number in the new content
			int beginPositionNew = newContentStartOffsetsToLines.get(edit.getBeginB());
			//end line number in the new content
			int endPositionNew = newContentStartOffsetsToLines.get(edit.getEndB());
			
			switch (edit.getType()) {
			case INSERT:
				int positionForInsert = beginPositionOld + shifting;
				//"- edit.getBeginB()" and "- edit.getEndB()" are needed because of the third problem described above in the java doc for the method.
				String textForInsert = concatenateContentLines(newContentLines, edit.getBeginB(), edit.getEndB());
				TextEdit insertEdit = new InsertEdit(positionForInsert, textForInsert);
				textEdits.add(insertEdit);
				shifting += endPositionNew - beginPositionNew;
				break;
			case DELETE:
				int positionForDelete = beginPositionOld + shifting;
				int lengthForDelete = endPositionOld - beginPositionOld;
				TextEdit deleteEdit = new DeleteEdit(positionForDelete, lengthForDelete);
				textEdits.add(deleteEdit);
				shifting -= lengthForDelete;
				break;
			case REPLACE:
				int positionForReplace = beginPositionOld + shifting;
				int lengthForReplace = endPositionOld - beginPositionOld;
				String textForReplace = concatenateContentLines(newContentLines, edit.getBeginB(), edit.getEndB());
				TextEdit replaceEdit = new ReplaceEdit(positionForReplace, lengthForReplace, textForReplace);
				textEdits.add(replaceEdit);
				shifting = shifting - lengthForReplace + (endPositionNew - beginPositionNew);
				break;
			case EMPTY:
				//do nothing
				break;
			default:
				//do nothing
				break;
			}
		}
		
		return textEdits;
	}
	
	
	/**
	 * Concatenate <code>contentLines</code> between [<code>beginLineNumber</code>, <code>endLineNumber</code>)
	 * 
	 * @param newContentLines - Lines of the content
	 * @param beginPositionNew - begin line number. Included
	 * @param endPositionNew - end line number. Not included.
	 * @return concatenated lines as String
	 */
	private String concatenateContentLines(List<String> contentLines, int beginLineNumber, int endLineNumber) {
		String content = "";
		
		for (int i = beginLineNumber;  i < endLineNumber; i++) {
			content += contentLines.get(i);
		}
		
		return content;
	}


	/**
	 * Splits <code>fileContent</code> into lines. Each split line except the last line contains a line delimiter at the end.
	 * The last line may contain a line delimiter at the end.
	 * @param fileContent
	 * @return List of content lines with line delimiters at the end
	 */
	private List<String> splitFileContentIntoLinesWithLineDelimitors(String fileContent) {
		
		List<String> contentLines = new ArrayList<String>();
		
		Stream<String> lines = fileContent.lines();
		Iterator<String> iterator = lines.iterator();
		
		//Determine begin offset for each line
		while (iterator.hasNext()) {
			String line = iterator.next() + lineDelimiter;
			//if the current line is the last line, check if the last line ends with a line delimiter
			if (!iterator.hasNext()) {
				if (!fileContent.endsWith(lineDelimiter)) {
					//remove line delimiter
					line = line.substring(0, line.length() - lineDelimiter.length());
				} 
			}
			contentLines.add(line);
		}

		return contentLines;
	}
	
	
	/**
	 * Computes start offset for each line of <code>fileContent</code>. The file content is represented as List of content lines with line delimiters.
	 * The last element in the result list contains the length of the last content line. This implies, that the length
	 * of the result list equals the number of content lines + 1.
	 *  
	 * @param fileContent - file content.
	 * @return list of computed start offsets. The last element in the list contains the length of the last content line.
	 */

	private List<Integer> computeStartOffsetsToLineNumbers(List<String> fileContent) {
		//Index of each element in the list corresponds to the line number of the context
		List<Integer> startOffsets = new ArrayList<>();
		
		int offsetCounter = 0;
		
		for (String line : fileContent) {
			startOffsets.add(offsetCounter);
			offsetCounter += line.length();
		}
		//The last element in startOffsets contains length of the last line
		startOffsets.add(offsetCounter);
		
		return startOffsets;
	}
	
	
	public RevCommit getLatestCommit() {
		return latestCommit;
	}
	
	
	public List<DiffEntry> getDiffs() {
		return diffs;
	}


	public File getRootDirectory() {
		return rootDirectory;
	}


	public void setLineDelimiter(String lineDelimiter) {
		this.lineDelimiter = lineDelimiter;
	}
	
	
	public String getLineDelimiter() {
		return lineDelimiter;
	}

}
