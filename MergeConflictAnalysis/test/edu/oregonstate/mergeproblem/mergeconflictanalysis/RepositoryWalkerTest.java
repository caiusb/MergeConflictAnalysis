package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.List;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.junit.Before;
import org.junit.Test;

public class RepositoryWalkerTest extends MergeGitTest {

	private RepositoryWalker walker;

	@Before
	@Override
	public void before() throws Exception {
		super.before();
		walker = new RepositoryWalker(repository);
	}
	
	@Test
	public void testGetOneCommit() throws Exception {
		RevCommit mergeCommit = createNonConflictingMerge();
		walker.walk();
		List<RevCommit> mergeCommits = walker.getMergeCommits();
		assertEquals(1, mergeCommits.size());
		assertTrue(mergeCommits.contains(mergeCommit));
	}
	
	@Test
	public void testTwoCommitsInChronologicalOrder() throws Exception {
		RevCommit mergeCommit1 = createNonConflictingMerge();
		Thread.sleep(1000); // so I get different time stamps
		RevCommit mergeCommit2 = createSecondNonConflictingCommit();
		
		walker.walk();
		List<RevCommit> mergeCommits = walker.getMergeCommits();
		assertEquals(2, mergeCommits.size());
		assertTrue(mergeCommits.contains(mergeCommit1));
		assertTrue(mergeCommits.contains(mergeCommit2));
		
		RevCommit first = mergeCommits.get(0);
		RevCommit second = mergeCommits.get(1);
		assertFalse(first.getCommitTime() == 0);
		assertFalse(second.getCommitTime() == 0);
		assertTrue(first.getCommitTime() < second.getCommitTime());
	}

	private RevCommit createSecondNonConflictingCommit() throws Exception {
		branch("branch2");
		add("B","four");
		checkout("master");
		add("A", "five");
		MergeResult mergeResult2 = merge("branch2");
		assertEquals(MergeStatus.MERGED, mergeResult2.getMergeStatus());
		RevCommit mergeCommit2 = CommitUtils.getCommit(repository, mergeResult2.getNewHead());
		return mergeCommit2;
	}
}
