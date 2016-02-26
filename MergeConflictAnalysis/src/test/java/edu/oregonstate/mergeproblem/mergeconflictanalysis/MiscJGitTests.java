package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.tests.GitTestCase;
import org.junit.Test;

public class MiscJGitTests extends GitTestCase {

	@Test
	public void testLogForNonExistingFiles() throws Exception {
		add("A.file", "bla");
		Git git = Git.open(testRepo);
		Iterable<RevCommit> revs = git.log().addPath("nofile.txt").call();
		assertFalse(revs.iterator().hasNext());
	}
}
