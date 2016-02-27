package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import static org.junit.Assert.assertEquals;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ChunkOwner;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CombinedFile;
import org.junit.Before;
import org.junit.Test;

public class CombinedFileTest {
	
	private CombinedFile combinedFile;
	
	private static String aExpected = "Both\nJust A\nBoth again\n";
	private static String bExpected = "Both\nJust B\nBoth again\n";
	private static String bothExpected = "Both\nBoth again\n";

	@Before
	public void before() {
		combinedFile = new CombinedFile();
		combinedFile.addChunk(ChunkOwner.BOTH, "Both\n");
		combinedFile.addChunk(ChunkOwner.A, "Just A\n");
		combinedFile.addChunk(ChunkOwner.B, "Just B\n");
		combinedFile.addChunk(ChunkOwner.BOTH, "Both again\n");
	}
	
	@Test
	public void testGetA() {
		String actual = combinedFile.getVersion(ChunkOwner.A);
		assertEquals(aExpected, actual);
	}

	@Test
	public void testGetB() {
		String actual = combinedFile.getVersion(ChunkOwner.B);
		assertEquals(bExpected, actual);
	}
	
	@Test
	public void testGetBoth() {
		String actual = combinedFile.getVersion(ChunkOwner.BOTH);
		assertEquals(bothExpected, actual);
	}
}
