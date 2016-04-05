package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.List;

import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.tree.Tree;

public class PreMergeASTDiffProcessor extends AbstractPerMergeASTDiffProcessor {

	@Override
	public String getHeader() {
		return "AST_DIFF";
	}

	@Override
	public String getResults(Tree a, Tree b, List<Action> actions) {
		return actions.size() + "";
	}

}
