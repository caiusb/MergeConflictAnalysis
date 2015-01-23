package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.tests.GitTestCase;
import org.junit.Before;
import org.junit.Test;

public class MergeFilterTest extends GitTestCase{
	
	private RevWalk revWalk;

	@Before
	public void before() throws Exception {
		revWalk = new RevWalk(Git.open(testRepo).getRepository());
	}

	@Test
	public void testDontIncludeRegularCommits() throws Exception {
		RevCommit commitOne = add("A", "some content");
		
		MergeFilter mergeFilter = new MergeFilter();
		
		assertFalse(mergeFilter.include(revWalk, commitOne));
	}
	
}
