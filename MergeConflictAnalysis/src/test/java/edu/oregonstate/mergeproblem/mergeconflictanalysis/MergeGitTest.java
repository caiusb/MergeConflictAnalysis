package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.gitective.tests.GitTestCase;
import org.junit.After;
import org.junit.Before;

public abstract class MergeGitTest extends GitTestCase {

	protected Repository repository;
	
	@Before 
	public void before() throws Exception {
		repository = Git.open(testRepo).getRepository();
	}

	@After
	public void deleteRepo() throws Exception {
		testRepo.delete();
	}
	
	protected RevCommit createNonConflictingMerge() throws Exception {
		return createNonConflictingMerge(0);
	}

	protected RevCommit createNonConflictingMerge(int base) throws Exception {
		add("A.java", getBase(base));
		checkoutBranch();
		add("B.java", getBranchVersion(base));
		checkout("master");
		add("A.java", getMasterVersion(base));
		MergeResult mergeResult = merge("branch");
		assertEquals(MergeStatus.MERGED, mergeResult.getMergeStatus());
		ObjectId newHead = mergeResult.getNewHead();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, newHead);
		return mergeCommit;
	}

	protected String getBase(int base) {
		return "public class A" + (base + 1) + "{}";
	}
	
	protected String getBranchVersion(int base) {
		return "public class A" + (base + 2) + "{}";
		
	}
	protected String getMasterVersion(int base) {
		return "public class A" + (base + 3)+ "{}";
	}
	
	protected String getConflictingVersion(int base) {
		return "public class conflictingA" + (base + 3) + "{}";
	}

	protected RevCommit createConflictingCommit() throws Exception {
		MergeResult merge = createConflictingMergeResult(0);
		RevCommit mergeCommit = resolveMergeConflict(merge);
		return mergeCommit;
	}

	protected RevCommit resolveMergeConflict(MergeResult merge)
			throws Exception {
		String fileName = "A.java";
		return resolveMergeConflict(merge, fileName);
	}

	protected RevCommit resolveMergeConflict(MergeResult merge, String fileName) throws Exception {
		return resolveMergeConflict(merge, fileName, "message");
	}

	protected RevCommit resolveMergeConflict(MergeResult merge, String fileName, String message) throws Exception {
		MergeStatus mergeStatus = merge.getMergeStatus();
		assertEquals(MergeStatus.CONFLICTING, mergeStatus);
		assertEquals(1,merge.getConflicts().keySet().size());
		assertTrue(merge.getConflicts().keySet().contains(fileName));
		
		RevCommit mergeCommit = add(fileName,"public class A{}", message);
		assertEquals(2, mergeCommit.getParentCount());
		return mergeCommit;
	}

	protected MergeResult createConflictingMergeResult() throws Exception {
		return createConflictingMergeResult(0);
	}

	protected MergeResult createConflictingMergeResult(int base) throws Exception {
		add("A.java", getBase(base));
		checkoutBranch();
		add("A.java", getBranchVersion(base));
		checkout("master");
		add("A.java", getConflictingVersion(base));
		
		MergeResult merge = merge("branch");
		return merge;
	}

	private void checkoutBranch() throws IOException, Exception {
		if (repository.getRef("branch") == null)
			branch("branch");
		else
			checkout("branch");
	}

	protected File addSubmodule() throws Exception {
		File subRepo = initRepo();
		Git git = Git.wrap(repository);
		git.submoduleAdd().setURI(subRepo.toURI().toASCIIString()).setPath("sub").call();
		git.add().addFilepattern(".gitmodules").addFilepattern("sub").call();
		git.commit().setMessage("Submodule").call();

		return subRepo;
	}
}