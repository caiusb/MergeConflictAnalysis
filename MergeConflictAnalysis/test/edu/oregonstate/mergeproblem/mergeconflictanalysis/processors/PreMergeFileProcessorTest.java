package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class PreMergeFileProcessorTest extends ProcessorTest {

	private PreMergeLOCFileSizeProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new PreMergeLOCFileSizeProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("LOC_A_BEFORE_SIZE,LOC_B_BEFORE_SIZE", header);
	}
	
	@Test
	public void testCollectMerge() throws Exception {
		CommitStatus commitStatus = generateCommitStatus();
		assertEquals("1,1", processor.getData(commitStatus, commitStatus.getListOfConflictingFiles().get(0)));
	}
}
