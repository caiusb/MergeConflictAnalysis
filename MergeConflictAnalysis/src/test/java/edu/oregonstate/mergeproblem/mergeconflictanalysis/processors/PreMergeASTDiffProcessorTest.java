package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class PreMergeASTDiffProcessorTest extends MergeGitTest{
	
	@Test
	public void testHeader() {
		String header = new PreMergeASTDiffProcessor().getHeader();
		assertEquals("AST_DIFF_BEFORE", header);
	}
	
	@Test
	public void testDiff() throws Exception {
		RevCommit commit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(commit);
		PreMergeASTDiffProcessor processor = new PreMergeASTDiffProcessor();
		String data = processor.getData(commitStatus, "A.java");
		assertEquals("1", data);
	}
}
