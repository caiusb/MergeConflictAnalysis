package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.revwalk.RevCommit;

public class ConflictDetector {

	public boolean isConflict(RevCommit mergeCommit, Git git) throws Exception {
		RevCommit[] parents = mergeCommit.getParents();
		if (parents.length <= 1)
			return false;

		RevCommit first = parents[0];
		RevCommit second = parents[1];

		MergeResult mergeResults = merge(git, first, second);
		if (mergeResults.getMergeStatus().equals(MergeStatus.CONFLICTING))
			return true;

		return false;
	}

	private MergeResult merge(Git git, RevCommit first, RevCommit second)
			throws Exception {
		CheckoutCommand checkoutCommand = git.checkout().setName(first.getId().getName());
		checkoutCommand.call();
		MergeCommand merge = git.merge();
		merge.include(second);
		MergeResult mergeResults = merge.call();
		return mergeResults;
	}

}
