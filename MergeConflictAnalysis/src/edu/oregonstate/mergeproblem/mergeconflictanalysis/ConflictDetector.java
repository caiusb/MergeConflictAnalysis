package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.revwalk.RevCommit;

public class ConflictDetector {

	private MergeResult mergeResult;

	public boolean isConflict(RevCommit mergeCommit, Git git) throws Exception {
		RevCommit[] parents = mergeCommit.getParents();
		if (parents.length <= 1)
			return false;

		RevCommit first = parents[0];
		RevCommit second = parents[1];

		try {
			mergeResult = merge(git, first, second);
		} catch (CheckoutConflictException e) {
			return false;
		}
		if (mergeResult.getMergeStatus().equals(MergeStatus.CONFLICTING)) {
			git.reset().setMode(ResetType.HARD).setRef(mergeCommit.getName()).call();
			return true;
		}
		
		return false;
	}

	private MergeResult merge(Git git, RevCommit first, RevCommit second)
			throws Exception {
		Status status = git.status().call();
		if (!status.isClean())
			git.checkout().setAllPaths(true).call();
		
		CheckoutCommand checkoutCommand = git.checkout().setName(first.getName()).setForce(true);
		try {
			checkoutCommand.call();
		} catch (CheckoutConflictException e) {
			System.out.println("Error checking out "  + first.getName());
			throw e;
		}
		MergeCommand merge = git.merge();
		merge.include(second);
		MergeResult mergeResults = merge.call();
		return mergeResults;
	}

	public MergeResult getLastMergeResult() {
		return mergeResult;
	}

}
