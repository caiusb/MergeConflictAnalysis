package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;


public class PreMergeLOCFileSizeProcessor extends AbstractPreMergeProcessor {

	@Override
	public String getHeader() {
		return "LOC_A_BEFORE_SIZE,LOC_B_BEFORE_SIZE";
	}

	@Override
	public String getResults(String a, String b) {
		int aLength = a.split("\r|\n|\r\n").length;
		int bLength = b.split("\r|\n|\r\n").length;
		
		return aLength + "," + bLength;
	}

}
