package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;

public class ASTFileProcessor extends DiffFileProcessor {

	@Override
	public String getHeader() {
		return "COMBINED_AST_A_TO_B,COMBINED_AST_A_TO_SOLVED,COMBINED_AST_B_TO_SOLVED";
	}

	@Override
	public int getDiffSize(String aContent, String bContent) {
		return new ASTDiff().getActions(aContent, bContent).size();
	}

}
