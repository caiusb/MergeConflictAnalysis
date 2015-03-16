package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

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
		String expected = "{\"" + commit.getName() + "\": " + createConflictingStatusWithOneFile(mergeResult) + "}";
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
		String expected = "{\"" + commit1.getName() + "\": " + createConflictingStatusWithOneFile(mergeResult1);
		expected += ",\n\"" + commit2.getName() + "\": " + createConflictingStatusWithOneFile(mergeResult2) + "}";
		assertEquals(expected, actual);
	}

	private String createConflictingStatusWithOneFile(MergeResult result) {
		MergeDiffInfo info = new MergeDiffInfo();
		info.diffFile("A.java", repository, result);
		String jsonString = new Status().setConflict(true).setFiles(Arrays.asList(new String[]{"A.java"})).setConflictDiffInfo("A.java", info).toJSONString();
		return jsonString;
	}
	
	@Test
	public void testFailingJSON() throws Exception {
		RevCommit commit = collectConflictingCommit();
		resultCollector.collectFailure(commit);
		String expected = "{\"" + commit.getName() + "\": "+ new Status().setFailure(true).toJSONString() +"}";
		assertEquals(expected, resultCollector.toJSONString());
	}
	
	@Test
	public void testEmptyResults() throws Exception {
		String actual = resultCollector.toJSONString();
		String expected = "{}";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCollectSubmodule() throws Exception {
		RevCommit commit = createConflictingCommit();
		resultCollector.collectSubmodule(commit);
		String expected = "{\"" + commit.getName() + "\": " + new Status().setStatus(Status.SUBMODULE).toJSONString() + "}";
		assertEquals(expected, resultCollector.toJSONString());
	}
}
