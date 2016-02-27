package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;
import fr.labri.gumtree.tree.Tree;

public class PreMergeASTSizeProcessor extends AbstractPreMergeProcessor {

	@Override
	public String getHeader() {
		return "AST_A_BEFORE_SIZE,AST_B_BEFORE_SIZE";
	}

	@Override
	protected String getResults(String a, String b) {
		Tree treeA = new ASTDiff().getTree(a);
		Tree treeB = new ASTDiff().getTree(b);
		return treeA.getSize() + "," + treeB.getSize();
	}
}