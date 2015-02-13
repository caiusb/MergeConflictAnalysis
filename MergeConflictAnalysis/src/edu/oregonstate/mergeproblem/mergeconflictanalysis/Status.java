package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

public class Status implements JSONAware {

	private String status = "";
	private List<String> files;
	
	public final static String CLEAN = "clean";
	public final static String CONFLICT = "conflict";
	public final static String FAILURE = "failure";
	public final static String SUBMODULE = "submodule";

	public Status setConflict(boolean isConflicting) {
		if (isConflicting)
			status = CONFLICT;
		else
			status = CLEAN;
		this.files = new ArrayList<String>();
		return this;
	}

	public boolean isConflicting() {
		return status.equals(CONFLICT);
	}

	public Status setFailure(boolean failure) {
		status = FAILURE;
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
		if (status.equals(FAILURE))
			return "{\"failure\": []}";
		
		boolean conflicting = isConflicting();
		return "{\"" + conflicting + "\": " + JSONArray.toJSONString(files) + "}";
	}
}
