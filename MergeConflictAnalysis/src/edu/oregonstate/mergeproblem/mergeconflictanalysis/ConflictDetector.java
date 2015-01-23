package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidMergeHeadsException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.revwalk.RevCommit;

public class ConflictDetector {

	public boolean isConflict(RevCommit mergeCommit, Git git) throws NoHeadException, ConcurrentRefUpdateException, CheckoutConflictException, InvalidMergeHeadsException, WrongRepositoryStateException, NoMessageException, GitAPIException {
		RevCommit[] parents = mergeCommit.getParents();
		RevCommit first = parents[0];
		RevCommit second = parents[1];

		CheckoutCommand checkoutCommand = git.checkout().setName(first.getId().getName());
		checkoutCommand.call();
		MergeCommand merge = git.merge();
		merge.include(second);
		MergeResult mergeResults = merge.call();
		if (mergeResults.getMergeStatus().equals(MergeStatus.CONFLICTING))
			return true;
		
		return false;
	}

}
