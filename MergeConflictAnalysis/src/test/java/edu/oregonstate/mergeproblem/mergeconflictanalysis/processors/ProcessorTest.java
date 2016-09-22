package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.HashMap;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.revwalk.RevCommit;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public abstract class ProcessorTest extends MergeGitTest {

	protected static final String FILE_NAME = "A.java";

	public CommitStatus generateCommitStatus() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		return status;
	}

	public CommitStatus createCommitStatus(String a, String b) throws Exception {
		RevCommit commit = createConflictingCommit();
		CombinedFile combinedFile = new CombinedFile();
		combinedFile.addChunk(ChunkOwner.A, a);
		combinedFile.addChunk(ChunkOwner.B, b);
		
		HashMap<String, CombinedFile> conflictingFiles = new HashMap<String, CombinedFile>();
		conflictingFiles.put(FILE_NAME, combinedFile);
		CommitStatus commitStatus = new CommitStatus(repository, commit);
		return commitStatus;
	}
}