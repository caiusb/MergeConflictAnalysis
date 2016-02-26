package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.List;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.ASTDiff;
import fr.labri.gumtree.actions.model.Action;

public class PreMergeASTDiffProcessor extends AbstractPreMergeProcessor {

	@Override
	public String getHeader() {
		return "AST_DIFF_BEFORE";
	}

	@Override
	protected String getResults(String a, String b) {
		List<Action> actions = new ASTDiff().getActions(a, b);
		return actions.size() + "";
	}

}
