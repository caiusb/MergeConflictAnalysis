package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.simple.JSONAware;

public class ResultCollector implements JSONAware {
	
	private Map<String, Status> results = new HashMap<String, Status>();

	public void collectNonConflict(RevCommit mergeCommit) {
		results.put(mergeCommit.getName(), new Status().setConflict(false));
	}

	public Map<String, Status> getResults() {
		return results;
	}

	public void collectConflict(RevCommit mergeCommit, MergeResult mergeResult) {
		List<String> conflictingFiles = new ArrayList<String>();
		conflictingFiles.addAll(mergeResult.getConflicts().keySet());
		results.put(mergeCommit.getName(), new Status().setConflict(true).setFiles(conflictingFiles));
	}

	public String toJSONString() {
		String resultsString = "{";
		Set<String> keys = results.keySet();
		ArrayList<String> keysList = new ArrayList<String>();
		keysList.addAll(keys);		
		keysList.sort((String s1, String s2) -> s1.compareTo(s2));
		
		for (String key : keys) {
			Status value = results.get(key);
			resultsString = resultsString + "\"" + key + "\":" + value.toJSONString() + ",\n";
		}
		resultsString = resultsString.substring(0, resultsString.length() - 2);
		resultsString = resultsString + "}";
		return resultsString;
	}
	
}
