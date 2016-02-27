package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.ASTFileProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.ASTSizeProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.AuthorProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.BasicDataProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.CompositeProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.MetricProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.IsConflictProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.LOCFileProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.LOCSizeProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.MergeBaseProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.MergedInMasterProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.ModifiedProgramElementsProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.OperationCounterProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.PreMergeASTDiffProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.PreMergeASTSizeProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.PreMergeLOCDiffProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.PreMergeLOCFileSizeProcessor;

public class Main {
	
	public static final String LOG_NAME = "edu.oregonstate.mergeproblem";

	@Option(name="-viz-folder", usage="Where the files for the visualization will be generated")
	private String vizFolder = null;
	
	@Option(name="-url-folder", usage="Folder where the viz will be stored in the url")
	private String urlFolder = "";
	
	@Option(name="-output", usage="The file where to output the results")
	private String outputFile = null;
	
	@Option(name="-log-to-console", usage="If I should log to the console, using a fine level")
	private boolean logToConsole = false;
	
	@Argument
	private List<String> repositories = new ArrayList<String>();
	
	private static Logger logger = Logger.getLogger(LOG_NAME);

	private CompositeProcessor processor;
	
	public static void main(String[] args) throws Exception {
		new Main().doMain(args);
	}

	private void doMain(String[] args) throws IOException, WalkException {
		Logger gumtreeLogger = Logger.getLogger("fr.labri.gumtree");
		gumtreeLogger.setLevel(Level.OFF);
		
		CmdLineParser cmdLineParser = new CmdLineParser(this);
		try {
			cmdLineParser.parseArgument(args);
		} catch (CmdLineException e) {
		}
		
		if (logToConsole) {
			StreamHandler consoleHandler = new StreamHandler(System.out, new SimpleFormatter());
			consoleHandler.setLevel(Level.FINE);
			logger.addHandler(consoleHandler);
		} else {
			logger.setLevel(Level.SEVERE);
		}
		
		BufferedOutputStream outputStream = new BufferedOutputStream(System.out);
		if (outputFile != null) {
			File file = new File(outputFile);
			if (!file.exists())
				file.createNewFile();
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
		}
		
		initializeProcessor();
		for (String repositoryPath : repositories) {
			String projectName = Paths.get(repositoryPath).getFileName().toString();
			
			logger.info("Recreating the commits for " + repositoryPath);
			List<CommitStatus> statuses = recreateMergesInRepository(repositoryPath);
			logger.info("Processing results for " + repositoryPath);
			processResultsAndWriteToStream(statuses, outputStream);
			outputStream.flush();
			outputStream.close();
			if (vizFolder != null)
				generateDiffs(projectName, statuses);
		}
	}
	
	private void initializeProcessor() {
		processor = new CompositeProcessor();
		processor.addProcessor(new BasicDataProcessor());
		processor.addProcessor(new MergeBaseProcessor());
		processor.addProcessor(new LOCFileProcessor());
		processor.addProcessor(new ASTFileProcessor());
		processor.addProcessor(new LOCSizeProcessor());
		processor.addProcessor(new ASTSizeProcessor());
		processor.addProcessor(new IsConflictProcessor());
		processor.addProcessor(new ModifiedProgramElementsProcessor());
		processor.addProcessor(new OperationCounterProcessor());
		processor.addProcessor(new PreMergeLOCFileSizeProcessor());
		processor.addProcessor(new PreMergeLOCDiffProcessor());
		processor.addProcessor(new PreMergeASTSizeProcessor());
		processor.addProcessor(new PreMergeASTDiffProcessor());
		processor.addProcessor(new MetricProcessor());
		processor.addProcessor(new AuthorProcessor());
		processor.addProcessor(new MergedInMasterProcessor());
	}

	private void generateDiffs(String projectName, List<CommitStatus> statuses) {
		VisualizationDataGenerator dataGenerator = new VisualizationDataGenerator();
		dataGenerator.setURLFolder(urlFolder);
		statuses.stream().filter(this::containsJavaFiles)
			.forEach((status) -> dataGenerator.generateData(projectName, statuses, vizFolder));
	}
	
	private boolean containsJavaFiles(CommitStatus status) {
		return getFilesOfInterest(status).stream().anyMatch((file) -> file.endsWith("java"));
	}

	private List<String> getFilesOfInterest(CommitStatus status) {
		return status.getModifiedFiles();
	}
	
	private List<CommitStatus> recreateMergesInRepository(String repositoryPath) throws IOException,
			WalkException {
		Repository repository = Git.open(new File(repositoryPath)).getRepository();
		List<RevCommit> mergeCommits = new RepositoryWalker(repository).getMergeCommits();
		InMemoryMerger merger = new InMemoryMerger(repository);

		List<CommitStatus> statuses = mergeCommits.stream().parallel().map((commit) -> merger.recreateMerge(commit))
				.collect(Collectors.toList());
		return statuses;
	}

	private void processResultsAndWriteToStream(List<CommitStatus> statuses, OutputStream outputStream) throws IOException {
		outputStream.write((processor.getHeader() + "\n").getBytes());
		statuses.stream().parallel().map(this::processCommitStatus).forEach((r) -> {
			try {
				outputStream.write(r.getBytes());
			} catch (IOException e) {
			}
		});
	}

	public String processCommitStatus(CommitStatus status) {
		String statusResult = getFilesOfInterest(status).stream()
			.filter((file) -> file.endsWith("java"))
			.map((file) -> processor.getData(status, file))
			.collect(Collectors.joining("\n"));
		if (statusResult.equals(""))
			return statusResult;
		else
			return statusResult += "\n";
	}
}	
