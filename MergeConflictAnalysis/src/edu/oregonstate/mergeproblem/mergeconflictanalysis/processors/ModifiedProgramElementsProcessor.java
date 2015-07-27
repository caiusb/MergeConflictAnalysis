package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.ASTDiff;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.gen.jdt.JdtTree;
import fr.labri.gumtree.tree.Tree;

public class ModifiedProgramElementsProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "NO_METHODS,NO_CLASSES,NO_STATEMENTS";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		List<ASTNode> knownMethods = new ArrayList<ASTNode>();
		List<ASTNode> knownClasses = new ArrayList<ASTNode>();
		List<ASTNode> knownStatements = new ArrayList<ASTNode>();
		List<ASTNode> seenNodes = new ArrayList<ASTNode>();
		
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String aVersion = combinedFile.getVersion(ChunkOwner.A);
		String bVersion = combinedFile.getVersion(ChunkOwner.B);
		
		List<Action> actions = new ASTDiff().getActions(aVersion, bVersion);
		for (Action action : actions) {
			Tree node = action.getNode();
			if (!(node instanceof JdtTree))
				continue;
			JdtTree jdtNode = (JdtTree) node;
			processNode(knownMethods, knownClasses, knownStatements, seenNodes, jdtNode);
		}
		
		return knownMethods.size() + "," + knownClasses.size() + "," + knownStatements.size();
	}

	private void processNode(List<ASTNode> knownMethods,
			List<ASTNode> knownClasses, List<ASTNode> knownStatements, List<ASTNode> seenNodes, JdtTree jdtNode) {
		ASTNode containedNode = jdtNode.getContainedNode();
		if (seenNodes.contains(containedNode))
			return;
		seenNodes.add(containedNode);
		
		if (jdtNode.getType() == ASTNode.METHOD_DECLARATION) {
			if (!knownMethods.contains(containedNode)) {
				knownMethods.add(containedNode);
			}
		}
		if (containedNode instanceof Statement) {
			if (!knownStatements.contains(containedNode))
				knownStatements.add(containedNode);
		}
		if (jdtNode.getType() == ASTNode.TYPE_DECLARATION) {
			if (!knownClasses.contains(containedNode))
				knownClasses.add(containedNode);
			return;
		}
		
		
		List<Tree> parents = jdtNode.getParents();
		for (Tree parent : parents) {
			if (parent instanceof JdtTree)
				processNode(knownMethods, knownClasses, knownStatements, seenNodes, (JdtTree) parent);
		}
	}
	
}
