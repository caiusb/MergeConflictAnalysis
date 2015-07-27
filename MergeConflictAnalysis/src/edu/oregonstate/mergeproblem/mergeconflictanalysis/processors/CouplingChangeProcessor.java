package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.ASTDiff;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.actions.model.Addition;
import fr.labri.gumtree.actions.model.Update;
import fr.labri.gumtree.gen.jdt.JdtTree;
import fr.labri.gumtree.tree.Tree;

public class CouplingChangeProcessor extends AbstractPreMergeProcessor {

	@Override
	public String getHeader() {
		return "COUPLING_CHANGE";
	}

	@Override
	protected String getResults(String a, String b) {
		Set<String> types = new HashSet<String>();
		
		ASTDiff astDiff = new ASTDiff();
		JdtTree aTree = (JdtTree) astDiff.getTree(a);
		Set<String> firstTypes = getTypes(aTree);
		JdtTree bTree = (JdtTree) astDiff.getTree(b);
		Set<String> secondTypes = getTypes(bTree);
		
		List<Action> actions = astDiff.getActions(aTree, bTree);
		for (Action action : actions) {
			JdtTree node = (JdtTree) action.getNode();			
			ASTNode astNode = node.getContainedNode();
			if (action instanceof Update)
				continue;
			Set<String> existingTypes;
			if (action instanceof Addition)
				 existingTypes = firstTypes;
			else
				existingTypes = secondTypes;
			if (astNode instanceof FieldDeclaration) {
				Type type = ((FieldDeclaration)astNode).getType();
				addTypeIfNotContained(existingTypes, types, type);
			} else if (astNode instanceof ClassInstanceCreation) {
				Type type = ((ClassInstanceCreation)astNode).getType();
				addTypeIfNotContained(existingTypes, types, type);
			}
		}
		return "" + types.size();
	}

	public void addTypeIfNotContained(Set<String> existingTypes, Set<String> types, Type type) {
		if (type.isSimpleType())
			if (!existingTypes.contains(getTypeName((SimpleType) type)))
				addType(types, type);
	}

	private Set<String> getTypes(JdtTree tree) {
		Set<String> types = new HashSet<String>();
		ASTNode containedNode = tree.getContainedNode();
		if (containedNode instanceof Type) {
			Type type = (Type) containedNode;
			addType(types, type);
		} else {
			List<Tree> children = tree.getChildren();
			for (Tree child : children) {
				types.addAll(getTypes((JdtTree) child));
			}
		}
		return types;
	}

	private void addType(Set<String> types, Type type) {
		if (type.isSimpleType())
			types.add(getTypeName((SimpleType) type));
	}
	
	private String getTypeName(SimpleType type) {
		Name name = type.getName();
		if (name.isQualifiedName())
			name = ((QualifiedName) name).getName();
		return name.getFullyQualifiedName();
	}
}
