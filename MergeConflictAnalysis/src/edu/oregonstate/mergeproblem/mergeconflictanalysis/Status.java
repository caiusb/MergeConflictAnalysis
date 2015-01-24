package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.List;

public class Status {

	private boolean isConflicting;
	private List<String> files;

	public Status setConflict(boolean isConflicting) {
		this.isConflicting = isConflicting;
		return this;
	}

	public boolean isConflicting() {
		return isConflicting;
	}

	public Status setFiles(List<String> files) {
		this.files = files;
		return this;
	}

	public List<String> getFiles() {
		return files;
	}
	
}
