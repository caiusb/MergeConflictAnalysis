package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.gitective.core.BlobUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class FileDiffCollector implements JSONAware {
	
	private class PairOfFiles implements JSONAware {
		
		private String filename;
		private String AContent;
		private String B;
		private String BContent;
		
		public PairOfFiles(String filename, String AContent, String BContent) {
			this.filename = filename;
			this.AContent = AContent;
			this.BContent = BContent;
		}

		@Override
		public String toJSONString() {
			return "{\"filename\": \""+ JSONObject.escape(filename) + "\", " + 
					"\"A\": \"" + JSONObject.escape(AContent) + "\", " +
					"\"B\": \"" + JSONObject.escape(BContent) + "\"}";
		}
	}
	
	private List<PairOfFiles> conflictingFiles = new ArrayList<PairOfFiles>();

	public void collectConflictInfo(Repository repository, MergeResult conflictingMergeResult) {
		Map<String, int[][]> conflicts = conflictingMergeResult.getConflicts();
		ObjectId[] mergedCommits = conflictingMergeResult.getMergedCommits();
		for (String file : conflicts.keySet()) {
			String AContent = BlobUtils.getContent(repository, mergedCommits[0], file);
			String BContent = BlobUtils.getContent(repository, mergedCommits[1], file);
			conflictingFiles.add(new PairOfFiles(file, AContent, BContent));
		}
	}

	public String toJSONString() {
		return JSONArray.toJSONString(conflictingFiles);
	}		

}
