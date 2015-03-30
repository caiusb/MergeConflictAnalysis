package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;

public class Status implements JSONAware {

	private String status = "";
	private String message = "";
	private List<String> files = new ArrayList<String>();
	private List<MergeDiffInfo> mergeInfo = new ArrayList<MergeDiffInfo>();
	
	public static final String JSON_FILES = "files";
	public static final String JSON_STATUS = "status";
	public static final String JSON_MESSAGE = "message";
	public static final String JSON_DIFFS= "diffs";
	
	public final static String CLEAN = "clean";
	public final static String CONFLICT = "conflict";
	public final static String FAILURE = "failure";
	public final static String SUBMODULE = "submodule";
	
	public Status setStatus(String status) {
		this.status = status;
		return this;
	}

	public Status setConflict(boolean isConflicting) {
		if (isConflicting)
			status = CONFLICT;
		else
			status = CLEAN;
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
	
	public Status setConflictDiffInfo(MergeDiffInfo info) {
		mergeInfo.add(info);
		return this;
	}

	public List<String> getFiles() {
		return files;
	}

	@Override
	public String toJSONString() {
		return "{\"" + JSON_STATUS + "\": \"" + status + "\", \"" + 
				JSON_MESSAGE + "\": \"" + message + "\", \"" + 
				JSON_FILES + "\": " + JSONArray.toJSONString(files) + ", \"" +
				JSON_DIFFS + "\": " + JSONArray.toJSONString(mergeInfo) + 
				"}";
	}

	public Status setMessage(String message) {
		this.message = message;
		return this;
	}
}
