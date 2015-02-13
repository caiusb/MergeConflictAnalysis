package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

public class ResultCollectorTest extends MergeGitTest {
	
	private ResultCollector resultCollector;

	@Before
	public void before() throws Exception {
		super.before();
		resultCollector = new ResultCollector();
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
		MergeResult mergeResult = createConflictingMergeResult();
		RevCommit mergeCommit = resolveMergeConflict(mergeResult);
		resultCollector.collectConflict(mergeCommit, mergeResult);
		return mergeCommit;
	}
	
	@Test
	public void testJSONString() throws Exception {
		RevCommit commit = collectConflictingCommit();
		String json = resultCollector.toJSONString();
		String expected = "{\"" + commit.getName() + "\": " + createConflictingStatusWithOneFile() + "}";
		assertEquals(expected,json);
	}
	
	@Test
	public void testTwoLineJSonString() throws Exception {
		RevCommit commit1 = collectConflictingCommit();
		RevCommit commit2 = collectConflictingCommit();
		
		if (commit1.getName().compareTo(commit2.getName()) >= 0) {
			RevCommit temp = commit1;
			commit1 = commit2;
			commit2 = temp; 
		}
		
		String actual = resultCollector.toJSONString();
		String expected = "{\"" + commit1.getName() + "\": " + createConflictingStatusWithOneFile();
		expected += ",\n\"" + commit2.getName() + "\": " + createConflictingStatusWithOneFile() + "}";
		assertEquals(expected, actual);
	}

	private String createConflictingStatusWithOneFile() {
		return new Status().setConflict(true).setFiles(Arrays.asList(new String[]{"A"})).toJSONString();
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
}
