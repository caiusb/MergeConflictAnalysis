package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;

public class ASTSizeProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "AST_SIZE_A,AST_SIZE_B,AST_SIZE_SOLVED";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		int aSize = -1;
		int bSize = -1;
		int solvedSize = -1;
		ASTDiff astDiff = new ASTDiff();

		aSize = astDiff.getTree(combinedFile.getVersion(ChunkOwner.A)).getSize();
		bSize = astDiff.getTree(combinedFile.getVersion(ChunkOwner.B)).getSize();
		solvedSize = astDiff.getTree(status.getSolvedVersion(fileName)).getSize();
		
		return aSize + "," + bSize + "," + solvedSize;
	}

}
