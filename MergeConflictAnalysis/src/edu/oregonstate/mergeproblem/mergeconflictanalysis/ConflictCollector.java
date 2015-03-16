package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class ConflictCollector implements Collector {
	
	private Map<String, Status> results = new HashMap<String, Status>();

	/* (non-Javadoc)
	 * @see edu.oregonstate.mergeproblem.mergeconflictanalysis.Collector#collectConflict(org.eclipse.jgit.revwalk.RevCommit, org.eclipse.jgit.api.MergeResult)
	 */
	@Override
	public void collect(Repository repository, RevCommit mergeCommit, MergeResult mergeResult) {
		MergeStatus mergeStatus = mergeResult.getMergeStatus();
		if(mergeStatus.equals(MergeStatus.CONFLICTING)) {
			collectConflict(repository, mergeCommit, mergeResult);
		} else if (mergeStatus.equals(MergeStatus.MERGED)) {
			collectNonConflict(mergeCommit);
		}
	}
	
	public Map<String, Status> getResults() {
		return results;
	}

	public void collectNonConflict(RevCommit mergeCommit) {
		results.put(mergeCommit.getName(), new Status().setConflict(false));
	}
	

	private void collectConflict(Repository repository, RevCommit mergeCommit, MergeResult mergeResult) {
		List<String> conflictingFiles = new ArrayList<String>();
		Status status = new Status().setConflict(true).setFiles(conflictingFiles);
		conflictingFiles.addAll(mergeResult.getConflicts().keySet());
		for (String file : conflictingFiles) {
			MergeDiffInfo mergeDiffInfo = new MergeDiffInfo();
			if (mergeDiffInfo.diffFile(file, repository, mergeResult))
				status.setConflictDiffInfo(mergeDiffInfo);
		}
		results.put(mergeCommit.getName(), status);
	}
	
	public void logException(Repository repository, RevCommit commit, Exception e) {
		results.put(commit.getName(), new Status().setFailure(true).setMessage(e.getMessage()));
	}

	public void collectFailure(RevCommit mergeCommit) {
		results.put(mergeCommit.getName(), new Status().setFailure(true));
	}
	
	/* (non-Javadoc)
	 * @see edu.oregonstate.mergeproblem.mergeconflictanalysis.Collector#toJSONString()
	 */
	@Override
	public String toJSONString() {
		String resultsString = "{";
		ArrayList<String> keysList = getSortedCommits();
		
		for (String key : keysList) {
			Status value = results.get(key);
			resultsString = resultsString + "\"" + key + "\": " + value.toJSONString() + ",\n";
		}
		resultsString = trimTheLastTwoCharacters(resultsString);
		resultsString = resultsString + "}";
		return resultsString;
	}

	private ArrayList<String> getSortedCommits() {
		Set<String> keys = results.keySet();
		ArrayList<String> keysList = new ArrayList<String>();
		keysList.addAll(keys);		
		keysList.sort((String s1, String s2) -> s1.compareTo(s2));
		return keysList;
	}

	private String trimTheLastTwoCharacters(String resultsString) {
		int endIndex = resultsString.length() - 2;
		if (endIndex > 0) {
			resultsString = resultsString.substring(0, endIndex);
		}
		return resultsString;
	}

	public void collectSubmodule(RevCommit commit) {
		results.put(commit.getName(), new Status().setStatus(Status.SUBMODULE));
	}

}