package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.lib.Repository;
import org.gitective.core.BlobUtils;

public class FileRetriver {
	
	public static String retrieveFile(Repository repository, String sha1, String filename) {
		String content = BlobUtils.getContent(repository, sha1, filename);
		if (content == null)
			content = "";
		return content;
	}
}
