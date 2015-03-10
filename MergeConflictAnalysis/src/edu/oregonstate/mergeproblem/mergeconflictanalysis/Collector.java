package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.simple.JSONAware;

public interface Collector extends JSONAware {

	public abstract void collect(Repository repository, RevCommit mergeCommit, MergeResult mergeResult);
	
	public abstract void logException(Repository repository, RevCommit commit, Exception e);

	public abstract String toJSONString();

}