package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;

public class MergeBaseProcessorTest extends MergeGitTest {

	private MergeBaseProcessor processor;
	
	@Before
	public void before() throws Exception {
		super.before();
		processor = new MergeBaseProcessor();
	}

	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("BASE_SHA,BASE_TIME", header);
	}
	
	@Test
	public void testData() throws Exception {
		RevCommit mergeCommit = createConflictingCommit();
		RevCommit base = CommitUtils.getCommit(repository, "HEAD~2");
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		String data = processor.getDataForMerge(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		String expected = base.getName() + "," + base.getCommitTime();
		assertEquals(expected, data);
	}
}
	