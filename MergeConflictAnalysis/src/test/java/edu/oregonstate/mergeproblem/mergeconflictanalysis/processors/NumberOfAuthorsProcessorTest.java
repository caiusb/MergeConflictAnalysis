package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import java.util.Date;
import java.util.TimeZone;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

public class NumberOfAuthorsProcessorTest extends ProcessorTest {
	
	private AuthorProcessor processor;
	private PersonIdent aAuthor;
	private PersonIdent bAuthor;
	
	@Before
	@Override
	public void before() throws Exception {
		super.before();
		processor = new AuthorProcessor();
		aAuthor = new PersonIdent("a", "a@test.com", new Date(), TimeZone.getTimeZone("GMT-8:00"));
		bAuthor = new PersonIdent("b", "b@test.com", new Date(), TimeZone.getTimeZone("GMT-8:00"));
	}

	@Test
	public void testOneAuthor() throws Exception {
		CommitStatus commitStatus = generateCommitStatus();
		String data = processor.getData(commitStatus, commitStatus.getListOfConflictingFiles().get(0));
		assertEquals("1", data);
	}
	
	@Test
	public void testTwoAuthors() throws Exception {
		RevCommit mergeCommit = createMultiAuthorMergeCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		String data = processor.getData(commitStatus, "A.java");
		assertEquals("2", data);
	}

	private RevCommit createMultiAuthorMergeCommit()
			throws Exception, GitAPIException, NoHeadException, NoMessageException, UnmergedPathsException,
			ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException {
		author = aAuthor;
		add("A.java", "public class A{}");
		branch("branch");
		add("B.java", "public class B{}");
		checkout("master");
		author = bAuthor;
		add("A.java", "public class A{public void m(){}}");
		author = aAuthor;
		MergeResult mergeResult = merge("branch");
		assertEquals(MergeStatus.MERGED, mergeResult.getMergeStatus());
		RevCommit mergeCommit = Git.wrap(repository).commit().setAmend(true).setAuthor(author).setMessage("merged").call();
		return mergeCommit;
	}
	
	@Test
	public void testTwoAuthorsInASeaOfManyAuthors() throws Exception {
		author = new PersonIdent("another", "another@test.com");
		add("C.java", "public class C{}");
		RevCommit mergeCommit = createMultiAuthorMergeCommit();
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		String data = processor.getData(status, "A.java");
		assertEquals("2", data);
	}
	
	@Test
	public void testHeader() {
		String header = processor.getHeader();
		assertEquals("NO_AUTHORS", header);
	}
	
	@Test
	public void testPartOfMerge() throws Exception {
		PersonIdent another = new PersonIdent("another", "another@test.com", new Date(), TimeZone.getTimeZone("GMT-8:00"));
		author = another;
		add("C.java", "public class C{}");
		branch("branch2");
		checkout("master");
		RevCommit mergeCommit = createMultiAuthorMergeCommit();
		author = another;
		checkout("branch2");
		add("D.java", "public class D{}");
		checkout("master");
		merge("branch2");
		CommitStatus status = new InMemoryMerger(repository).recreateMerge(mergeCommit);
		String data = processor.getData(status, "A.java");
		assertEquals("2", data);
	}
}
