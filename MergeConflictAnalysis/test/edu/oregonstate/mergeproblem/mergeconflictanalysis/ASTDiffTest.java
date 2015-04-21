package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import static org.junit.Assert.assertEquals;

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

}
