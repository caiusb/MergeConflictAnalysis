package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
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
	
	public static List<String> getFilesChangedByCommit(Repository repository, String sha1) {
		return new ArrayList<String>();
	}
}
