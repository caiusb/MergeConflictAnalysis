package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import fr.labri.gumtree.tree.Tree;

public class ASTSizeProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "COMBINED_AST_SIZE_A,COMBINED_AST_SIZE_B,AST_SIZE_SOLVED";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String aSize = "NA";
		String bSize = "NA";
		String solvedSize = "NA";
		ASTDiff astDiff = new ASTDiff();

		aSize = getResultString(astDiff.getTree(combinedFile.getVersion(ChunkOwner.A)));
		bSize = getResultString(astDiff.getTree(combinedFile.getVersion(ChunkOwner.B)));
		solvedSize = getResultString(astDiff.getTree(status.getSolvedVersion(fileName)));

		return aSize + "," + bSize + "," + solvedSize;
	}

	private String getResultString(Tree aTree) {
		if(aTree.getChildren().size() != 0)
			return aTree.getSize() + "";
		else
			return "NA";
	}

}
