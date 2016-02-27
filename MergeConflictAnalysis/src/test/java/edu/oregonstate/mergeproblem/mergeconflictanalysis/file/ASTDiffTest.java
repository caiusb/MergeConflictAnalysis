package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

import fr.labri.gumtree.actions.model.Action;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ASTDiffTest {
	
	private ASTDiff astDiff;
	
	@Before
	public void before() {
		astDiff = new ASTDiff();
	}
	
	@Test
	public void testDiffEmptyFiles() throws Exception {
		List<Action> actions = astDiff.getActions("", "");
		assertEquals(0, actions.size());
	}

	@Test
	public void testDiffGarbage() throws IOException {
		List<Action> actions = astDiff.getActions("fdkaljfka", "fjkd");
		assertEquals(0, actions.size());
	}
	
	@Test
	public void testValidWithEmpty() throws Exception {
		List<Action> actions = astDiff.getActions("public class A{}", "");
		assertEquals(3, actions.size());
	}
	
	@Test
	public void testValid() throws Exception {
		List<Action> actions = astDiff.getActions("public class A {}", "public class B {}");
		assertEquals(1, actions.size());
	}

	@Test
	public void testABiggerExample() throws Exception {
		String a = "public class A{public void m(){} public void unchanged(){}}";
		String b = "public class A{public void n(){} public void unchanged(){}}";
		List<Action> actions = astDiff.getActions(a, b);
		assertEquals(1, actions.size());
	}
}
