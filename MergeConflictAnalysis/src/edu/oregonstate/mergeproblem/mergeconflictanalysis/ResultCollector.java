package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;

public class ResultCollector {
	
	private Map<String, Status> results = new HashMap<String, Status>();

	public void collectNonConflict(RevCommit mergeCommit) {
		results.put(mergeCommit.getName(), new Status().setConflict(false));
	}

	public Map<String, Status> getResults() {
		return results;
	}

	public void collectConflict(RevCommit mergeCommit, MergeResult mergeResult) {
	}
	
}
