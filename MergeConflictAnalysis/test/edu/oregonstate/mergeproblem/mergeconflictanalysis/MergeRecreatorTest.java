package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MergeRecreatorTest extends MergeGitTest {
	
	private MergeRecreator conflictDetector;
	private Git git;

	@Before
	public void before() throws Exception {
		super.before();
		conflictDetector = new MergeRecreator();
		git = Git.open(testRepo);
	}
	
	@Test
	public void testSingleParentInput() throws Exception {
		RevCommit one = add("A", "something");
		conflictDetector.recreateMerge(one, git);
	}
	
	@Test
	public void testNoConflict() throws Exception {
		RevCommit mergeCommit = createNonConflictingMerge();
		assertEquals(2, mergeCommit.getParentCount());
		assertFalse(conflictDetector.recreateMerge(mergeCommit, git));
	}
	
	@Test
	public void testDetectConflict() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		assertTrue(conflictDetector.recreateMerge(mergeCommit, git));
	}
	
	@Test
	public void testConflictStatus() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		assertTrue(conflictDetector.recreateMerge(mergeCommit, git));
		MergeResult mergeResult = conflictDetector.getLastMergeResult();
		assertEquals(MergeStatus.CONFLICTING, mergeResult.getMergeStatus());
	}
	
	@Test
	public void testResetWorkspaceAfterConflict() throws Exception {
		RevCommit commit = createConflictingCommit();
		conflictDetector.recreateMerge(commit, git);
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
		
		assertTrue(conflictDetector.recreateMerge(mergeCommit, git));
	}
	
	@Test
	public void testTwoMergeConflicts() throws Exception {
		RevCommit firstMerge = createConflictingCommit();
		RevCommit secondMerge = createNonConflictingMerge();
		assertTrue(conflictDetector.recreateMerge(firstMerge, git));
		assertFalse(conflictDetector.recreateMerge(secondMerge, git));
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
		assertTrue(conflictDetector.recreateMerge(fix, git));
		RevCommit secondOne = createConflictingCommit();
		assertTrue(conflictDetector.recreateMerge(secondOne, git));
	}
	
	@Test(expected=SubmoduleDetectedException.class)
	public void testDetectSubmodule() throws Exception {
		addSubmodule();
		RevCommit commit = createConflictingCommit();
		conflictDetector.recreateMerge(commit, git);
	}
	
	@Test
	@Ignore
	public void testDeletedSubmodule() throws Exception {
		RevCommit conflict0 = createConflictingCommit();
		File submodule = addSubmodule();
		add(submodule, "bla.txt", "bla bla bla boring");
		git.add().addFilepattern("sub").call();
		git.commit().setMessage("Change to submodule at " + new Date()).call();
		RevCommit conflict1 = createConflictingCommit();
		submodule.delete();
		Map<String, SubmoduleStatus> submoduleStatus = git.submoduleStatus().call();
		git.rm().addFilepattern("sub").call();
		git.commit().setMessage("Removed submodule").call();
		new File(testRepo.toString(), "sub").mkdir();
		git.checkout().setName(conflict1.getName()).call();
		git.checkout().setName("master").call();
		
		RevCommit conflict2 = createConflictingCommit();
		
		Iterable<RevCommit> log = git.log().call();
		Iterator<RevCommit> iterator = log.iterator();
		while (iterator.hasNext()) {
			RevCommit revCommit = (RevCommit) iterator.next();
			System.out.println(revCommit.getName() + ": " + revCommit.getFullMessage() + ": "+ revCommit.getParentCount());
		}
		
		assertTrue(conflictDetector.recreateMerge(conflict0, git));
		assertTrue(conflictDetector.recreateMerge(conflict1, git));
		assertTrue(conflictDetector.recreateMerge(conflict2, git));
	}
}
