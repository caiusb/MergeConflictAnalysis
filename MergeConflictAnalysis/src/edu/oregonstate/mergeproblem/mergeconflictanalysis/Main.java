package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.nio.file.Paths;
import java.util.Iterator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.CommitUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		for (String repositoryPath : args) {
			ResultCollector resultCollector = new ResultCollector();
			Git repository = Git.open(Paths.get(repositoryPath).toFile());
			repository.checkout().setName("master").setForce(true).call();
			RevWalk revWalk = new RevWalk(repository.getRepository());
			revWalk.setRevFilter(new MergeFilter());
			RevCommit start = CommitUtils.getCommit(repository.getRepository(), Constants.HEAD);
			revWalk.markStart(start);
			Iterator<RevCommit> mergeCommits = revWalk.iterator();
			while (mergeCommits.hasNext()) {
				RevCommit mergeCommit = (RevCommit) mergeCommits.next();
				ConflictDetector conflictDetector = new ConflictDetector();
				if (conflictDetector.isConflict(mergeCommit, repository)) {
					MergeResult mergeResult = conflictDetector.getLastMergeResult();
					resultCollector.collectConflict(mergeCommit, mergeResult);
				} else
					resultCollector.collectNonConflict(mergeCommit);
			}
			System.out.println(resultCollector.toJSONString());
		}
	}

}
