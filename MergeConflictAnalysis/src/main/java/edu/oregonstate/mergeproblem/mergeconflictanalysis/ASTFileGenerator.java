package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import fr.labri.gumtree.client.StringHtmlDiff;

public class ASTFileGenerator {
	
	private String urlFolder;
	
	public ASTFileGenerator() {
		this("");
	}

	public ASTFileGenerator(String urlFolder) {
		this.urlFolder = urlFolder;
	}
	
	public String generateIndex() {
		return "<head>\n" + 
				"	<meta charset=\"utf-8\" />\n" + 
				"	<title>Merge Visualizer</title>\n" + 
				"</head>\n" + 
				"\n" + 
				"<body>\n" + 
				"	<a href=\"atob.html\">A to B</a><br />\n" + 
				"	<a href=\"atos.html\">A to Solved</a><br />\n" + 
				"	<a href=\"btos.html\">B to Solved</a><br />\n" + 
				"</body>";
	}
	
	public String generateDiff(String aContents, String bContents, String aName, String bName) {
		try {
			return new StringHtmlDiff().getHtmlOfDiff(urlFolder, aContents, bContents, aName, bName);		
		} catch (Exception e) {
			return "There was an exception generating this file: \n " + e.getMessage();
		}
	}

}
