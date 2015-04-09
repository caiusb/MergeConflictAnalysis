package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

public class InMemoryMergerTest extends MergeGitTest {
	
	private InMemoryMerger merger;
	
	@Before
	public void beforeTest() {
		merger = new InMemoryMerger(repository);
	}
	
	@Test
	public void testEmptyMerge() throws Exception {
		RevCommit commit = createConflictingCommit();
		RevCommit[] parents = commit.getParents();
		assertEquals(2, parents.length);
		
		RevCommit first = parents[0];
		RevCommit second = parents[1];
		
		merger.merge(first, second);
	}

}
