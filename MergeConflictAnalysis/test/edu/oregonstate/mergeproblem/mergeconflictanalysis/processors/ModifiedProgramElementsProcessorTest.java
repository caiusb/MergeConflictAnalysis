package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class ModifiedProgramElementsProcessorTest extends ProcessorTest {
	
	private static final String FILE_NAME = "A.java";
	private ModifiedProgramElementsProcessor processor;

	@Override
	@Before
	public void before() throws Exception {
		super.before();
		processor = new ModifiedProgramElementsProcessor();
	}
	
	private CommitStatus crateCommitStatus(String a, String b) {
		CombinedFile combinedFile = new CombinedFile();
		combinedFile.addChunk(ChunkOwner.A, a);
		combinedFile.addChunk(ChunkOwner.B, b);
		
		HashMap<String, CombinedFile> conflictingFiles = new HashMap<String, CombinedFile>();
		conflictingFiles.put(FILE_NAME, combinedFile);
		CommitStatus commitStatus = new CommitStatus(null, null, conflictingFiles, -1);
		return commitStatus;
	}
	
	private void assertResult(String a, String b, String expected) {
		CommitStatus commitStatus = crateCommitStatus(a, b);
		String data = processor.getData(commitStatus, FILE_NAME);
		assertEquals(expected,data);
	}
	
	@Test
	public void testOneMethodChange() {
		String a = "public class A{public void m(){}}";
		String b = "public class A{public void n(){}}";
		String expected = "1,1";
		assertResult(a, b, expected);
	}

	@Test
	public void testAMoreComplicatedOneMethodChange() {
		String a = "public class A{public void m(){int x = 1; int y = 2;}}";
		String b = "public class A{public void m(){int x = 10; int yx = 2;}}";
		String expected = "1,1";
		assertResult(a, b, expected);
	}
	
	@Test
	public void testATwoMethodChange() {
		String a = "public class A{public void m(){int x = 1;} public void int n(){ int y = 2;}}";
		String b = "public class A{public void m(){int x = 10;} public void int n(){ int yx = 2;}}";
		String expected = "2,1";
		assertResult(a, b, expected);
	}
}
