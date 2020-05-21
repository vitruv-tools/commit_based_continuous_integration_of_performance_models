package tools.vitruv.applications.pcmjava.integrationFromGit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
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
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.Edit.Type;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.CombinedHunkHeader;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.StringUtils;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

//import com.gitblit.models.AnnotatedLine;

public class GitRepository {

	// private String localPath;
	private final Git git;
	private RevCommit latestCommit;
	private final File rootDirectory;
	private List<DiffEntry> diffs;
	private String lineDelimiter = System.lineSeparator();

	
	public GitRepository(File rootDirectory) throws IllegalStateException, GitAPIException {
		this.rootDirectory = rootDirectory;
		this.git = Git.init().setDirectory(rootDirectory).call();
	}
	
	
	public GitRepository(File rootDirectory, String uriToRemoteRepository) throws IllegalStateException, GitAPIException {
		this.rootDirectory = rootDirectory;
		this.git = Git.cloneRepository()
		  .setURI(uriToRemoteRepository)
		  .setDirectory(rootDirectory)
		  .call();
	}
	
	
	public GitRepository(File rootDirectory, File localRepository) throws IllegalStateException, GitAPIException {
		this.rootDirectory = rootDirectory;
		this.git = Git.cloneRepository()
		  .setGitDir(localRepository)
		  .setDirectory(rootDirectory)
		  .call();
	}


	public void addFilePattern(String fileName) throws NoFilepatternException, GitAPIException {
		git.add().addFilepattern(fileName).call();
	}

	
	public Status getStatus() throws NoWorkTreeException, GitAPIException {
		return git.status().call();
	}

	
	public void checkoutFromCommitId(String commitId) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		git.checkout().setName(commitId).call();
	}
	
	public void checkoutAndTrackBranch(String branchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		 git.checkout().setCreateBranch(true).setName("testBranch")
         .setUpstreamMode(SetupUpstreamMode./*TRACK*/SET_UPSTREAM)
		 .setStartPoint("origin/" + branchName).call();
	}
	
	public void printStatusAdded() throws NoWorkTreeException, GitAPIException {
		Status status = getStatus();
		Set<String> added = status.getAdded();
		for (String add : added) {
			System.out.println("Added: " + add);
		}
	}

	
	public void printStatusUntracked() throws NoWorkTreeException, GitAPIException {
		Status status = getStatus();
		Set<String> added = status.getUntracked();
		for (String add : added) {
			System.out.println("Untracked: " + add);
		}
	}

	
	public RevCommit commit(String commitMessage) throws NoHeadException, NoMessageException, UnmergedPathsException,
			ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
		latestCommit = git.commit().setMessage(commitMessage).call();
		return latestCommit;
	}


	public List<RevCommit> getAllCommits() throws NoHeadException, GitAPIException, IOException {
		Iterable<RevCommit> commits = git.log().all().call();
		List<RevCommit> listOfCommits = new ArrayList<>();
		commits.forEach(listOfCommits :: add);
		return listOfCommits;
	}
	
	public List<RevCommit> getAllCommitsFromBranch(String branchName) throws NoHeadException, GitAPIException, IOException {
		Iterable<RevCommit> commits = git.log().add(git.getRepository().resolve(branchName)).call();
		List<RevCommit> listOfCommits = new ArrayList<>();
		commits.forEach(listOfCommits :: add);
		return listOfCommits;
	}
	
	
	
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<DiffEntry> computeDiffsBetweenTwoCommits(RevCommit oldRevCommit, RevCommit newRevCommit, boolean onlyChangesOnJavaFiles, boolean detectRenames) {

		RevWalk oldWalk = new RevWalk(git.getRepository());
		RevCommit oldCommit = null;
		
		try {
			oldCommit = oldWalk.parseCommit(oldRevCommit);
		} catch (MissingObjectException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IncorrectObjectTypeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ObjectId oldTreeId = /* git.getRepository().resolve( "HEAD~1^{tree}" ) */oldCommit.getTree();

		ObjectReader oldTreeReader = git.getRepository().newObjectReader();
		CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
		try {
			oldTreeParser.reset(oldTreeReader, oldTreeId);
			// oldTreeParser = new CanonicalTreeParser(null, oldTreeReader, oldTreeId);
		} catch (IncorrectObjectTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RevWalk newWalk = new RevWalk(git.getRepository());
		RevCommit newCommit = null;
		try {
			newCommit = newWalk.parseCommit(newRevCommit);
		} catch (MissingObjectException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IncorrectObjectTypeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ObjectId newTreeId = newTreeId = newCommit.getTree();

		CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
		try {
			newTreeParser.reset(oldTreeReader, newTreeId);
			// newTreeParser = new CanonicalTreeParser(null, newTreeReader, newTreeId);
		} catch (IncorrectObjectTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<DiffEntry> diffs = null;

		try {

			DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream()); // use NullOutputStream.INSTANCE if you
																				// don't need the diff output
			df.setRepository(git.getRepository());
			
			//Set filter to detect only changes on java files, if necessary 
			if (onlyChangesOnJavaFiles) {
				TreeFilter treeFilter = PathSuffixFilter.create(".java");
				df.setPathFilter(treeFilter);
			}
			
			diffs = df.scan(oldTreeParser, newTreeParser);
			
			//Detect renames on changed files, if necessary
			if (detectRenames) {
				 RenameDetector rd = new RenameDetector(git.getRepository());
				 rd.addAll(diffs);
				 diffs = rd.compute();
			}
			
			/*
			 * diffs = git.diff() .setOldTree( oldTreeParser ) .setNewTree( newTreeParser )
			 * .call();
			 */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.diffs = diffs;
		return diffs;
	}

	
	public EditList computeEditListFromDiffEntry(DiffEntry diff) {

		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		diffFormatter.setRepository(git.getRepository());
		FileHeader fileHeader;
		
		try {

			fileHeader = diffFormatter.toFileHeader(diff);
			return fileHeader.toEditList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	
	public String getOldContentOfFileFromDiffEntry(DiffEntry diff) {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
        ObjectLoader loader;
        String oldContent = null;
		try {
			loader = git.getRepository().open(oldObjectId);
			oldContent = new String(loader.getBytes());
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return oldContent;
	}
	
	
	public String getNewContentOfFileFromDiffEntry(DiffEntry diff) {
		ObjectId newObjectId = diff.getNewId().toObjectId();
        ObjectLoader loader;
        String newContent = null;
		try {
			loader = git.getRepository().open(newObjectId);
			newContent = new String(loader.getBytes());
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return newContent;
	}
	
	
	public OutputStream getOldContentOfFileFromDiffEntryInOutputStream(DiffEntry diff) {
		ObjectId oldObjectId = diff.getOldId().toObjectId();
        ObjectLoader loader;
        OutputStream oldContent = new ByteArrayOutputStream();
		try {
			loader = git.getRepository().open(oldObjectId);
			loader.copyTo(oldContent);
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return oldContent;
	}
	
	
	public OutputStream getNewContentOfFileFromDiffEntryInOutputStream(DiffEntry diff) {
		ObjectId newObjectId = diff.getNewId().toObjectId();
        ObjectLoader loader;
        OutputStream newContent =  new ByteArrayOutputStream();
		try {
			loader = git.getRepository().open(newObjectId);
			loader.copyTo(newContent);
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return newContent;
	}
	
	
	public FileHeader getFileHeaderFromDiffEntry(DiffEntry diff) {

		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		diffFormatter.setRepository(git.getRepository());
		FileHeader fileHeader;
		try {
			fileHeader = diffFormatter.toFileHeader(diff);
			return fileHeader;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	public List<String> getPathesOfChangedFilesFromDiffs(List<DiffEntry> diffs) {
		if (diffs != null) {
			List<String> names = new ArrayList<String>();
			for (DiffEntry de : diffs) {
				names.add(de.getOldPath());
			}
			return names;
		}
		return null;
	}
	
	
	public String replaceAllLineDelimitersWithSystemLineDelimiters(String fileContent) {
		return fileContent.replaceAll("\r\n|\n|\r", lineDelimiter);
	}
	
	
	
	/**
	 * Transforms EditList (org.eclipse.jgit.diff.EditList) into List of TextEdits (org.eclipse.text.edits.TextEdit).
	 * There are two main problems to solve:
	 *  
	 * The first one is, an Edit (org.eclipse.jgit.diff.Edit) contains information about content in form of line numbers, 
	 * whereas an TextEdit deals with offsets of content. Therefore, we have to compute offsets to line numbers.
	 * 
	 * The second problem is, EditList contains Edits that describe which lines in the old content have to be removed/replaced with
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
	 * Concatenate the content lines between [beginLineNumber, endLineNumber)
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
	 * Splits file content into lines. Each split line except the last line contains a line delimiter at the end.
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
	 * Computes start offset for each line of file content. The file content is represented as List of content lines with line delimiters.
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

	
	
	/*	
	public List<SingleChange> getListOfParsedDiffsForSingleFile() {
		return listOfParsedDiffsForSingleFile;
	}
	 */
	
	
	/*
	public EditList computeEditListBetweenTwoCommits(RevCommit oldRevCommit, RevCommit newRevCommit, boolean onlyChangesOnJavaFiles, boolean detectRenames) {

		RevWalk oldWalk = new RevWalk(git.getRepository());
		RevCommit oldCommit = null;
		try {
			oldCommit = oldWalk.parseCommit(oldRevCommit);
		} catch (MissingObjectException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IncorrectObjectTypeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ObjectId oldTreeId = oldCommit.getTree();

		ObjectReader oldTreeReader = git.getRepository().newObjectReader();
		CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
		try {
			oldTreeParser.reset(oldTreeReader, oldTreeId);
		} catch (IncorrectObjectTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RevWalk newWalk = new RevWalk(git.getRepository());
		RevCommit newCommit = null;
		try {
			newCommit = newWalk.parseCommit(newRevCommit);
		} catch (MissingObjectException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IncorrectObjectTypeException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		ObjectId newTreeId = newTreeId = newCommit.getTree();

		CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
		try {
			newTreeParser.reset(oldTreeReader, newTreeId);
			// newTreeParser = new CanonicalTreeParser(null, newTreeReader, newTreeId);
		} catch (IncorrectObjectTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
		diffFormatter.setRepository(git.getRepository());
		
		//Set filter if necessary 
		if (onlyChangesOnJavaFiles) {
			TreeFilter treeFilter = PathSuffixFilter.create(".java");
			diffFormatter.setPathFilter(treeFilter);
		}
		
		List<DiffEntry> diffEntries;
		FileHeader fileHeader;
		try {
			diffEntries = diffFormatter.scan(oldTreeParser, newTreeParser);
			
			if (detectRenames) {
				 RenameDetector rd = new RenameDetector(git.getRepository());
				 rd.addAll(diffEntries);
				 diffEntries = rd.compute();
			}
			
			fileHeader = diffFormatter.toFileHeader(diffEntries.get(0));
		
			return fileHeader.toEditList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
*/
	
	
	
	
	/**
	 * Computes begin offset for each line of file content. The file content is represented as plain text.
	 *  
	 * @param fileContent - file content represented as plain text.
	 * @return list of computed offsets. In the list an index corresponds to the line number of the context.
	 */
/*
	private List<Integer> computeOffsetsToLineNumbers(String fileContent) {
		//Index of each element in the list corresponds to the line number of the context
		List<Integer> startOffsets = new ArrayList<>();
		//Divide the content in lines
		Stream<String> lines = fileContent.lines();
		
		Iterator<String> iterator = lines.iterator();
		int offsetCounter = 0;
		int lineDelimiterLength = lineDelimiter.length();
		
		//Determine begin offset for each line
		while (iterator.hasNext()) {
			startOffsets.add(offsetCounter);
			String line = iterator.next();
			int length = line.length();	
			//offsetCounter = length == 0 ? offsetCounter + 1 : offsetCounter + length + 2; //+2 reflects line separator, for example \n. BUT length of special symbols like \t or \n or \r etc. is 1.
			offsetCounter += length + lineDelimiterLength; //+2 reflects line separator, for example \n. BUT all standard functions from Class String consider length of special symbols like \t or \n or \r etc. to be 1.
		}
		//The last element in the list startOffsets is needed to determine the length of the last line
		//Because "\n" is not visible in a line after applying fileContent.lines(), we have to find out if the last line contains a line separator.
		//Depending on the result, adjust the length of the last line
		if (fileContent.endsWith(lineDelimiter)) {
			startOffsets.add(offsetCounter);
		} 
		else {
			startOffsets.add(offsetCounter - lineDelimiterLength);
		}
		
		return startOffsets;
	}
*/
	
	
	
	/*
	public int getOffsetForLineNumber(final int lineNumberOfChangedStatement, final ICompilationUnit icu) throws JavaModelException {
 		final String sourceCodeOfCompilationUnit = icu.getSource();
		Stream<String> lines = sourceCodeOfCompilationUnit.lines();
		Iterator<String> iterator = lines.iterator();
		int lineNumberCounter = 0;
		int offsetCounter = 0;
		//determine offset for lineNumberOfChangedStatement
		while (iterator.hasNext() && lineNumberCounter != lineNumberOfChangedStatement) {
			String line = iterator.next();
			offsetCounter += line.length() + 2; //+2 reflects line separator, for example \n
			lineNumberCounter++;
		}
		
		return offsetCounter;
 	}
*/
	
	
	/*	
	public void getChanges() {
		List<DiffEntry> diffs = getDiffsBetweenTwoCommits(latestCommit.getParent(0), latestCommit);
		printDiffs(diffs, git.getRepository());

	}
*/
/*	
	public void getChanges(RevCommit oldCommit, RevCommit newCommit) {
		List<DiffEntry> diffs = getDiffsBetweenTwoCommits(oldCommit, newCommit);
		printDiffs(diffs, git.getRepository());

	}
*/
	
	
	/*	
	public static void printFile(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	
	
	/*//My parser
	public List<SingleChange> parseDiff(DiffEntry diff) throws IOException {
		List<SingleChange> changes = new ArrayList<>();
		String classPath = diff.getOldPath();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DiffFormatter formatter = new DiffFormatter(outputStream);

		formatter.setRepository(git.getRepository());
		formatter.setContext(0);
		formatter.format(diff);

		Scanner scanner = new Scanner(outputStream.toString("UTF-8"));
		String currentLine;

		while (scanner.hasNextLine()) {
			currentLine = scanner.nextLine();

			if (currentLine.startsWith("@@")) {
				SingleChange singleChange = new SingleChange();
				singleChange.setClassPath(classPath);

				parseLineNumbers(scanner, singleChange, currentLine);
				changes.add(singleChange);
			}
		}

		outputStream.close();
		formatter.close();
		scanner.close();

		return changes;
	}

	private void parseLineNumbers(Scanner scanner, SingleChange singleChange, String currentLine) {
		// Parse @@ Line
		parseSingleLineThatContainsLineNumbers(currentLine, singleChange);
		Pattern beginOfNewSingleChangePattern = Pattern.compile("@@.*");

		// While scanner has next line and the next line is not the next change (doesn't
		// match pattern "@@.*")
		while (scanner.hasNext() && !scanner.hasNext(beginOfNewSingleChangePattern)) {
			String nextLine = scanner.nextLine();

			if (nextLine.startsWith("+")) {
				parseAddedLines(scanner, singleChange, nextLine);
			} else if (nextLine.startsWith("-")) {
				parseRemovedLines(scanner, singleChange, nextLine);
			}
		}
	}

	private void parseSingleLineThatContainsLineNumbers(String currentLine, SingleChange singleChange) {

		String removeStartLineNumber = "";
		String removeNumberOfLines = "";
		String addStartLineNumber = "";
		String addNumberOfLines = "";

		// \d means any number, 0 - 9
		// Pattern digit = Pattern.compile("\\d");

		// Examples:
		// @@ -8,2 +8,3 @@
		// @@ -8,2 +8 @@
		// @@ -8 +8,3 @@
		// @@ -8 +8 @@
		// @@ -8,2 @@
		// @@ -8 @@
		// @@ +8,2 @@
		// @@ +8 @@

		// remove "@@ "
		String subString = currentLine.substring(3);

		// Look for removed lines
		if (subString.startsWith("-")) {
			// remove "-"
			subString = subString.substring(1);

			// \d means any number, 0 - 9
			// read start line number for remove command
			while (Pattern.matches("\\d", subString.substring(0, 1))) {
				removeStartLineNumber = removeStartLineNumber + subString.substring(0, 1);
				subString = subString.substring(1);
			}

			// if more than one line were removed, there is a comma separator and
			// after the comma the number of removed lines is written
			if (subString.substring(0, 1).equals(",")) {
				// remove ","
				subString = subString.substring(1);
				// read number of removed lines
				while (Pattern.matches("\\d", subString.substring(0, 1))) {
					removeNumberOfLines = removeNumberOfLines + subString.substring(0, 1);
					subString = subString.substring(1);
				}
			} else {
				removeNumberOfLines = "1";
			}

			// remove space character " "
			subString = subString.substring(1);
		}

		// Look for added lines
		if (subString.startsWith("+")) {
			// remove "+"
			subString = subString.substring(1);

			// \d means any number, 0 - 9
			// read start line number for add command
			while (Pattern.matches("\\d", subString.substring(0, 1))) {
				addStartLineNumber = addStartLineNumber + subString.substring(0, 1);
				subString = subString.substring(1);
			}

			// if more than one line were added, there is a comma separator and
			// after the comma the number of added lines is written
			if (subString.substring(0, 1).equals(",")) {
				// remove ","
				subString = subString.substring(1);
				// read number of removed lines
				while (Pattern.matches("\\d", subString.substring(0, 1))) {
					addNumberOfLines = addNumberOfLines + subString.substring(0, 1);
					subString = subString.substring(1);
				}
			} else {
				addNumberOfLines = "1";
			}
		}

		// Set determined numbers in singleChange
		singleChange.setRemoveStartLineNumber(
				removeStartLineNumber.equals("") ? 0 : Integer.parseInt(removeStartLineNumber));
		singleChange.setRemoveNumberOfLines(removeNumberOfLines.equals("") ? 0 : Integer.parseInt(removeNumberOfLines));
		singleChange.setAddStartLineNumber(addStartLineNumber.equals("") ? 0 : Integer.parseInt(addStartLineNumber));
		singleChange.setAddNumberOfLines(addNumberOfLines.equals("") ? 0 : Integer.parseInt(addNumberOfLines));

	}

	private void parseAddedLines(Scanner scanner, SingleChange singleChange, String currentLine) {

		singleChange.addlinesOfCodeToAdd(currentLine.substring(1));

		Pattern addedLinePattern = Pattern.compile("\\+.*");
		Pattern removedLinePattern = Pattern.compile("-.*");

		if (scanner.hasNext(addedLinePattern)) {
			parseAddedLines(scanner, singleChange, scanner.nextLine());
		} else if (scanner.hasNext(removedLinePattern)) {
			parseRemovedLines(scanner, singleChange, scanner.nextLine());
		}
	}

	private void parseRemovedLines(Scanner scanner, SingleChange singleChange, String currentLine) {

		singleChange.addlinesOfCodeToRemove(currentLine.substring(1));

		Pattern addedLinePattern = Pattern.compile("\\+.*");
		Pattern removedLinePattern = Pattern.compile("-.*");

		if (scanner.hasNext(addedLinePattern)) {
			parseAddedLines(scanner, singleChange, scanner.nextLine());
		} else if (scanner.hasNext(removedLinePattern)) {
			parseRemovedLines(scanner, singleChange, scanner.nextLine());
		}

	}
*/
	
	
	/*
	 * public String parseDiff(RevCommit oldCommit, RevCommit newCommit) throws
	 * IOException { ByteArrayOutputStream diffOutputStream = new
	 * ByteArrayOutputStream(); DiffFormatter diffFormater = new
	 * DiffFormatter(diffOutputStream);
	 * diffFormater.setRepository(git.getRepository());
	 * diffFormater.setDiffComparator(RawTextComparator.DEFAULT);
	 * diffFormater.setDetectRenames(true); diffFormater.format(oldCommit.getTree(),
	 * newCommit.getTree());
	 * 
	 * diffFormater.close();
	 * 
	 * return new String(diffOutputStream.toByteArray()); }
	 */

	/*
	public void printHunks() {
		FileHeader fileHeader = getFileHeaderOfChanges(latestCommit.getParent(0), latestCommit);
		System.out.println("fileHeader.getScriptText():\n" + fileHeader.getScriptText());

		// List hunks = fileHeader.getHunks().get(0).;
		System.out.println("Hunks:");
		for (int i = 0; i < fileHeader.getHunks().size(); i++) {
			System.out.println(fileHeader.getHunks().get(i).getBuffer());
		}

	}
	*/
	
	/**
	 * Returns the list of lines in the specified source file annotated with the
	 * source commit metadata.
	 * 
	 * @param repository
	 * @param blobPath
	 * @param objectId
	 * @return list of annotated lines
	 * @throws GitAPIException
	 */
	/*
	 * public void blame(String filePath, ObjectId commitId) throws GitAPIException
	 * {
	 * 
	 * BlameCommand blameCommand = new BlameCommand(git.getRepository());
	 * blameCommand.setFilePath(filePath); blameCommand.setStartCommit(commitId);
	 * BlameResult blameResult = blameCommand.call(); RawText rawText =
	 * blameResult.getResultContents(); int length = rawText.size();
	 * System.out.println("blame:"); for (int i = 0; i < length; i++) {
	 * System.out.println(rawText.getRawContent()); //RevCommit commit =
	 * blameResult.getSourceCommit(i); //AnnotatedLine line = new
	 * AnnotatedLine(commit, i + 1, rawText.getString(i)); //lines.add(line); }
	 * 
	 * }
	 */
	
}
