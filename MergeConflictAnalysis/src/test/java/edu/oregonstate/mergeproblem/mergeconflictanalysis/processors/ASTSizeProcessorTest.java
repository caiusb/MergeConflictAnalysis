package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import org.junit.Before;
import org.junit.Test;

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
		assertEquals("COMBINED_AST_SIZE_A,COMBINED_AST_SIZE_B,AST_SIZE_SOLVED", header);
	}
	
	@Test
	public void testData() throws Exception {
		CommitStatus status = generateCommitStatus();
		String data = processor.getData(status, status.getListOfConflictingFiles().get(0));
		assertEquals("4,4,1", data);
	}

}
