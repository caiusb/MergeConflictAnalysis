package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.logging.Logger;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.revwalk.RevCommit;

public class MergeRecreator {

	private MergeResult mergeResult;
	private Logger logger = Logger.getLogger(Main.LOGGER_NAME);
	
	public boolean recreateMerge(RevCommit mergeCommit, Git git) throws Exception {
		RevCommit[] parents = mergeCommit.getParents();
		if (parents.length <= 1)
			return false;

		RevCommit first = parents[0];
		RevCommit second = parents[1];

		try {
			mergeResult = merge(git, first, second);
		} catch (CheckoutConflictException e) {
			logger.severe("Error on replicating merge: " + mergeCommit.getName());
			throw new MergingException();
		} catch (JGitInternalException e) {
			logger.severe("Internal exception occured when trying to replicate " + mergeCommit.getName() + "\n" + e);
			throw new MergingException();
		}
		
		if (mergeResult == null) {
			logger.severe("Merge result is null for replicating " + mergeCommit.getName());
			throw new MergingException();
		}
		if (mergeResult.getMergeStatus() == null) {
			logger.severe("Merge status is null for replicating " + mergeCommit.getName());
			throw new MergingException();
		}
		
		if (mergeResult.getMergeStatus().equals(MergeStatus.CONFLICTING)) {
			git.reset().setMode(ResetType.HARD).setRef(mergeCommit.getName()).call();
			return true;
		}
		
		return false;
	}

	private MergeResult merge(Git git, RevCommit first, RevCommit second)
			throws Exception {
		
		Iterable<RevCommit> versions = git.log().addPath(".gitmodules").call();
		if (versions.iterator().hasNext())
			throw new SubmoduleDetectedException();
		
		Status status = git.status().call();
		if (!status.isClean()) {
			logger.fine("Working directory was dirty before checking out " + first.getName());
			git.checkout().setAllPaths(true).setForce(true).call();
		}
		
		CheckoutCommand checkoutCommand = git.checkout().setName(first.getName());
		checkoutCommand.call();
		MergeCommand merge = git.merge();
		merge.include(second);
		MergeResult mergeResults = merge.call();
		logger.finer("Merging " + first.getName() + " with " + second.getName() + " resulted in a " + mergeResults.getMergeStatus());
		return mergeResults;
	}

	public MergeResult getLastMergeResult() {
		return mergeResult;
	}

}
