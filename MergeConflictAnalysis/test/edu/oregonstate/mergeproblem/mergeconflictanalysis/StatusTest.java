package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class StatusTest {

	@Test
	public void testIsNotConflicting() {
		Status status = new Status().setConflict(false);
		assertFalse(status.isConflicting());
	}
	
	@Test
	public void testIsConflicting() {
		Status status = new Status().setConflict(true);
		assertTrue(status.isConflicting());
	}
	
	@Test
	public void testIsConflictingWithFiles() {
		Status status = createConflictingStatus();
		assertEquals(2, status.getFiles().size());
	}

	private Status createConflictingStatus() {
		Status status = new Status().setConflict(true).setFiles(Arrays.asList(new String[]{"FileA", "FileB"}));
		return status;
	}
	
	@Test
	public void testJSON() {
		Status status = createConflictingStatus();
		String jsonString = status.toJSONString();
		String expected = "{\"true\": [\"FileA\",\"FileB\"]}";
		assertEquals(expected,jsonString);
	}
	
	@Test
	public void testNonConflictJSON() {	
		Status status = new Status().setConflict(false);
		String jsonString = status.toJSONString();
		String expected = "{\"false\": []}";
		assertEquals(expected, jsonString);
	}
}
