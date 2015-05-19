package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.ASTFileProcessor;

public class ASTFileProcessorTest extends MergeGitTest {
	
	private ASTFileProcessor astFileProcessor;
	
	@Before
	public void before() throws Exception {
		super.before();
		astFileProcessor = new ASTFileProcessor();
	}

	@Test
	public void testHeader() {
		String header = astFileProcessor.getHeader();
		assertEquals("AST_A_TO_B, AST_A_TO_SOLVED, AST_B_TO_SOLVED", header);
	}
	
	@Test
	public void testGetData() throws Exception {
		RevCommit conflictingCommit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(conflictingCommit);
		String data = astFileProcessor.getDataForMerge(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		assertEquals("1,3,3", data);
	}
}
