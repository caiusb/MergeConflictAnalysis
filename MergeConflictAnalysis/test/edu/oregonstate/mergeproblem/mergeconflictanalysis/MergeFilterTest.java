package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.Before;
import org.junit.Test;

public class MergeFilterTest extends MergeGitTest{
	
	private RevWalk revWalk;
	private MergeFilter mergeFilter;

	@Before
	public void before() throws Exception {
		super.before();
		revWalk = new RevWalk(repository);
		mergeFilter = new MergeFilter();
	}

	@Test
	public void testDontIncludeRegularCommits() throws Exception {
		RevCommit commitOne = add("A", "some content");
		
		
		assertFalse(mergeFilter.include(revWalk, commitOne));
	}
	
	@Test
	public void testIncludeMergeCommits() throws Exception {
		RevCommit mergeCommit = createNonConflictingMerge();
		assertTrue(mergeFilter.include(revWalk, mergeCommit));
	}
	
}
