package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.BlobUtils;
import org.gitective.core.CommitUtils;
import org.gitective.core.GitException;

public class Util {
	
	public static String retrieveFile(Repository repository, String sha1, String filename) {
		String content = null;
		try {
			content = BlobUtils.getContent(repository, sha1, filename);
		} catch (GitException e) {
			return "";
		}
		if (content == null)
			content = "";
		return content;
	}
	
	public static List<String> getFilesChangedByCommit(Repository repository, String sha1) {
		List<String> modifiedFiles = new ArrayList<>();
		RevCommit mergeCommit = CommitUtils.getCommit(repository, sha1);
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.reset();
		treeWalk.setRecursive(true);
		RevTree tree = mergeCommit.getTree();
		try {
			treeWalk.addTree(tree);
			while (treeWalk.next()) {
				String path = new String(treeWalk.getRawPath());
				modifiedFiles.add(path);
			}
		} catch (IOException e) {
			Logger.getLogger(NewMain.LOG_NAME).severe(e.getMessage());
		}
		treeWalk.close();
		return modifiedFiles;
	}
}
