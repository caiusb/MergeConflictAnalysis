package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.CommitUtils;
import org.gitective.tests.GitTestCase;
import org.junit.Before;
import org.junit.Test;

public class MergeFilterTest extends GitTestCase{
	
	private RevWalk revWalk;
	private Repository repository;
	private MergeFilter mergeFilter;

	@Before
	public void before() throws Exception {
		repository = Git.open(testRepo).getRepository();
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
		add("A", "some content");
		branch("branch");
		add("B", "some other content");
		checkout("master");
		add("A", "a change! ");
		MergeResult mergeResult = merge("branch");
		ObjectId newHead = mergeResult.getNewHead();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, newHead);
		assertTrue(mergeFilter.include(revWalk, mergeCommit));
	}
		
}
