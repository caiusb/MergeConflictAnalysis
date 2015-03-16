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
		String expected = "[{\"filename\": \"A.java\", \"A\": \"public class conflictingA3{}\", \"B\": \"public class A2{}\", \"ASTDiff: \"1\"},\n" + 
				"{\"filename\": \"A.java\", \"A\": \"public class A1{}\", \"B\": \"public class conflictingA3{}\", \"ASTDiff: \"1\"},\n" + 
				"{\"filename\": \"A.java\", \"A\": \"public class A1{}\", \"B\": \"public class A2{}\", \"ASTDiff: \"1\"}]";
		assertEquals(expected, actual);
	}
}
