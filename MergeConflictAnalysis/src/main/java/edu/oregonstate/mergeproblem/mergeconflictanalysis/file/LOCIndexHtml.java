package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

public class LOCIndexHtml {
	
	private String urlFolder = "";
	private String loc_index_contents = null;
	
	public LOCIndexHtml() {
	}
	
	public LOCIndexHtml(String urlFolder) {
		this.urlFolder = urlFolder;
	}
	
	public String getIndex() {
		if (loc_index_contents == null)
			loc_index_contents = "<!DOCTYPE html>\n" + 
					"<html>\n" + 
					"\n" + 
					"<head>\n" + 
					"	<meta charset=\"utf-8\" />\n" + 
					"	<title>Merge Visualizer</title>\n" + 
					"	<link rel=\"stylesheet\" href=\"/" + urlFolder + "/jsdifflib/diffview.css\"/>\n" + 
					"</head>\n" + 
					"\n" + 
					"<body>\n" +
					"   <div id=\"buttons\"></div>\n" + 
					"	<button onclick=\"AtoB()\">A - B</button>\n" + 
					"	<button onclick=\"AtoS()\">A - S</button>\n" + 
					"	<button onclick=\"BtoS()\">B - S</button>\n" + 
					"	<div id=\"diffoutput\"></div>\n" + 
					"\n" + 
					"	<script src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script>\n" + 
					"	<script src=\"/" + urlFolder + "/jsdifflib/difflib.js\"></script>\n" + 
					"	<script src=\"/" + urlFolder + "/jsdifflib/diffview.js\"></script>\n" + 
					"	<script src=\"/" + urlFolder + "/diffLOC.js\"></script>\n" + 
					"	<script>\n" + 
					"		diffLOC(a, b);\n" + 
					"	</script>\n" + 
					"\n" + 
					"</body>\n" + 
					"</html>";
		
		return loc_index_contents;
			
	}

}
