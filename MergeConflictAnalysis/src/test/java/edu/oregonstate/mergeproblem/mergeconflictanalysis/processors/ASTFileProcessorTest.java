package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import org.junit.Before;
import org.junit.Test;

public class ASTFileProcessorTest extends ProcessorTest {
	
	private ASTFileProcessor astFileProcessor;
	
	@Before
	public void before() throws Exception {
		super.before();
		astFileProcessor = new ASTFileProcessor();
	}

	@Test
	public void testHeader() {
		String header = astFileProcessor.getHeader();
		assertEquals("AST_A_TO_B,AST_A_TO_SOLVED,AST_B_TO_SOLVED", header);
	}
	
	@Test
	public void testGetData() throws Exception {
		CommitStatus commitStatus = generateCommitStatus();
		String data = astFileProcessor.getData(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		assertEquals("1,1,1", data);
	}
}
