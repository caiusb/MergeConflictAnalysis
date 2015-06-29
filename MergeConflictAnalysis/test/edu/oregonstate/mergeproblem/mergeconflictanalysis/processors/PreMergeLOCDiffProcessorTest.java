package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class PreMergeLOCDiffProcessorTest extends MergeGitTest {
	
	@Test
	public void testHeader() {
		String header = new PreMergeLOCDiffProcessor().getHeader();
		assertEquals("LOC_DIFF_BEFORE", header);
	}
	
	@Test
	public void testDiff() throws Exception {
		RevCommit commit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(commit);
		PreMergeLOCDiffProcessor processor = new PreMergeLOCDiffProcessor();
		String data = processor.getData(commitStatus, "A.java");
		assertEquals("1",data);
	}
}
