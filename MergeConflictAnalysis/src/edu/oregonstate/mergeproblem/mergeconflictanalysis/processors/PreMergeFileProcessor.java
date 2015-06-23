package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.BlobUtils;
import org.gitective.core.CommitUtils;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class PreMergeFileProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "A_BEFORE,B_BEFORE";
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
		String escapedA = StringEscapeUtils.escapeCsv(a);
		String escapedB = StringEscapeUtils.escapeCsv(b);
		return escapedA + "," + escapedB;
	}
}
