package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;

import java.util.function.BiFunction;

public abstract class DiffFileProcessor implements FileProcessor {
	
	protected String getDiff(String solvedVersion, String aVersion, String bVersion, BiFunction<String, String, Integer> diffFunction) {
		int aToB = -1;
		int aToSolved = -1;
		int bToSolved = -1;
		
		if (aVersion != null && bVersion != null) {
			aToB = diffFunction.apply(aVersion, bVersion);
			if (solvedVersion != null) {
				aToSolved = diffFunction.apply(aVersion, solvedVersion);
				bToSolved = diffFunction.apply(bVersion, solvedVersion);
			}
		}
		String locDiff = aToB + "," + aToSolved + "," + bToSolved;
		return locDiff;
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String diff = getDiff(status.getSolvedVersion(fileName), combinedFile.getVersion(ChunkOwner.A), combinedFile.getVersion(ChunkOwner.B), (a, b) -> getDiffSize(a, b));
		return diff;
	}
	
	public abstract int getDiffSize(String aContent, String bContent);
}
