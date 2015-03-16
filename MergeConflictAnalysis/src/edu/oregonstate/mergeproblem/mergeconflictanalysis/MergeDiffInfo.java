package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.gitective.core.BlobUtils;

import fr.labri.gumtree.actions.ActionGenerator;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.gen.jdt.JdtTreeGenerator;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;

public class MergeDiffInfo {
	
	public static final String JSON_A_TO_B = "AtoB";
	public static final String JSON_BASE_TO_A = "baseToA";
	public static final String JSON_BASE_TO_B = "baseToB";
	
	private DiffInfo AtoB;
	private DiffInfo baseToA;
	private DiffInfo baseToB;

	public boolean diffFile(String file, Repository repository, MergeResult status) {
		ObjectId[] mergedCommits = status.getMergedCommits();
		ObjectId base = status.getBase();
		
		if (!file.endsWith(".java"))
			return false; 
		String AContent = BlobUtils.getContent(repository, mergedCommits[0], file);
		String BContent = BlobUtils.getContent(repository, mergedCommits[1], file);
		String baseContent = BlobUtils.getContent(repository, base, file);
		
		List<Action> AB_Actions = new ArrayList<Action>();
		List<Action> baseA_Actions = new ArrayList<Action>();
		List<Action> baseB_Actions = new ArrayList<Action>();
		try {
			AB_Actions = getActions(AContent, BContent);
			baseA_Actions = getActions(baseContent, AContent);
			baseB_Actions = getActions(baseContent, BContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AtoB = new DiffInfo(file, AContent, BContent, AB_Actions.size());
		baseToA = new DiffInfo(file, baseContent, AContent, baseA_Actions.size());
		baseToB = new DiffInfo(file, baseContent, BContent, baseB_Actions.size());
		
		return true;
	}

	private List<Action> getActions(String AContent, String BContent)
			throws IOException {
		JdtTreeGenerator jdtTreeGenerator = new JdtTreeGenerator();
		Tree leftTree = getTree(AContent, jdtTreeGenerator);
		Tree rightTree = getTree(BContent, jdtTreeGenerator);
		List<Action> actions = getActions(leftTree, rightTree);
		return actions;
	}

	private List<Action> getActions(Tree leftTree, Tree rightTree) {
		List<Action> actions;
		Matcher matcher = MatcherFactories.newMatcher(leftTree, rightTree);
		matcher.match();
		ActionGenerator actionGenerator = new ActionGenerator(leftTree, rightTree, matcher.getMappings());
		actionGenerator.generate();
		actions = actionGenerator.getActions();
		return actions;
	}

	private Tree getTree(String AContent, JdtTreeGenerator jdtTreeGenerator) throws IOException {
		Path leftFilePath = Files.createTempFile("", "java");
		Files.write(leftFilePath, AContent.getBytes(), StandardOpenOption.WRITE);
		File leftFile = leftFilePath.toFile();
		Tree leftTree = jdtTreeGenerator.fromFile(leftFile.getCanonicalPath());
		leftFile.delete();
		return leftTree;
	}

	public String toJSONString() {
		String json = "{";
		json += "\"" + JSON_A_TO_B +"\": " + AtoB.toJSONString() + ", ";
		json += "\"" + JSON_BASE_TO_A + "\": " + baseToA.toJSONString() + ", ";
		json += "\"" + JSON_BASE_TO_B + "\": " + baseToB.toJSONString();
		json += "}";
		return json;
	}
	
	@Override
	public String toString() {
		return toJSONString();
	}
}