package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class NewMain {

	public static void main(String[] args) throws Exception {
		for (String repositoryPath : args) {
			Repository repository = Git.open(new File(repositoryPath)).getRepository();
			List<RevCommit> mergeCommits = new RepositoryWalker(repository).getMergeCommits();
			InMemoryMerger merger = new InMemoryMerger(repository);

			long start = System.nanoTime();
			List<CommitStatus> statuses = mergeCommits.stream().parallel()
					.map((commit) -> merger.recreateMerge(commit))
					.collect(Collectors.toList());
			long finish = System.nanoTime();
			
			String results = statuses.stream().parallel()
					.map((status) -> status.toJSONString())
					.collect(Collectors.joining(",\n"));
			
			System.out.println("[" + results + "]");
			
			System.out.println("The processing took " + (finish - start)/1000000 + " miliseconds");
		}
	}
}
