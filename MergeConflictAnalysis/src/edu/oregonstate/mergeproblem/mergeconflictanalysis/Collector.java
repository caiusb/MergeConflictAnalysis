package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.simple.JSONAware;

public interface Collector extends JSONAware {

	public abstract void collect(RevCommit mergeCommit, MergeResult mergeResult);

	public abstract String toJSONString();

}