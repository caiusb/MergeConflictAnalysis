package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class NewMain {

	private static DiffAlgorithm diffAlgorithm = DiffAlgorithm.getAlgorithm(SupportedAlgorithm.MYERS);
	
	@Option(name="-viz-folder", usage="Where the files for the visualization will be generated")
	private String vizFolder = null;
	
	@Argument
	private List<String> repositories = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {
		
		new NewMain().doMain(args);
	}

	private void doMain(String[] args) throws IOException, WalkException {
		Logger gumtreeLogger = Logger.getLogger("fr.labri.gumtree");
		gumtreeLogger.setLevel(Level.OFF);
		
		CmdLineParser cmdLineParser = new CmdLineParser(this);
		try {
			cmdLineParser.parseArgument(args);
		} catch (CmdLineException e) {
		}
		
		for (String repositoryPath : repositories) {
			String projectName = Paths.get(repositoryPath).getFileName().toString();
			
			List<CommitStatus> statuses = recreateMergesInRepository(repositoryPath);
			processResults(statuses);
			if (vizFolder != null)
				generateDiffs(statuses);
		}
	}

	private void generateDiffs(List<CommitStatus> statuses) {
		System.out.println("Generating diffs!!!");
	}
	
	private List<CommitStatus> recreateMergesInRepository(String repositoryPath) throws IOException,
			WalkException {
		Repository repository = Git.open(new File(repositoryPath)).getRepository();
		List<RevCommit> mergeCommits = new RepositoryWalker(repository).getMergeCommits();
		InMemoryMerger merger = new InMemoryMerger(repository);

		long start = System.nanoTime();
		List<CommitStatus> statuses = mergeCommits.stream().parallel().map((commit) -> merger.recreateMerge(commit))
				.collect(Collectors.toList());
		return statuses;
	}

	private void processResults(List<CommitStatus> statuses) {
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
	}
	
	private String processFile(CommitStatus status, String fileName) {
		String solvedVersion = status.getSolvedVersion(fileName);
		CombinedFile combinedFile = status.getCombinedFile(fileName);
		String aVersion = combinedFile.getVersion(ChunkOwner.A);
		String bVersion = combinedFile.getVersion(ChunkOwner.B);
		String locDiff = getDiff(solvedVersion, aVersion, bVersion, (a, b) -> getLOCDiffSize(a, b));
		String astDiff = getDiff(solvedVersion, aVersion, bVersion, (a, b) -> getASTDIffSize(a, b));
		return status.getSHA1() + "," + fileName + "," + locDiff + "," + astDiff;
	}
	
	private String getDiff(String solvedVersion, String aVersion, String bVersion, BiFunction<String, String, Integer> diffFunction) {
		int aToB = -1;
		int aToSolved = -1;
		int bToSolved = -1;
		
		if (aVersion != null && bVersion != null) {
			aToB = diffFunction.apply(aVersion, bVersion);
			if (solvedVersion != null) {
				aToSolved = diffFunction.apply(aVersion, solvedVersion);
				bToSolved = diffFunction.apply(bVersion, solvedVersion);
			}
		}
		String locDiff = aToB + "," + aToSolved + "," + bToSolved;
		return locDiff;
	}

	private int getLOCDiffSize(String aVersion, String bVersion) {
		return diffAlgorithm.diff(RawTextComparator.DEFAULT, new RawText(aVersion.getBytes()), new RawText(bVersion.getBytes())).size();
	}
	
	private int getASTDIffSize(String aVersion, String bVersion) {
		try {
			return new ASTDiff().getActions(aVersion, bVersion).size();
		} catch (IOException e) {
			return -1;
		}
	}
}
