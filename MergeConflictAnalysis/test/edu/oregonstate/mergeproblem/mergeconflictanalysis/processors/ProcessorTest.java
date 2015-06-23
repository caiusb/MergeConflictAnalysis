package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public abstract class ProcessorTest extends MergeGitTest {

	public CommitStatus generateCommitStatus() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		return status;
	}

}