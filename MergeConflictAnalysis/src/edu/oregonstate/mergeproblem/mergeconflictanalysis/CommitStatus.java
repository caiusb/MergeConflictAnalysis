package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.Map;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class CommitStatus implements JSONAware {
	
	private String sha1;
	private Map<String, CombinedFile> conflictingFiles;
	
	public CommitStatus(String sha1, Map<String, CombinedFile> conflictingFiles) {
		this.sha1 = sha1;
		this.conflictingFiles = conflictingFiles;
	}

	public String toJSONString() {
		String json = "{";
		json += "\"sha1\": \"" + sha1 + "\", ";
		json += "\"conflictingFiles:\": " + JSONObject.toJSONString(conflictingFiles);
		json += "}";
		return json;
	}
}
