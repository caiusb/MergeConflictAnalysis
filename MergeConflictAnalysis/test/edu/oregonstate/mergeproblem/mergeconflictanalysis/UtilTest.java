package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class UtilTest extends MergeGitTest {

	@Test
	public void testAddFileChangeInMerge() throws Exception {
		createConflictingMergeResult();
		RevCommit mergeCommit = add(Arrays.asList(new String[]{"A.java", "second.java"}), Arrays.asList(new String[]{"Solved version", "Something else"}));
		Map<String, String> filesChangedByCommit = Util.getFilesChangedByCommit(repository, mergeCommit.getName());
		assertEquals(2, filesChangedByCommit.keySet().size());
	}
	
	@Test
	public void sanityTestFileChanged() throws Exception {
		add("A.java", "something");
		branch("branch");
		add("A.java", "something else");
		add("B.java", "bla");
		add("C.java", "bla bla");
		checkout("master");
		add("A.java", "conflicting");
		add("D.java", "something else");
		merge("branch");
		RevCommit merge = add("A.java", "conflict resolution");
		assertEquals(2, merge.getParentCount());
		Map<String, String> filesChanged = Util.getFilesChangedByCommit(repository, merge.getName());
		assertEquals(1, filesChanged.size());
	}
}
