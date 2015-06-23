package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

public class ASTSizeProcessorTest extends ProcessorTest {
	
	private ASTSizeProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new ASTSizeProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("AST_SIZE_A,AST_SIZE_B,AST_SIZE_SOLVED", header);
	}
	
	@Test
	public void testData() throws Exception {
		CommitStatus status = generateCommitStatus();
		String data = processor.getDataForMerge(status, status.getListOfConflictingFiles().get(0));
		assertEquals("4,4,1", data);
	}

}
