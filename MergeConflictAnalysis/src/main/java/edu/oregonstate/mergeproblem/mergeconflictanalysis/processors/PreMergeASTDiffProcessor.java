package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff;
import fr.labri.gumtree.actions.model.Action;

import java.util.List;

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
