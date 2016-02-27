package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;
import org.junit.Before;
import org.junit.Test;

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
		String data = processor.getData(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		String expected = base.getName() + "," + base.getCommitTime();
		assertEquals(expected, data);
	}
}
	