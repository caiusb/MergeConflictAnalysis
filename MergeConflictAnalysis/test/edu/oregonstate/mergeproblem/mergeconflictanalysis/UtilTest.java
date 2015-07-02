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
}
