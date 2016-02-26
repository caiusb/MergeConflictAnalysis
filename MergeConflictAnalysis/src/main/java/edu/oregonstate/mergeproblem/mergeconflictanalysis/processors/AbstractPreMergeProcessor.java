package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.BlobUtils;
import org.gitective.core.CommitUtils;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public abstract class AbstractPreMergeProcessor implements FileProcessor {

	public AbstractPreMergeProcessor() {
		super();
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		String conflictingCommitSHA = status.getSHA1();
		Repository repository = status.getRepository();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, conflictingCommitSHA);
		RevCommit[] parents = mergeCommit.getParents();
		if (parents.length != 2)
			return ",";
		RevCommit second;
		RevCommit first;
		if (parents[0].getCommitTime() < parents[1].getCommitTime()) {
			first = parents[0];
			second = parents[1];
		} else {
			first = parents[1];
			second = parents[0];
		}
		String a = BlobUtils.getContent(repository, first, fileName);
		String b = BlobUtils.getContent(repository, second, fileName);
		
		if (a == null)
			a = "";
		if (b == null)
			b = "";
		
		return getResults(a, b);
	}

	protected abstract String getResults(String a, String b);
}