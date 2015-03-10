package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.MergeResult;
import org.junit.Test;

public class FileDiffCollectorTest extends MergeGitTest{

	@Test
	public void collectConflictingFiles() throws Exception {
		MergeResult conflictingMergeResult = createConflictingMergeResult(0);
		FileDiffCollector diffCollector = new FileDiffCollector();
		diffCollector.collect(repository, null, conflictingMergeResult);
		String actual = diffCollector.toJSONString();
		String expected = "[{\"filename\": \"A\", \"A\": \"conflicting 3\", \"B\": \"2\"}]";
		assertEquals(expected, actual);
	}
}
