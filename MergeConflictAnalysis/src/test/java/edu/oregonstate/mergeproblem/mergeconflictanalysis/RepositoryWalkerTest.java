package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
		List<RevCommit> mergeCommits = walker.getMergeCommits();
		assertEquals(1, mergeCommits.size());
		assertTrue(mergeCommits.contains(mergeCommit));
	}
	
	@Test
	public void testTwoCommitsInChronologicalOrder() throws Exception {
		RevCommit mergeCommit1 = createNonConflictingMerge(0);
		Thread.sleep(1000); // so I get different time stamps
		RevCommit mergeCommit2 = createNonConflictingMerge(4);
		
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
}
