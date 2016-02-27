package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

import java.util.List;

import fr.labri.gumtree.actions.ActionGenerator;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.gen.jdt.JdtTreeGenerator;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;

public class ASTDiff {
	
	public List<Action> getActions(String AContent, String BContent) {
		JdtTreeGenerator jdtTreeGenerator = new JdtTreeGenerator();
		Tree leftTree = getTree(AContent, jdtTreeGenerator);
		Tree rightTree = getTree(BContent, jdtTreeGenerator);
		List<Action> actions = getActions(leftTree, rightTree);
		return actions;
	}

	public List<Action> getActions(Tree leftTree, Tree rightTree) {
		List<Action> actions;
		Matcher matcher = MatcherFactories.newMatcher(leftTree, rightTree);
		matcher.match();
		ActionGenerator actionGenerator = new ActionGenerator(leftTree, rightTree, matcher.getMappings());
		actionGenerator.generate();
		actions = actionGenerator.getActions();
		return actions;
	}

	private Tree getTree(String AContent, JdtTreeGenerator jdtTreeGenerator) {
		return jdtTreeGenerator.fromString(AContent);
	}
	
	public Tree getTree(String content) {
		JdtTreeGenerator jdtTreeGenerator = new JdtTreeGenerator();
		return getTree(content, jdtTreeGenerator);
	}
}
