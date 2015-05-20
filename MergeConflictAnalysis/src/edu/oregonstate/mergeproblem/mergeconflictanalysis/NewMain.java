package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.BasicDataProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.CompositeProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.LOCFileProcessor;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.LOCSizeProcessor;

public class NewMain {
	
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
	
	private static Logger logger = Logger.getLogger("edu.oregonstate.mergeproblem");

	private CompositeProcessor processor;
	
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
		
		if (logToConsole) {
			StreamHandler consoleHandler = new StreamHandler(System.out, new SimpleFormatter());
			consoleHandler.setLevel(Level.INFO);
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
			
			List<CommitStatus> statuses = recreateMergesInRepository(repositoryPath);
			String results = processResults(statuses);
			outputStream.write(results.getBytes());
			outputStream.flush();
			outputStream.close();
			if (vizFolder != null)
				generateDiffs(projectName, statuses);
		}
	}

	private void initializeProcessor() {
		processor = new CompositeProcessor();
		processor.addProcessor(new BasicDataProcessor());
		processor.addProcessor(new LOCFileProcessor());
		processor.addProcessor(new ASTFileProcessor());
		processor.addProcessor(new LOCSizeProcessor());
	}

	private void generateDiffs(String projectName, List<CommitStatus> statuses) {
		VisualizationDataGenerator dataGenerator = new VisualizationDataGenerator();
		dataGenerator.setURLFolder(urlFolder);
		statuses.forEach((status) -> dataGenerator.generateData(projectName, statuses, vizFolder));
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

	private String processResults(List<CommitStatus> statuses) {
		String result = processor.getHeader() + "\n";
		result += statuses.stream().parallel().map((status) ->{
			String statusResult = status.getListOfConflictingFiles().stream()
				.filter((file) -> file.endsWith("java"))
				.map((file) -> processor.getDataForMerge(status, file))
				.collect(Collectors.joining("\n"));
			if (statusResult.equals(""))
				return statusResult;
			else
				return statusResult += "\n";
		}).collect(Collectors.joining());
					
		return result;
	}
}	
