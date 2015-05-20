package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class ASTSizeProcessorTest extends MergeGitTest {
	
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
		RevCommit commit = createConflictingCommit();
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(commit);
		String data = processor.getDataForMerge(status, status.getListOfConflictingFiles().get(0));
		assertEquals("4,4,1", data);
	}

}
