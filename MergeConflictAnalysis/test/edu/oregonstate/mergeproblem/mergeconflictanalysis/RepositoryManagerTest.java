package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RepositoryManagerTest extends MergeGitTest {
	
	private RepositoryManager repositoryManager;
	private Git tempRepo;
	
	@Before
	@Override
	public void before() throws Exception {
		super.before();
		repositoryManager = new RepositoryManager(repository.getWorkTree());
		tempRepo = repositoryManager.getTempRepo();
	}
	
	@After
	public void after() throws Exception {
		repositoryManager.clean();
	}

	@Test
	public void testCreateRepositoryManager() throws Exception {
		Iterable<RevCommit> commits = tempRepo.log().call();
		List<RevCommit> commitsList = convertIterableToList(commits);
		assertEquals(0, commitsList.size());
	}

	@Test
	public void testPullCommit() throws Exception {
		add("A.txt","one");
		RevCommit commitToPull = add("A.txt","two");
		add("A.txt","three");
		
		RepositoryManager repositoryManager = new RepositoryManager(repository.getWorkTree());
		Git tempRepo = repositoryManager.getTempRepo();
		
		repositoryManager.pull(commitToPull);
		
		Iterable<RevCommit> commits = tempRepo.log().call();
		List<RevCommit> commitsList = convertIterableToList(commits);
		
		assertEquals(2, commitsList.size());
	}

	private List<RevCommit> convertIterableToList(Iterable<RevCommit> commits) {
		Iterator<RevCommit> iterator = commits.iterator();
		ArrayList<RevCommit> commitsList = new ArrayList<RevCommit>();
		while (iterator.hasNext()) {
			RevCommit commit = (RevCommit) iterator.next();
			commitsList.add(commit);
		}
		return commitsList;
	}
}
