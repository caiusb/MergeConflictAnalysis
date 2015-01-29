package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.CommitUtils;

public class RepositoryWalker {

	private Repository repository;
	private List<RevCommit> mergeCommits;

	public RepositoryWalker(Repository repository) {
		this.repository = repository;
		this.mergeCommits = new ArrayList<RevCommit>();
	}

	public List<RevCommit> getMergeCommits() {
		return mergeCommits;
	}

	public void walk() throws WalkException {
		RevWalk revWalk = new RevWalk(repository);
		revWalk.setRevFilter(new MergeFilter());
		RevCommit start = CommitUtils.getCommit(repository, Constants.HEAD);
		try {
			revWalk.markStart(start);
		} catch (IOException e) {
			throw new WalkException(e);
		}
		Iterator<RevCommit> mergeCommits = revWalk.iterator();
		while (mergeCommits.hasNext()) {
			RevCommit revCommit = (RevCommit) mergeCommits.next();
			this.mergeCommits.add(revCommit);
		}
		this.mergeCommits.sort(new Comparator<RevCommit>() {

			@Override
			public int compare(RevCommit c1, RevCommit c2) {
				if (c1.getCommitTime() < c2.getCommitTime())
					return -1;
				else if (c1.getCommitTime() == c2.getCommitTime())
					return 0;
				else
					return 1;
			}
		});
	}

}
