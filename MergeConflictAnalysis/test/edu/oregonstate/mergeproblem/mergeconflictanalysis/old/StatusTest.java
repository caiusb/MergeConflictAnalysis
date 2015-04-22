package edu.oregonstate.mergeproblem.mergeconflictanalysis.old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.old.Status;

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
		Status status = new Status().setConflict(true).setFiles(Arrays.asList(new String[] { "FileA", "FileB" }));
		return status;
	}

	@Test
	public void testJSON() {
		Status status = createConflictingStatus();
		String jsonString = status.toJSONString();
		String expected = "{\"" + Status.JSON_STATUS + "\": \"" + Status.CONFLICT + "\", \"message\": \"\", \"" + Status.JSON_FILES
				+ "\": [\"FileA\",\"FileB\"], \"diffs\": []}";
		assertEquals(expected, jsonString);
	}

	@Test
	public void testNonConflictJSON() {
		Status status = new Status().setConflict(false);
		String jsonString = status.toJSONString();
		String expected = "{\"" + Status.JSON_STATUS + "\": \"" + Status.CLEAN + "\", \"message\": \"\", \"" + Status.JSON_FILES
				+ "\": [], \"diffs\": []}";
		assertEquals(expected, jsonString);
	}

	@Test
	public void testFailureJSON() {
		Status status = new Status().setFailure(true);
		String jsonString = status.toJSONString();
		String expected = "{\"" + Status.JSON_STATUS + "\": \"" + Status.FAILURE + "\", \"message\": \"\", \"" + Status.JSON_FILES
				+ "\": [], \"diffs\": []}";
		assertEquals(expected, jsonString);
	}
}