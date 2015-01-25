package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

public class ConflictDetectorTest extends MergeGitTest {
	
	private ConflictDetector conflictDetector;
	private Git git;

	@Before
	public void before() throws Exception {
		super.before();
		conflictDetector = new ConflictDetector();
		git = Git.open(testRepo);
	}
	
	@Test
	public void testSingleParentInput() throws Exception {
		RevCommit one = add("A", "something");
		conflictDetector.isConflict(one, git);
	}
	
	@Test
	public void testNoConflict() throws Exception {
		RevCommit mergeCommit = createNonConflictingMerge();
		assertEquals(2, mergeCommit.getParentCount());
		assertFalse(conflictDetector.isConflict(mergeCommit, git));
	}
	
	@Test
	public void testDetectConflict() throws Exception {
		MergeResult merge = createConflictingCommit();
		assertEquals(1,merge.getConflicts().keySet().size());
		assertTrue(merge.getConflicts().keySet().contains("A"));
		
		RevCommit mergeCommit = add("A","version two+three");
		assertEquals(2, mergeCommit.getParentCount());
		
		assertTrue(conflictDetector.isConflict(mergeCommit, git));
	}
}
