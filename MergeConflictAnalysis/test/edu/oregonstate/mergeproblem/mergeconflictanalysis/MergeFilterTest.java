package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.tests.GitTestCase;
import org.junit.Before;
import org.junit.Test;

public class MergeFilterTest extends GitTestCase{
	
	private RevWalk revWalk;
	private MergeFilter mergeFilter;

	@Before
	public void before() throws Exception {
		revWalk = new RevWalk(Git.open(testRepo).getRepository());
		mergeFilter = new MergeFilter();
	}

	@Test
	public void testDontIncludeRegularCommits() throws Exception {
		RevCommit commitOne = add("A", "some content");
		
		
		assertFalse(mergeFilter.include(revWalk, commitOne));
	}
	
}
