package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;

public class ASTFileProcessor extends DiffFileProcessor {

	@Override
	public String getHeader() {
		return "AST_A_TO_B,AST_A_TO_SOLVED,AST_B_TO_SOLVED";
	}

	@Override
	public int getDiffSize(String aContent, String bContent) {
		return new ASTDiff().getActions(aContent, bContent).size();
	}

}
