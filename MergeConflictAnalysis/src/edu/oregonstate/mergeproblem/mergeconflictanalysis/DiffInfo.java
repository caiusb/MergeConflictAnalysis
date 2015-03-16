package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

class DiffInfo implements JSONAware {
	
	private String filename;
	private String AContent;
	private String BContent;
	private int astDiff;
	
	public DiffInfo(String filename, String AContent, String BContent, int astDiff) {
		this.filename = filename;
		this.AContent = AContent;
		this.BContent = BContent;
		this.astDiff = astDiff;
	}

	@Override
	public String toJSONString() {
		return "{\"filename\": \""+ JSONObject.escape(filename) + "\", " + 
				"\"A\": \"" + JSONObject.escape(AContent) + "\", " +
				"\"B\": \"" + JSONObject.escape(BContent) + "\", " +
				"\"ASTDiff\": \"" + astDiff + "\"}";
	}
}