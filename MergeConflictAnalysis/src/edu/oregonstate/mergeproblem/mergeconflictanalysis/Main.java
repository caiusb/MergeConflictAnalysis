package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.nio.file.Paths;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class Main {

	public static final String LOGGER_NAME = "MergingProblemLogger";

	public static void main(String[] args) throws Exception {

		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.setLevel(Level.ALL);

		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);

		for (String repositoryPath : args) {
			ResultCollector resultCollector = new ResultCollector();
			try {
				Git git = Git.open(Paths.get(repositoryPath).toFile());
				Repository repository = git.getRepository();
				RepositoryWalker repositoryWalker = new RepositoryWalker(repository);
				List<RevCommit> mergeCommits = repositoryWalker.getMergeCommits();


				for (RevCommit mergeCommit : mergeCommits) {
					ConflictDetector conflictDetector = new ConflictDetector();
					try {
						if (conflictDetector.isConflict(mergeCommit, git)) {
							MergeResult mergeResult = conflictDetector.getLastMergeResult();
							resultCollector.collectConflict(mergeCommit, mergeResult);
						} else
							resultCollector.collectNonConflict(mergeCommit);
					} catch (MergingException e) {
						resultCollector.collectFailure(mergeCommit);
					}
				}
			} catch (Throwable e) {
				logger.severe("The anaylsis threw this: " + e);
			} finally {
				System.out.println(resultCollector.toJSONString());
			}
		}
	}

}
