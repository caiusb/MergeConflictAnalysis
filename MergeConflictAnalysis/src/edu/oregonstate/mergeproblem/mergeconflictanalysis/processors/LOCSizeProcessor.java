package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class LOCSizeProcessor implements FileProcessor {

	private static final String NEW_LINE_REGEX = "\n|\r|\r\n";

	@Override
	public String getHeader() {
		return "LOC_SIZE_A,LOC_SIZE_B,LOC_SIZE_SOLVED";
	}

	@Override
	public String getDataForMerge(CommitStatus status, String fileName) {
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String[] aLines = combinedFile.getVersion(ChunkOwner.A).split(NEW_LINE_REGEX);
		String[] bLines = combinedFile.getVersion(ChunkOwner.B).split(NEW_LINE_REGEX);
		String[] solvedLines = status.getSolvedVersion(fileName).split(NEW_LINE_REGEX);
		return aLines.length + "," + bLines.length + "," + solvedLines.length;
	}

}
