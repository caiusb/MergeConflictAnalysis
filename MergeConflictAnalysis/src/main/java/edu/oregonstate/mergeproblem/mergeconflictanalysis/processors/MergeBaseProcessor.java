package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;

public class MergeBaseProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "BASE_SHA,BASE_TIME";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		String mergeSHA = status.getSHA1();
		Repository repository = status.getRepository();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, mergeSHA);
		RevCommit[] parents = mergeCommit.getParents();
		RevCommit base = CommitUtils.getBase(repository, parents[0], parents[1]);
		if (base == null) {
			return "NO NAME,-1";
		}
		return base.getName() + "," + base.getCommitTime();
	}

}
