package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import org.junit.Before;
import org.junit.Test;

public class LOCFileProcessorTest extends ProcessorTest {
	
	private LOCFileProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new LOCFileProcessor();
	}

	@Test
	public void testHeader() {
		processor.getHeader();
		assertEquals("LOC_A_TO_B,LOC_A_TO_SOLVED,LOC_B_TO_SOLVED", processor.getHeader());
	}
	
	@Test
	public void testOnFile() throws Exception {
		CommitStatus status = generateCommitStatus();
		String data = processor.getData(status, status.getListOfConflictingFiles().get(0));
		assertEquals("1,1,1", data);		
	}

}
