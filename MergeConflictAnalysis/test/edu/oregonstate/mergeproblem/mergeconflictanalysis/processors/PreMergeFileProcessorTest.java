package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class PreMergeFileProcessorTest extends ProcessorTest {

	private PreMergeFileProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new PreMergeFileProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("A_BEFORE,B_BEFORE", header);
	}
	
	@Test
	public void testCollectMerge() throws Exception {
		CommitStatus commitStatus = generateCommitStatus();
		assertEquals("public class A2{},public class conflictingA3{}", processor.getDataForMerge(commitStatus, commitStatus.getListOfConflictingFiles().get(0)));
	}
}
