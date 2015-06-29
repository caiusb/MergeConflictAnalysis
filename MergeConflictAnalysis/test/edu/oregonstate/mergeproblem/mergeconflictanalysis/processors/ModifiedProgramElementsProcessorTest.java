package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class ModifiedProgramElementsProcessorTest extends ProcessorTest {
	
	private ModifiedProgramElementsProcessor processor;

	@Override
	@Before
	public void before() throws Exception {
		super.before();
		processor = new ModifiedProgramElementsProcessor();
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
