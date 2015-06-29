package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;

public class PreMergeLOCDiffProcessor extends AbstractPreMergeProcessor {

	@Override
	public String getHeader() {
		return "LOC_DIFF_BEFORE";
	}

	@Override
	protected String getResults(String a, String b) {
		EditList diff = DiffAlgorithm.getAlgorithm(SupportedAlgorithm.MYERS).diff(
				RawTextComparator.DEFAULT, new RawText(a.getBytes()),
				new RawText(b.getBytes()));
		return "" + diff.size();
	}

}
