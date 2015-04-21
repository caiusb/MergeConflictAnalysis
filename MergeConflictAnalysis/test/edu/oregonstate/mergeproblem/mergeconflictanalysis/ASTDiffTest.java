package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import fr.labri.gumtree.actions.model.Action;

public class ASTDiffTest {
	
	@Test
	public void testDiffEmptyFiles() throws Exception {
		ASTDiff astDiff = new ASTDiff();
		List<Action> actions = astDiff.getActions("", "");
		assertEquals(0, actions.size());
	}

	@Test
	public void testDiffGarbage() throws IOException {
		ASTDiff astDiff = new ASTDiff();
		List<Action> actions = astDiff.getActions("fdkaljfka", "fjkd");
		assertEquals(0, actions.size());
	}
	
	@Test
	public void testValidWithEmpty() throws Exception {
		ASTDiff astDiff = new ASTDiff();
		List<Action> actions = astDiff.getActions("public class A{}", "");
		assertEquals(3, actions.size());
	}
	
	@Test
	public void testValid() throws Exception {
		ASTDiff astDiff = new ASTDiff();
		List<Action> actions = astDiff.getActions("public class A {}", "public class B {}");
		assertEquals(6, actions.size());
	}
}
