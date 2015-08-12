package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class BasicDataProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "SHA,FILE,TIME_A,TIME_B,TIME_SOLVED,SHA_A,SHA_B,TIME_OFFSET";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		return status.getSHA1() + "," + fileName + "," + combinedFile.getATime() + "," + combinedFile.getBTime() + "," + status.getSolvedTime()
			+ "," + status.getASHA() + "," + status.getBSHA();
	}
}
