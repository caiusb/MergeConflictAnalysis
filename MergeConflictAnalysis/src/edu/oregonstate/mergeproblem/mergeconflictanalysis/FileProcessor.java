package edu.oregonstate.mergeproblem.mergeconflictanalysis;

public interface FileProcessor {
	
	public String getHeader();
	
	public String getDataForMerge(CommitStatus status, String fileName);
}
