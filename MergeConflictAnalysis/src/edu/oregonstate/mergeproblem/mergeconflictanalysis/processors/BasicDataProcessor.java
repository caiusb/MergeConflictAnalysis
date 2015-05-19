package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class BasicDataProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "SHA, FILE, TIME_A, TIME_B, TIME_SOLVED";
	}

	@Override
	public String getDataForMerge(CommitStatus status, String fileName) {
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		return status.getSHA1() + "," + fileName + "," + combinedFile.getATime() + "," + combinedFile.getBTime() + "," + status.getSolvedTime();
	}

}
