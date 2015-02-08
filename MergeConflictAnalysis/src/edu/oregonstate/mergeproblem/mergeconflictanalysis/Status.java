package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

public class Status implements JSONAware {

	private boolean isConflicting;
	private List<String> files;
	private boolean failure = false;

	public Status setConflict(boolean isConflicting) {
		this.isConflicting = isConflicting;
		this.files = new ArrayList<String>();
		return this;
	}

	public boolean isConflicting() {
		return isConflicting;
	}

	public Status setFailure(boolean failure) {
		this.failure  = failure;
		return this;
	}
	
	public Status setFiles(List<String> files) {
		this.files = files;
		return this;
	}

	public List<String> getFiles() {
		return files;
	}

	@Override
	public String toJSONString() {
		if (failure)
			return "{\"failure\": []}";
		
		return "{\"" + isConflicting + "\": " + JSONArray.toJSONString(files) + "}";
	}
}
