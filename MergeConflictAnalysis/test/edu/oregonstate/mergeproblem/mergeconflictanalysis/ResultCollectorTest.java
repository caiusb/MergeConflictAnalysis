package edu.oregonstate.mergeproblem.mergeconflictanalysis;

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
		String expected = "{\"" + commit.getName() + "\":{\"true\", [\"A\"]}}";
		assertEquals(expected,json);
	}
	
	@Test
	public void testTwoLineJSonString() throws Exception {
		RevCommit commit1 = collectConflictingCommit();
		RevCommit commit2 = collectConflictingCommit();
		String actual = resultCollector.toJSONString();
		String expected = "{\"" + commit1.getName() + "\":{\"true\", [\"A\"]},\n";
		expected += "\"" + commit2.getName() + "\":{\"true\", [\"A\"]}}";
		assertEquals(expected, actual);
	}
}
