package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.gitective.tests.GitTestCase;
import org.junit.Before;

public abstract class MergeGitTest extends GitTestCase {

	protected Repository repository;
	
	@Before 
	public void before() throws Exception {
		repository = Git.open(testRepo).getRepository();
	}
	
	protected RevCommit createNonConflictingMerge() throws Exception {
		add("A", "some content");
		branch("branch");
		add("B", "some other content");
		checkout("master");
		add("A", "a change! ");
		MergeResult mergeResult = merge("branch");
		ObjectId newHead = mergeResult.getNewHead();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, newHead);
		return mergeCommit;
	}

}