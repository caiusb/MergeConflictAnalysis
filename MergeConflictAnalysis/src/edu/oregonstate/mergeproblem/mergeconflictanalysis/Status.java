package edu.oregonstate.mergeproblem.mergeconflictanalysis;

public class Status {

	private boolean isConflicting;

	public Status setConflict(boolean isConflicting) {
		this.isConflicting = isConflicting;
		return this;
	}

	public boolean isConflicting() {
		return isConflicting;
	}
	
}
