package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class IsConflictProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "IS_CONFLICT";
	}

	@Override
	public String getDataForMerge(CommitStatus status, String fileName) {
		boolean isConflict = status.getListOfConflictingFiles().contains(fileName);
		if (isConflict) {
			return "true";
		}
		return "false";
	}

}
