package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.tree.Tree;

import java.util.List;

public class PreMergeASTSizeProcessor extends AbstractPerMergeASTDiffProcessor{

	@Override
	public String getHeader() {
		return "AST_A_SIZE,AST_B_SIZE";
	}

	@Override
	public String getResults(Tree treeA, Tree treeB, List<Action> actions) {
		return treeA.getSize() + "," + treeB.getSize();
	}
}
