package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class PreMergeASTSizeProcessorTest extends MergeGitTest {
	
	@Test
	public void testHeader() {
		String header = new PreMergeASTSizeProcessor().getHeader();
		assertEquals("AST_A_SIZE,AST_B_SIZE", header);
	}

	@Test
	public void testDiff() throws Exception {
		RevCommit commit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(commit);
		String data = new PreMergeASTSizeProcessor().getData(commitStatus, "A.java");
		assertEquals("4,4",data);
	}
}
