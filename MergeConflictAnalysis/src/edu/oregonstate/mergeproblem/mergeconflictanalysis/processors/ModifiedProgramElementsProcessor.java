package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

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
		return "NO_METHODS,NO_CLASSES";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		List<ASTNode> knownMethods = new ArrayList<ASTNode>();
		List<ASTNode> knownClasses = new ArrayList<ASTNode>();
		
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String aVersion = combinedFile.getVersion(ChunkOwner.A);
		String bVersion = combinedFile.getVersion(ChunkOwner.B);
		
		List<Action> actions = new ASTDiff().getActions(aVersion, bVersion);
		for (Action action : actions) {
			Tree node = action.getNode();
			if (!(node instanceof JdtTree))
				continue;
			JdtTree jdtNode = (JdtTree) node;
			processNode(knownMethods, knownClasses, jdtNode);
		}
		
		return knownMethods.size() + "," + knownClasses.size();
	}

	private void processNode(List<ASTNode> knownMethods,
			List<ASTNode> knownClasses, JdtTree jdtNode) {
		if (jdtNode.getType() == ASTNode.METHOD_DECLARATION) {
			ASTNode containedNode = jdtNode.getContainedNode();
			if (!knownMethods.contains(containedNode)) {
				knownMethods.add(containedNode);
			}
		}
		if (jdtNode.getType() == ASTNode.TYPE_DECLARATION) {
			ASTNode containedNode = jdtNode.getContainedNode();
			if (!knownClasses.contains(containedNode))
				knownClasses.add(containedNode);
			return;
		}
		
		List<Tree> parents = jdtNode.getParents();
		for (Tree parent : parents) {
			if (parent instanceof JdtTree)
				processNode(knownMethods, knownClasses, (JdtTree) parent);
		}
	}
	
}
