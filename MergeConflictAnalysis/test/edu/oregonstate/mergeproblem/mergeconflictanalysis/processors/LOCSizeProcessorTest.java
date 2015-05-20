package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class LOCSizeProcessorTest extends MergeGitTest {
	
	private LOCSizeProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new LOCSizeProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("LOC_SIZE_A,LOC_SIZE_B,LOC_SIZE_SOLVED",header);
	}
	
	@Test
	public void testData() throws Exception {
		RevCommit conflictingCommit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(conflictingCommit);
		String data = processor.getDataForMerge(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		assertEquals("1,1,1", data);
	}
}
