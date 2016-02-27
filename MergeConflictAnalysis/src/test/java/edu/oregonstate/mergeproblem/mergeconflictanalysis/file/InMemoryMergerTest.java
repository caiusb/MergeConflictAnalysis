package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InMemoryMergerTest extends MergeGitTest {
	
	private InMemoryMerger merger;
	
	@Before
	public void beforeTest() {
		merger = new InMemoryMerger(repository);
	}
	
	@Test
	public void testConflictingMerge() throws Exception {
		RevCommit commit = createConflictingCommit();
		RevCommit[] parents = commit.getParents();
		assertEquals(2, parents.length);
		
		RevCommit first = parents[0];
		RevCommit second = parents[1];
		
		Map<String, CombinedFile> result = merger.merge(first, second);
		assertEquals(1, result.keySet().size());
		
		CombinedFile AFile = result.get("A.java");
		assertNotNull(AFile);
		
		String aVersion = AFile.getVersion(ChunkOwner.A);
		assertEquals("public class conflictingA3{}\n", aVersion);
		
		String bVersion = AFile.getVersion(ChunkOwner.B);
		assertEquals("public class A2{}\n", bVersion);
	}
	
	@Test
	public void testNonConflictingMerge() throws Exception {
		RevCommit merge = createNonConflictingMerge();
		RevCommit[] parents = merge.getParents();
		assertEquals(2, parents.length);
		
		Map<String, CombinedFile> result = merger.merge(parents[0], parents[1]);
		assertEquals(0, result.keySet().size());
	}
	
	@Test
	public void testModifiedFiles() throws Exception {
		createConflictingMergeResult();
		RevCommit mergeCommit = add(Arrays.asList(new String[]{"A.java", "second.java"}), Arrays.asList(new String[]{"Solved version", "Something else"}));
		CommitStatus status = merger.recreateMerge(mergeCommit);
		List<String> modifiedFiles = status.getModifiedFiles();
		assertEquals(2, modifiedFiles.size());
	}

}
