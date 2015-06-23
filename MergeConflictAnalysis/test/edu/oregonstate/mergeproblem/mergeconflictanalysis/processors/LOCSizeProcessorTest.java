package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class LOCSizeProcessorTest extends ProcessorTest {
	
	private LOCSizeProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new LOCSizeProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("LOC_SIZE_A,LOC_SIZE_B,LOC_SIZE_SOLVED",header);
	}
	
	@Test
	public void testData() throws Exception {
		CommitStatus commitStatus = generateCommitStatus();
		String data = processor.getData(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		assertEquals("1,1,1", data);
	}
}
