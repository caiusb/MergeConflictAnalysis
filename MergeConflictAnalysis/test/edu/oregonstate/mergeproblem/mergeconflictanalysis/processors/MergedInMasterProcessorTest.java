package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.InMemoryMerger;

public class MergedInMasterProcessorTest extends ProcessorTest {
	
	private MergedInMasterProcessor processor;
	
	@Before
	@Override
	public void before() throws Exception {
		super.before();
		processor = new MergedInMasterProcessor();
	}

	@Test
	public void testHeader() {
		assertEquals("MERGED_IN_MASTER", processor.getHeader());
	}
	
	private CommitStatus createCommitStatusWithMessage(String message) throws Exception {
		MergeResult mergeResult = createConflictingMergeResult();
		RevCommit commit = resolveMergeConflict(mergeResult, "A.java", message);
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(commit);
		return commitStatus;
	}
	
	@Test
	public void testFalse() throws Exception {
		RevCommit commit = createConflictingCommit();
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(commit);
		String data = processor.getData(status, "a");
		assertEquals("False", data);
	}
	
	@Test
	public void testTruePulLRequest() throws Exception {
		CommitStatus commitStatus = createCommitStatusWithMessage("Merge pull request #74 from bob");
		String data = processor.getData(commitStatus, "a");
		assertEquals("True", data);
	}
	
	@Test
	public void testTruePullRequest2() throws Exception {
		CommitStatus commitStatus = createCommitStatusWithMessage("Merge pull request #309 from tylanbin/master\n\nfix the bug of the method convertJsonToListeners\n");
		String data = processor.getData(commitStatus, "a");
		assertEquals("True", data);
	}

	@Test
	public void testTrueManualMerge() throws Exception {
		CommitStatus status = createCommitStatusWithMessage("Merge branch 'dev' of github://somebody@repo.git into master");
		String data = processor.getData(status, "A.java");
		assertEquals("True", data);
	}
	
	@Test
	public void testFalseMergeMasterIntoSomething() throws Exception {
		CommitStatus status = createCommitStatusWithMessage("Merge branch 'master' of github://somebody@repo.git into dev123");
		String data = processor.getData(status, "A.java");
		assertEquals("False", data);
	}

}
