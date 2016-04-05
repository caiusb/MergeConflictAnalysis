package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class PreMergeLOCDiffProcessorTest extends MergeGitTest {
	
	@Test
	public void testHeader() {
		String header = new PreMergeLOCDiffProcessor().getHeader();
		assertEquals("LOC_DIFF", header);
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
