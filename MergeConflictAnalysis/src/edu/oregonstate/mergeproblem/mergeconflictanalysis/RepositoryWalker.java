package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.CommitUtils;

public class RepositoryWalker {

	private Repository repository;

	public RepositoryWalker(Repository repository) {
		this.repository = repository;
	}

	public List<RevCommit> getMergeCommits() throws WalkException {
		Iterator<RevCommit> mergeCommitsIterator = getMergeCommitsIterator();
		ArrayList<RevCommit> mergeCommits = populateList(mergeCommitsIterator);
		mergeCommits.sort((RevCommit c1, RevCommit c2) -> Integer.compare(c1.getCommitTime(), c2.getCommitTime()));
		return mergeCommits;
	}

	private ArrayList<RevCommit> populateList(Iterator<RevCommit> mergeCommitsIterator) {
		ArrayList<RevCommit> mergeCommits = new ArrayList<RevCommit>();
		while (mergeCommitsIterator.hasNext()) {
			RevCommit revCommit = (RevCommit) mergeCommitsIterator.next();
			mergeCommits.add(revCommit);
		}
		return mergeCommits;
	}

	private Iterator<RevCommit> getMergeCommitsIterator() throws WalkException {
		RevWalk revWalk = new RevWalk(repository);
		revWalk.setRevFilter(new MergeFilter());
		RevCommit start = CommitUtils.getCommit(repository, Constants.HEAD);
		try {
			revWalk.markStart(start);
		} catch (IOException e) {
			throw new WalkException(e);
		}
		Iterator<RevCommit> mergeCommitsIterator = revWalk.iterator();
		return mergeCommitsIterator;
	}

}
