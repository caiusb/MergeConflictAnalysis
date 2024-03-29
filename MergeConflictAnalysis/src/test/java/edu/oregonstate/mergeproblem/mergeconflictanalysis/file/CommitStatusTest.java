package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

import java.util.Arrays;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CombinedFile;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class CommitStatusTest extends MergeGitTest {
	
	@Test
	public void testGetCombinedFileOfNonConflictingFile() throws Exception {
		createConflictingMergeResult();
		RevCommit merge = add(Arrays.asList(new String[]{"A.java", "B.java"}), Arrays.asList(new String[]{"resolved", "something"}));
		InMemoryMerger merger = new InMemoryMerger(repository);
		CommitStatus commitStatus = merger.recreateMerge(merge);
		CombinedFile combinedFile = commitStatus.getCombinedFile("B.java");
		assertNotNull(combinedFile);
	}

	@Test
	public void getTimeZone() throws Exception {
		RevCommit merge = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(merge);
		int offset = commitStatus.getTimeZoneOffset();
		assertEquals(author.getTimeZoneOffset() * 60, offset);
	}
}
