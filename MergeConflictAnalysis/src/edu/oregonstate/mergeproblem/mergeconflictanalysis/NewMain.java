package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class NewMain {

	private static DiffAlgorithm diffAlgorithm = DiffAlgorithm.getAlgorithm(SupportedAlgorithm.MYERS);;

	public static void main(String[] args) throws Exception {
		for (String repositoryPath : args) {
			Repository repository = Git.open(new File(repositoryPath)).getRepository();
			List<RevCommit> mergeCommits = new RepositoryWalker(repository).getMergeCommits();
			InMemoryMerger merger = new InMemoryMerger(repository);

			long start = System.nanoTime();
			List<CommitStatus> statuses = mergeCommits.stream().parallel()
					.map((commit) -> merger.recreateMerge(commit))
					.collect(Collectors.toList());
			
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("SHA, FILE, A_TO_B, A_TO_SOLVED, B_TO_SOLVED\n");
			statuses.stream().parallel().forEach((status) ->{
				List<String> listOfConflictingFiles = status.getListOfConflictingFiles();
				for (String file : listOfConflictingFiles) {
					if (!file.endsWith("java"))
						continue;
					String solvedVersion = status.getSolvedVersion(file);
					CombinedFile combinedFile = status.getCombinedFile(file);
					String aVersion = combinedFile.getVersion(ChunkOwner.A);
					String bVersion = combinedFile.getVersion(ChunkOwner.B);
					int aToB = -1;
					int aToSolved = -1;
					int bToSolved = -1;
					
					if (aVersion != null && bVersion != null && solvedVersion != null) {
						aToB = getDiffSize(aVersion, bVersion);
						aToSolved = getDiffSize(aVersion, solvedVersion);
						bToSolved = getDiffSize(bVersion, solvedVersion);
					}
					stringBuilder.append(status.getSHA1() + "," + file + "," + aToB + "," + aToSolved + "," + bToSolved + "\n");
				}
			});
						
			long finish = System.nanoTime();
			
			System.out.println(stringBuilder.toString());
			
//			System.out.println("The processing took " + (finish - start)/1000000 + " miliseconds");
		}
	}

	private static int getDiffSize(String aVersion, String bVersion) {
		return diffAlgorithm.diff(RawTextComparator.DEFAULT, new RawText(aVersion.getBytes()), new RawText(bVersion.getBytes())).size();
	}
}
