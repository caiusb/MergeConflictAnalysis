package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
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
		RevCommit mergeCommit = createConflictingCommit();
		assertTrue(conflictDetector.isConflict(mergeCommit, git));
	}
	
	@Test
	public void testConflictStatus() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		assertTrue(conflictDetector.isConflict(mergeCommit, git));
		MergeResult mergeResult = conflictDetector.getLastMergeResult();
		assertEquals(MergeStatus.CONFLICTING, mergeResult.getMergeStatus());
	}
	
	@Test
	public void testResetWorkspaceAfterConflict() throws Exception {
		RevCommit commit = createConflictingCommit();
		conflictDetector.isConflict(commit, git);
		Set<String> conflictingFiles = git.status().call().getConflicting();
		assertEquals(0, conflictingFiles.size());
		assertTrue(git.status().call().isClean());
	}
	
	@Test
	public void testMergeConflictInReverseChronologicalOrder() throws Exception {
		add("A", "one");
		
		branch("branch");
		checkout("master");
		add("A","two");
		
		checkout("branch");
		add("A","three");
		checkout("master");
		
		MergeResult mergeResult = merge("branch");
		assertEquals(MergeStatus.CONFLICTING, mergeResult.getMergeStatus());
		RevCommit mergeCommit = add("A","three");
		
		assertTrue(conflictDetector.isConflict(mergeCommit, git));
	}
	
	@Test
	public void testTwoMergeConflicts() throws Exception {
		RevCommit firstMerge = createConflictingCommit();
		RevCommit secondMerge = createNonConflictingMerge();
		assertTrue(conflictDetector.isConflict(firstMerge, git));
		assertFalse(conflictDetector.isConflict(secondMerge, git));
	}
	
	@Test
	public void testConflictWithDelete() throws Exception {
		add("A","one");
		branch("branch");
		add("A","two");
		checkout("master");
		delete("A");
		MergeResult merge = merge("branch");
		assertTrue(merge.getMergeStatus().equals(MergeStatus.CONFLICTING));
		RevCommit fix = add("A", "bla");
		assertTrue(conflictDetector.isConflict(fix, git));
		RevCommit secondOne = createConflictingCommit();
		assertTrue(conflictDetector.isConflict(secondOne, git));
	}
	
	@Test(expected=SubmoduleDetectedException.class)
	public void testDetectSubmodule() throws Exception {
		File subRepo = initRepo();
		Repository submodule = git.submoduleAdd().setURI(subRepo.toURI().toASCIIString()).setPath("sub").call();
		git.add().addFilepattern(".gitmodules").addFilepattern("sub").call();
		git.commit().setMessage("Submodule").call();
		RevCommit commit = createConflictingCommit();
		conflictDetector.isConflict(commit, git);
	}
}
