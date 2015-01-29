package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.revwalk.RevCommit;

public class ConflictDetector {

	private MergeResult mergeResult;

	public boolean isConflict(RevCommit mergeCommit, Git git) throws Exception {
		RevCommit[] parents = mergeCommit.getParents();
		if (parents.length <= 1)
			return false;

		RevCommit first = parents[0];
		RevCommit second = parents[1];

		mergeResult = merge(git, first, second);
		if (mergeResult.getMergeStatus().equals(MergeStatus.CONFLICTING)) {
			git.reset().setMode(ResetType.HARD).setRef("master").call();
			return true;
		}
		
		git.reset().setMode(ResetType.HARD).setRef("master").call();

		return false;
	}

	private MergeResult merge(Git git, RevCommit first, RevCommit second)
			throws Exception {
		git.reset().setMode(ResetType.HARD).setRef("master").call();
		CheckoutCommand checkoutCommand = git.checkout().setName(first.getName()).setForce(true);
		checkoutCommand.call();
		MergeCommand merge = git.merge();
		merge.include(second);
		MergeResult mergeResults = merge.call();
		return mergeResults;
	}

	public MergeResult getLastMergeResult() {
		return mergeResult;
	}

}
