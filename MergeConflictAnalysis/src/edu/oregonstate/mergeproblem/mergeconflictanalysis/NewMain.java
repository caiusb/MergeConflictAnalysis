package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class NewMain {

	private static DiffAlgorithm diffAlgorithm = DiffAlgorithm.getAlgorithm(SupportedAlgorithm.MYERS);
	
	public static void main(String[] args) throws Exception {
		
		Logger gumtreeLogger = Logger.getLogger("fr.labri.gumtree");
		gumtreeLogger.setLevel(Level.OFF);
		
		for (String repositoryPath : args) {
			Repository repository = Git.open(new File(repositoryPath)).getRepository();
			List<RevCommit> mergeCommits = new RepositoryWalker(repository).getMergeCommits();
			InMemoryMerger merger = new InMemoryMerger(repository);

			long start = System.nanoTime();
			List<CommitStatus> statuses = mergeCommits.stream().parallel()
					.map((commit) -> merger.recreateMerge(commit))
					.collect(Collectors.toList());
			
			String result = "SHA, FILE, LOC_A_TO_B, LOC_A_TO_SOLVED, LOC_B_TO_SOLVED, AST_A_TO_B, AST_A_TO_SOLVED, AST_B_TO_SOLVED\n";
			result += statuses.stream().parallel().map((status) ->{
				String statusResult = status.getListOfConflictingFiles().stream()
					.filter((file) -> file.endsWith("java"))
					.map((file) -> processFile(status, file))
					.collect(Collectors.joining("\n"));
				if (statusResult.equals(""))
					return statusResult;
				else
					return statusResult += "\n";
			}).collect(Collectors.joining());
						
			long finish = System.nanoTime();
			
			System.out.println(result);
			
//			System.out.println("The processing took " + (finish - start)/1000000 + " miliseconds");
		}
	}
	
	private static String processFile(CommitStatus status, String fileName) {
		String solvedVersion = status.getSolvedVersion(fileName);
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String aVersion = combinedFile.getVersion(ChunkOwner.A);
		String bVersion = combinedFile.getVersion(ChunkOwner.B);
		String locDiff = getDiff(solvedVersion, aVersion, bVersion, NewMain::getLOCDiffSize);
		String astDiff = getDiff(solvedVersion, aVersion, bVersion, NewMain::getASTDIffSize);
		return status.getSHA1() + "," + fileName + "," + locDiff + "," + astDiff;
	}
	
	private static String getDiff(String solvedVersion, String aVersion, String bVersion, BiFunction<String, String, Integer> diffFunction) {
		int aToB = -1;
		int aToSolved = -1;
		int bToSolved = -1;
		
		if (aVersion != null && bVersion != null && solvedVersion != null) {
			aToB = diffFunction.apply(aVersion, bVersion);
			aToSolved = diffFunction.apply(aVersion, solvedVersion);
			bToSolved = diffFunction.apply(bVersion, solvedVersion);
		}
		String locDiff = aToB + "," + aToSolved + "," + bToSolved;
		return locDiff;
	}

	private static int getLOCDiffSize(String aVersion, String bVersion) {
		return diffAlgorithm.diff(RawTextComparator.DEFAULT, new RawText(aVersion.getBytes()), new RawText(bVersion.getBytes())).size();
	}
	
	private static int getASTDIffSize(String aVersion, String bVersion) {
		try {
			return new ASTDiff().getActions(aVersion, bVersion).size();
		} catch (IOException e) {
			return -1;
		}
	}
}
