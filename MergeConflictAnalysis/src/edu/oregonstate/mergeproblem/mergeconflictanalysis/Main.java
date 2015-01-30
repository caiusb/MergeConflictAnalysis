package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.nio.file.Paths;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class Main {

	public static void main(String[] args) throws Exception {
		for (String repositoryPath : args) {
			Git git = Git.open(Paths.get(repositoryPath).toFile());
			Repository repository = git.getRepository();
			RepositoryWalker repositoryWalker = new RepositoryWalker(repository);
			List<RevCommit> mergeCommits = repositoryWalker.getMergeCommits();
			
			ResultCollector resultCollector = new ResultCollector();
			
			for (RevCommit mergeCommit : mergeCommits) {
				ConflictDetector conflictDetector = new ConflictDetector();
				if (conflictDetector.isConflict(mergeCommit, git)) {
					MergeResult mergeResult = conflictDetector.getLastMergeResult();
					resultCollector.collectConflict(mergeCommit, mergeResult);
				} else
					resultCollector.collectNonConflict(mergeCommit);
			}
			System.out.println(resultCollector.toJSONString());
		}
	}

}
