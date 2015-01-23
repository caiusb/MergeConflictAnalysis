package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.gitective.tests.GitTestCase;
import org.junit.Before;
import org.junit.Test;

public class ConflictDetectorTest extends GitTestCase{
	
	private ConflictDetector conflictDetector;
	private Git git;

	@Before
	public void before() throws Exception {
		conflictDetector = new ConflictDetector();
		git = Git.open(testRepo);
	}
	
	@Test
	public void testSingleParentInput() throws Exception {
		RevCommit one = add("A", "something");
		conflictDetector.isConflict(one, git);
	}
	
	@Test
	public void testDetectConflict() throws Exception {
		add("A", "version one");
		branch("branch");
		add("A", "version two");
		checkout("master");
		add("A", "conflicting version three");
		
		MergeResult merge = merge("branch");
		MergeStatus mergeStatus = merge.getMergeStatus();
		assertEquals(MergeStatus.CONFLICTING, mergeStatus);
		assertEquals(1,merge.getConflicts().keySet().size());
		assertTrue(merge.getConflicts().keySet().contains("A"));
		
		RevCommit mergeCommit = add("A","version two+three");
		assertEquals(2, mergeCommit.getParentCount());
		
		assertTrue(conflictDetector.isConflict(mergeCommit, git));
	}
}
