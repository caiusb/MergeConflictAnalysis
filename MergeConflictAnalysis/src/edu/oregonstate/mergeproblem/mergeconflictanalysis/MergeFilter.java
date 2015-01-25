package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.filter.commit.CommitFilter;

public class MergeFilter extends CommitFilter {

	@Override
	public boolean include(RevWalk walker, RevCommit cmit)
			throws StopWalkException, MissingObjectException,
			IncorrectObjectTypeException, IOException {
		
		RevCommit[] parents = cmit.getParents();
		if (parents.length >= 2)
			return true;
		
		return false;
	}
	
	@Override
	public boolean requiresCommitBody() {
		return true;
	}

}
