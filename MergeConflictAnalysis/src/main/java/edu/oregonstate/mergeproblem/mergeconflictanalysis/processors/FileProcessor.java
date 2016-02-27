package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;

public interface FileProcessor {
	
	public String getHeader();
	
	public String getData(CommitStatus status, String fileName);
}
