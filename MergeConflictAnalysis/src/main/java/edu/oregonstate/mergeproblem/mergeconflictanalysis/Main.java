package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.FileAnalysis;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

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
		
		BufferedOutputStream outputStream = new BufferedOutputStream(System.out);
		if (config.outputFile != null) {
			File file = new File(config.outputFile);
			if (!file.exists())
				file.createNewFile();
			outputStream = new BufferedOutputStream(new FileOutputStream(file));
		}

		new FileAnalysis().doAnalysis(config, outputStream);
	}
}	
