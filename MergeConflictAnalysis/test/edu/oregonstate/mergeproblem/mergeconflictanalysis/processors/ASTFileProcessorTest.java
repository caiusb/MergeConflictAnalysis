package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;

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
		String data = astFileProcessor.getDataForMerge(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		assertEquals("1,3,3", data);
	}
}
