package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.build.BuildAnalysis;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.build.MergeOnlyBuildAnalysis;
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.FileAnalysis;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class Main {

	public static final String LOG_NAME = "edu.oregonstate.mergeproblem";

	public class Config {

		@Option(name = "-viz-folder", usage = "Where the files for the visualization will be generated")
		public String vizFolder = null;

		@Option(name = "-url-folder", usage = "Folder where the viz will be stored in the url")
		public String urlFolder = "";

		@Option(name = "-output", usage = "The file where to output the results")
		public String outputFile = null;

		@Option(name = "-log-to-console", usage = "If I should log to the console, using a fine level")
		public boolean logToConsole = false;

		@Option(name = "-build", usage = "If I should do a build analysis. Default is off, doing a file anaylsis")
		public boolean build = false;

		@Option(name = "-merge-only", usage = "If I should do a build analysis only for the merge commits. Ignored if build is off")
		public boolean mergeOnly = false;

		@Option(name = "-conflict-only", usage = "If I should only do the conflict anaysis, and nothing else")
		public boolean conflictOnly = false;

		@Argument
		public List<String> repositories = new ArrayList<String>();

	}

	public static final Logger logger = Logger.getLogger(LOG_NAME);

	public static void main(String[] args) throws Exception {
		new Main().doMain(args);
	}

	private void doMain(String[] args) throws Exception {
		Logger gumtreeLogger = Logger.getLogger("fr.labri.gumtree");
		gumtreeLogger.setLevel(Level.OFF);

		Config config = new Config();
		CmdLineParser cmdLineParser = new CmdLineParser(config);
		try {
			cmdLineParser.parseArgument(args);
		} catch (CmdLineException e) {
		}
		
		if (config.logToConsole) {
			StreamHandler consoleHandler = new StreamHandler(System.out, new SimpleFormatter());
			consoleHandler.setLevel(Level.FINE);
			logger.addHandler(consoleHandler);
		} else {
			logger.setLevel(Level.SEVERE);
		}

		logger.log(Level.INFO, "Up and loaded in the JVM");

		BufferedOutputStream outputStream = new BufferedOutputStream(System.out);
		if (config.outputFile != null) {
			try {
				File file = new File(config.outputFile);
				if (file.isDirectory()) {
					System.out.println(config.outputFile + " is a directory!");
					logger.log(Level.SEVERE, config.outputFile + " is a directory!");
					System.exit(1);
				}
				if (!file.exists()) {
					file.createNewFile();
					logger.log(Level.INFO, "Created the result file");
				}
				outputStream = new BufferedOutputStream(new FileOutputStream(file));
			} catch (IOException e) {
				System.out.println("Error creating results file");
				logger.log(Level.SEVERE, "Error creating results file");
				System.out.println(e);
				System.exit(2);
			}
		}

		if (config.build)
			if (!config.mergeOnly)
				BuildAnalysis.doAnalysis(config, outputStream);
			else
				MergeOnlyBuildAnalysis.doAnalysis(config, outputStream);
		else if (config.conflictOnly)
			ConflictAnalysis.doAnalysis(config, outputStream);
		else
			new FileAnalysis().doAnalysis(config, outputStream);

		outputStream.flush();
		outputStream.close();
	}
}	
