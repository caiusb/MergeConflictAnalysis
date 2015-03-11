package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleStatus;
import org.gitective.core.CommitUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MergeRecreatorTest extends MergeGitTest {
	
	private MergeRecreator mergeRecreator;
	private Git git;

	@Before
	public void before() throws Exception {
		super.before();
		mergeRecreator = new MergeRecreator();
		git = Git.open(testRepo);
	}
	
	@Test
	public void testSingleParentInput() throws Exception {
		RevCommit one = add("A", "something");
		mergeRecreator.recreateMerge(one, git);
	}
	
	@Test
	public void testNoConflict() throws Exception {
		RevCommit mergeCommit = createNonConflictingMerge();
		assertEquals(2, mergeCommit.getParentCount());
		assertFalse(mergeRecreator.recreateMerge(mergeCommit, git));
	}
	
	@Test
	public void testDetectConflict() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		assertTrue(mergeRecreator.recreateMerge(mergeCommit, git));
	}
	
	@Test
	public void testConflictStatus() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		assertTrue(mergeRecreator.recreateMerge(mergeCommit, git));
		MergeResult mergeResult = mergeRecreator.getLastMergeResult();
		assertEquals(MergeStatus.CONFLICTING, mergeResult.getMergeStatus());
	}
	
	@Test
	public void testResetWorkspaceAfterConflict() throws Exception {
		RevCommit commit = createConflictingCommit();
		mergeRecreator.recreateMerge(commit, git);
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
		
		assertTrue(mergeRecreator.recreateMerge(mergeCommit, git));
	}
	
	@Test
	public void testTwoMergeConflicts() throws Exception {
		RevCommit firstMerge = createConflictingCommit();
		RevCommit secondMerge = createNonConflictingMerge();
		assertTrue(mergeRecreator.recreateMerge(firstMerge, git));
		assertFalse(mergeRecreator.recreateMerge(secondMerge, git));
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
		assertTrue(mergeRecreator.recreateMerge(fix, git));
		RevCommit secondOne = createConflictingCommit();
		assertTrue(mergeRecreator.recreateMerge(secondOne, git));
	}
	
	@Test(expected=SubmoduleDetectedException.class)
	public void testDetectSubmodule() throws Exception {
		addSubmodule();
		RevCommit commit = createConflictingCommit();
		mergeRecreator.recreateMerge(commit, git);
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
		
		assertTrue(mergeRecreator.recreateMerge(conflict0, git));
		assertTrue(mergeRecreator.recreateMerge(conflict1, git));
		assertTrue(mergeRecreator.recreateMerge(conflict2, git));
	}
	
	@Test
	public void testConcurrentlyChangedFilesCleanMerge() throws Exception {
		add("A.java", "public class A{\n\n}\n");
		branch("second");
		add("A.java", "public class B{\n\n}\n");
		checkout("master");
		add("A.java", "public class A{\n\npublic void m(){}\n}\n}");
		
		MergeResult mergeResult = merge("second");
		assertTrue(mergeResult.getMergeStatus().equals(MergeStatus.MERGED));
		ObjectId newHeadID = mergeResult.getNewHead();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, newHeadID);
		mergeRecreator.recreateMerge(mergeCommit, git);
		MergeResult recreatedMergeResult = mergeRecreator.getLastMergeResult();
		assertTrue(recreatedMergeResult.getMergeStatus().equals(MergeStatus.MERGED));
	}
	
}
