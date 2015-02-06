package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.gitective.tests.GitTestCase;
import org.junit.Before;

public abstract class MergeGitTest extends GitTestCase {

	protected Repository repository;
	
	@Before 
	public void before() throws Exception {
		repository = Git.open(testRepo).getRepository();
	}
	
	protected RevCommit createNonConflictingMerge() throws Exception {
		add("A", "" + Math.random());
		checkoutBranch();
		add("B", "" + Math.random());
		checkout("master");
		add("A", "" + Math.random());
		MergeResult mergeResult = merge("branch");
		assertEquals(MergeStatus.MERGED, mergeResult.getMergeStatus());
		ObjectId newHead = mergeResult.getNewHead();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, newHead);
		return mergeCommit;
	}

	protected RevCommit createConflictingCommit() throws Exception {
		MergeResult merge = createConflictingMergeResult();
		RevCommit mergeCommit = resolveMergeConflict(merge);
		return mergeCommit;
	}

	protected RevCommit resolveMergeConflict(MergeResult merge)
			throws Exception {
		MergeStatus mergeStatus = merge.getMergeStatus();
		assertEquals(MergeStatus.CONFLICTING, mergeStatus);
		assertEquals(1,merge.getConflicts().keySet().size());
		assertTrue(merge.getConflicts().keySet().contains("A"));
		
		RevCommit mergeCommit = add("A","version two+three");
		assertEquals(2, mergeCommit.getParentCount());
		return mergeCommit;
	}

	protected MergeResult createConflictingMergeResult() throws Exception {
		add("A", "" + Math.random());
		checkoutBranch();
		add("A", "" + Math.random());
		checkout("master");
		add("A", "conflicting " + Math.random());
		
		MergeResult merge = merge("branch");
		return merge;
	}

	private void checkoutBranch() throws IOException, Exception {
		if (repository.getRef("branch") == null)
			branch("branch");
		else
			checkout("branch");
	}
}