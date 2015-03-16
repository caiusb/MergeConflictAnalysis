package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.LinkedList;
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
		logger.setLevel(Level.INFO);

		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.INFO);
		logger.addHandler(handler);
		
		Logger gumTreeLogger = Logger.getLogger("fr.labri.gumtree.matchers");
		gumTreeLogger.setLevel(Level.OFF);
		
		List<Collector> collectors = new LinkedList<Collector>();
		collectors.add(new ResultCollector());

		for (String repositoryPath : args) {
			try {
				Git git = Git.open(Paths.get(repositoryPath).toFile());
				Repository repository = git.getRepository();
				RepositoryWalker repositoryWalker = new RepositoryWalker(repository);
				List<RevCommit> mergeCommits = repositoryWalker.getMergeCommits();

				for (RevCommit mergeCommit : mergeCommits) {
					MergeRecreator conflictDetector = new MergeRecreator();
					try {
						conflictDetector.recreateMerge(mergeCommit, git);
						MergeResult mergeResult = conflictDetector.getLastMergeResult();
						for (Collector collector : collectors)
							collector.collect(repository, mergeCommit, mergeResult);
					} catch (MergingException e) {
						for (Collector collector : collectors) {
							collector.logException(repository, mergeCommit, e);
						}
					} catch (SubmoduleDetectedException e) {
						//resultCollector.collectSubmodule(mergeCommit);
					}
				}
			} catch (Throwable e) {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				e.printStackTrace(new PrintStream(byteArrayOutputStream));
				logger.severe("The anaylsis threw this: " + e + "\n" + byteArrayOutputStream.toString());
			} finally {
				for (Collector collector : collectors) {
					System.out.println(collector.toJSONString());
				}
			}
		}
	}

}
