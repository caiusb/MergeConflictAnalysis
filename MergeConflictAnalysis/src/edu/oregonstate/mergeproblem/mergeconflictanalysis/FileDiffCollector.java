package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.BlobUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import fr.labri.gumtree.actions.ActionGenerator;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.gen.jdt.JdtTreeGenerator;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;

public class FileDiffCollector implements Collector {
	
	private class PairOfFiles implements JSONAware {
		
		private String filename;
		private String AContent;
		private String BContent;
		private int astDiff;
		
		public PairOfFiles(String filename, String AContent, String BContent, int astDiff) {
			this.filename = filename;
			this.AContent = AContent;
			this.BContent = BContent;
			this.astDiff = astDiff;
		}

		@Override
		public String toJSONString() {
			return "{\"filename\": \""+ JSONObject.escape(filename) + "\", " + 
					"\"A\": \"" + JSONObject.escape(AContent) + "\", " +
					"\"B\": \"" + JSONObject.escape(BContent) + "\", " +
					"\"ASTDiff: \"" + astDiff + "\"}";
		}
	}
	
	private List<PairOfFiles> conflictingFiles = new ArrayList<PairOfFiles>();
	

	public void collect(Repository repository, RevCommit commit, MergeResult conflictingMergeResult) {
		Map<String, int[][]> conflicts = conflictingMergeResult.getConflicts();
		ObjectId[] mergedCommits = conflictingMergeResult.getMergedCommits();
		ObjectId base = conflictingMergeResult.getBase();
		for (String file : conflicts.keySet()) {
			String AContent = BlobUtils.getContent(repository, mergedCommits[0], file);
			String BContent = BlobUtils.getContent(repository, mergedCommits[1], file);
			String baseContent = BlobUtils.getContent(repository, base, file);
			
			List<Action> AB_Actions = new ArrayList<Action>();
			List<Action> baseA_Actions = new ArrayList<>();
			List<Action> baseB_Actions = new ArrayList<>();
			try {
				AB_Actions = getActions(AContent, BContent);
				baseA_Actions = getActions(baseContent, AContent);
				baseB_Actions = getActions(baseContent, BContent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			conflictingFiles.add(new PairOfFiles(file, AContent, BContent, AB_Actions.size()));
			conflictingFiles.add(new PairOfFiles(file, baseContent, AContent, baseA_Actions.size()));
			conflictingFiles.add(new PairOfFiles(file, baseContent, BContent, baseB_Actions.size()));
		}
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
		return JSONArray.toJSONString(conflictingFiles);
	}

	@Override
	public void logException(Repository repository, RevCommit commit, Exception e) {
	}		

}
