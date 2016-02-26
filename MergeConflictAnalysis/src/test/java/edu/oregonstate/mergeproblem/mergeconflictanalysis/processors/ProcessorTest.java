package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.HashMap;

import org.eclipse.jgit.revwalk.RevCommit;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public abstract class ProcessorTest extends MergeGitTest {

	protected static final String FILE_NAME = "A.java";

	public CommitStatus generateCommitStatus() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		return status;
	}

	public CommitStatus crateCommitStatus(String a, String b) {
		CombinedFile combinedFile = new CombinedFile();
		combinedFile.addChunk(ChunkOwner.A, a);
		combinedFile.addChunk(ChunkOwner.B, b);
		
		HashMap<String, CombinedFile> conflictingFiles = new HashMap<String, CombinedFile>();
		conflictingFiles.put(FILE_NAME, combinedFile);
		CommitStatus commitStatus = new CommitStatus(null, null, conflictingFiles, -1);
		return commitStatus;
	}
}