package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.LOCFileProcessor;

public class LOCFileProcessorTest extends MergeGitTest {
	
	private LOCFileProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new LOCFileProcessor();
	}

	@Test
	public void testHeader() {
		processor.getHeader();
		assertEquals("LOC_A_TO_B, LOC_A_TO_SOLVED, LOC_B_TO_SOLVED", processor.getHeader());
	}
	
	@Test
	public void testOnFile() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		String data = processor.getDataForMerge(status, status.getListOfConflictingFiles().get(0));
		assertEquals("1,1,1", data);		
	}

}
