package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.gitective.core.BlobUtils;
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
	
	public static Map<String, String> getFilesChangedByCommit(Repository repository, String sha1) {
		Map<String, String> changedFiles = new HashMap<String, String>();
		try {
			ObjectId head = repository.resolve("HEAD^{tree}");
			ObjectId oldHead = repository.resolve("HEAD~1^{tree}");
			
			ObjectReader reader = repository.newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, oldHead);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, head);
			
			Git git = new Git(repository);
			List<DiffEntry> diffEntries = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
			git.close();
			
			for (DiffEntry entry : diffEntries) {
				String path = entry.getNewPath();
				changedFiles.put(path, retrieveFile(repository, sha1, path));
			}
		} catch (Exception e) {}
		
		return changedFiles;
	} 
}
