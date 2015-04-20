package edu.oregonstate.mergeproblem.mergeconflictanalysis.old;

import org.eclipse.jgit.api.MergeResult;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.old.MergeDiffInfo;

public class MergeDiffInfoTest extends MergeGitTest{

	@Test
	public void collectConflictingFiles() throws Exception {
		MergeResult conflictingMergeResult = createConflictingMergeResult(0);
		MergeDiffInfo diffCollector = new MergeDiffInfo();
		diffCollector.diffFile("A.java", repository, conflictingMergeResult);
		String actual = diffCollector.toJSONString();
		String expected = "{\"AtoB\": {\"filename\": \"A.java\", \"A\": \"public class conflictingA3{}\", \"B\": \"public class A2{}\", \"ASTDiff\": \"1\"}, " + 
				"\"baseToA\": {\"filename\": \"A.java\", \"A\": \"public class A1{}\", \"B\": \"public class conflictingA3{}\", \"ASTDiff\": \"1\"}, " + 
				"\"baseToB\": {\"filename\": \"A.java\", \"A\": \"public class A1{}\", \"B\": \"public class A2{}\", \"ASTDiff\": \"1\"}}";
		assertEquals(expected, actual);
	}
}
