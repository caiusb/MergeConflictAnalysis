package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;

public class ResultCollector {
	
	private Map<String, Status> results = new HashMap<String, Status>();

	public void collectNonConflict(RevCommit mergeCommit) {
		
	}

	public Map<String, Status> getResults() {
		return results;
	}
	
}
