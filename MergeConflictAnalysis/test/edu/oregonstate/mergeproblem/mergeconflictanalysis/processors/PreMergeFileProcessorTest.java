package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class PreMergeFileProcessorTest extends MergeGitTest {

	private PreMergeFileProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new PreMergeFileProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("A_BEFORE,B_BEFORE", header);
	}
	
	@Test
	public void testCollectMerge() throws Exception {
		RevCommit conflictingCommit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(conflictingCommit);
		assertEquals("public class A2{},public class conflictingA3{}", processor.getDataForMerge(commitStatus, commitStatus.getListOfConflictingFiles().get(0)));
	}
}
