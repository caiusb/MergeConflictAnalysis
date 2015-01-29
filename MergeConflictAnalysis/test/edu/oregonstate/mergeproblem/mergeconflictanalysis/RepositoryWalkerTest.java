package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.List;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
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
	
	
}
