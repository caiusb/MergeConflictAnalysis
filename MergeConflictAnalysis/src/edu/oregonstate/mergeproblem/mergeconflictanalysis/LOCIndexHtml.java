package edu.oregonstate.mergeproblem.mergeconflictanalysis;

public class LOCIndexHtml {
	
	public static final String LOC_INDEX = "<!DOCTYPE html>\n" + 
			"<html>\n" + 
			"\n" + 
			"<head>\n" + 
			"	<meta charset=\"utf-8\" />\n" + 
			"	<title>Merge Visualizer</title>\n" + 
			"	<link rel=\"stylesheet\" href=\"/jsdifflib/diffview.css\"/>\n" + 
			"</head>\n" + 
			"\n" + 
			"<body>\n" + 
			"	<div id=\"diffoutput\"></div>\n" + 
			"\n" + 
			"	<script src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script>\n" + 
			"	<script src=\"/jsdifflib/difflib.js\"></script>\n" + 
			"	<script src=\"/jsdifflib/diffview.js\"></script>\n" + 
			"	<script src=\"/diffLOC.js\"></script>\n" + 
			"	<script>\n" + 
			"		diffLOC('./A', './B');\n" + 
			"	</script>\n" + 
			"\n" + 
			"</body>\n" + 
			"</html>";

}
