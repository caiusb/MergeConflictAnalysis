package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;

public class LOCFileProcessor extends DiffFileProcessor {
	
	private DiffAlgorithm diffAlgorithm = DiffAlgorithm.getAlgorithm(SupportedAlgorithm.MYERS);

	@Override
	public String getHeader() {
		return "LOC_A_TO_B, LOC_A_TO_SOLVED, LOC_B_TO_SOLVED";
	}

	@Override
	public String getDataForMerge(CommitStatus status, String fileName) {
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String locDiff = getDiff(status.getSolvedVersion(fileName), combinedFile.getVersion(ChunkOwner.A), combinedFile.getVersion(ChunkOwner.B), (a, b) -> getLOCDiffSize(a, b));
		return locDiff;
	}

	private int getLOCDiffSize(String aVersion, String bVersion) {
		return diffAlgorithm.diff(RawTextComparator.DEFAULT, new RawText(aVersion.getBytes()), new RawText(bVersion.getBytes())).size();
	}
}
