package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class IsConflictProcessorTest extends ProcessorTest {
	
	private IsConflictProcessor processor;

	@Before
	public void before() throws Exception {
		super.before();
		processor = new IsConflictProcessor();
	}
	
	@Test
	public void testHeader() {
		assertEquals("IS_CONFLICT", processor.getHeader());
	}

	@Test
	public void testData() throws Exception {
		CommitStatus status = generateCommitStatus();
		String actual = processor.getDataForMerge(status, status.getListOfConflictingFiles().get(0));
		assertEquals("true", actual);
	}

}
