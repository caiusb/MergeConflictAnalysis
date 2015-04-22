package edu.oregonstate.mergeproblem.mergeconflictanalysis.old;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.old.ConflictCollector;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.old.MergeDiffInfo;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.old.Status;

public class ResultCollectorTest extends MergeGitTest {
	
	private ConflictCollector resultCollector;

	@Before
	public void before() throws Exception {
		super.before();
		resultCollector = new ConflictCollector();
	}

	@Test
	public void testCollectNonConflict() throws Exception {
		RevCommit mergeCommit = createNonConflictingMerge();
		resultCollector.collectNonConflict(mergeCommit);
		Map<String, Status> results = resultCollector.getResults();
		assertEquals(1, results.keySet().size());
		assertTrue(results.keySet().contains(mergeCommit.getName()));
		assertFalse(results.get(mergeCommit.getName()).isConflicting());
	}

	@Test
	public void testCollectConflict() throws Exception {
		RevCommit mergeCommit = collectConflictingCommit();
		Map<String, Status> results = resultCollector.getResults();
		assertEquals(1, results.keySet().size());
		assertTrue(results.keySet().contains(mergeCommit.getName()));
		
		Status status = results.get(mergeCommit.getName());
		assertTrue(status.isConflicting());
		assertEquals(1,status.getFiles().size());
	}

	protected RevCommit collectConflictingCommit() throws Exception {
		return collectConflictingCommit(0);
	}

	protected RevCommit collectConflictingCommit(int base) throws Exception {
		MergeResult mergeResult = createConflictingMergeResult(base);
		RevCommit mergeCommit = resolveMergeConflict(mergeResult);
		resultCollector.collect(this.repository, mergeCommit, mergeResult);
		return mergeCommit;
	}
	
	@Test
	public void testJSONString() throws Exception {
		MergeResult mergeResult = createConflictingMergeResult();
		RevCommit commit = resolveMergeConflict(mergeResult);
		resultCollector.collect(repository, commit, mergeResult);
		String json = resultCollector.toJSONString();
		String expected = "[{\"sha1\": \"" + commit.getName() + "\", \"status\": " + createConflictingStatusWithOneFile(mergeResult) + "}]";
		assertEquals(expected,json);
	}
	
	@Test
	public void testTwoLineJSonString() throws Exception {
		MergeResult mergeResult1 = createConflictingMergeResult(0);
		RevCommit commit1 = resolveMergeConflict(mergeResult1);
		MergeResult mergeResult2 = createConflictingMergeResult(2);
		RevCommit commit2 = resolveMergeConflict(mergeResult2);
		
		if (commit1.getName().compareTo(commit2.getName()) >= 0) {
			RevCommit temp = commit1;
			commit1 = commit2;
			commit2 = temp; 
		}

		resultCollector.collect(repository, commit1, mergeResult1);
		resultCollector.collect(repository, commit2, mergeResult2);
		
		String actual = resultCollector.toJSONString();
		String expected = "[{\"sha1\": \"" + commit1.getName() + "\", \"status\": " + createConflictingStatusWithOneFile(mergeResult1);
		expected += "},\n{\"sha1\": \"" + commit2.getName() + "\", \"status\": " + createConflictingStatusWithOneFile(mergeResult2) + "}]";
		assertEquals(expected, actual);
	}

	private String createConflictingStatusWithOneFile(MergeResult result) {
		MergeDiffInfo info = new MergeDiffInfo();
		info.diffFile("A.java", repository, result);
		String jsonString = new Status().setConflict(true).setFiles(Arrays.asList(new String[]{"A.java"})).setConflictDiffInfo(info).toJSONString();
		return jsonString;
	}
	
	@Test
	public void testFailingJSON() throws Exception {
		RevCommit commit = collectConflictingCommit();
		resultCollector.collectFailure(commit);
		String expected = "[{\"sha1\": \"" + commit.getName() + "\", \"status\": "+ new Status().setFailure(true).toJSONString() +"}]";
		assertEquals(expected, resultCollector.toJSONString());
	}
	
	@Test
	public void testEmptyResults() throws Exception {
		String actual = resultCollector.toJSONString();
		String expected = "[]";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCollectSubmodule() throws Exception {
		RevCommit commit = createConflictingCommit();
		resultCollector.collectSubmodule(commit);
		String expected = "[{\"sha1\": \"" + commit.getName() + "\", \"status\": " + new Status().setStatus(Status.SUBMODULE).toJSONString() + "}]";
		assertEquals(expected, resultCollector.toJSONString());
	}
	
	@Test
	public void testCollectConflictInNonJavaFile() throws Exception {
		add("bla.xml", "xxxx");
		branch("branch");
		add("bla.xml", "ppppp");
		checkout("master");
		add("bla.xml", "aaaaaa");
		MergeResult mergeResult = merge("branch");
		assertTrue(mergeResult.getMergeStatus().equals(MergeStatus.CONFLICTING));
		
		RevCommit commit = resolveMergeConflict(mergeResult, "bla.xml");
		resultCollector.collect(repository, commit, mergeResult);
		String actual = resultCollector.toJSONString();
		String expected = "[{\"sha1\": \"" + commit.getName() + "\", \"status\": " + new Status().setStatus(Status.CONFLICT).setFiles(Arrays.asList("bla.xml")).toJSONString() + "}]";
		assertEquals(expected, actual);
	}
}