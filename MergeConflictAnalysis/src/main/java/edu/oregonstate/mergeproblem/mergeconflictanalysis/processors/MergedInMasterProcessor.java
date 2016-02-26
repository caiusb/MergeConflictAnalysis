package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.regex.Pattern;

import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class MergedInMasterProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "MERGED_IN_MASTER";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		String sha1 = status.getSHA1();
		RevCommit commit = CommitUtils.getCommit(status.getRepository(), sha1);
		String fullMessage = commit.getFullMessage();
		Pattern pullReqPattern = Pattern.compile("\\s*Merge pull request #[0-9]* from .*", Pattern.DOTALL);
		if (pullReqPattern.matcher(fullMessage).matches())
			return "True";
		if (fullMessage.matches("Merge branch '(([^m].[^a].[^s].[^t].[^e].[^r].)|[^\\s]*)' of [^\\s]* into master.*"))
			return "True";
		return "False";
	}

}
