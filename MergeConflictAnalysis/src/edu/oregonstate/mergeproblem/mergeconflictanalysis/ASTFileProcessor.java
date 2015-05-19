package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;

public class ASTFileProcessor extends DiffFileProcessor {

	@Override
	public String getHeader() {
		return "AST_A_TO_B, AST_A_TO_SOLVED, AST_B_TO_SOLVED";
	}

	@Override
	public int getDiffSize(String aContent, String bContent) {
		try {
			return new ASTDiff().getActions(aContent, bContent).size();
		} catch (IOException e) {
			return -1;
		}
	}

}
