package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VisualizationDataGeneratorTest {
	
	private VisualizationDataGenerator generator;
	private Path tempDirectory;
	private String tempFolderAbsolutePath;

	@Before
	public void before() throws Exception {
		generator = new VisualizationDataGenerator();
		tempDirectory = Files.createTempDirectory("");
		tempFolderAbsolutePath = tempDirectory.toFile().getAbsoluteFile().getAbsolutePath();
	}
	
	@After
	public void after() {
		tempDirectory.toFile().delete();
	}
	
	@Test
	public void testCreateProjectFolder() {
		generator.generateData("testProject", new ArrayList<CommitStatus>(), tempFolderAbsolutePath);
		
		File projectFolder = tempDirectory.resolve("testProject").toFile();
		assertTrue(projectFolder.exists());
		assertTrue(projectFolder.isDirectory());
	}
	
	@Test
	public void testCreateCommitFolder() {
		CommitStatus commitStatus = new CommitStatus(null, "abcde", new HashMap<String, CombinedFile>());
		generator.generateData("testProject", Arrays.asList(new CommitStatus[]{commitStatus}), tempFolderAbsolutePath);
		File commitFolder = tempDirectory.resolve("testProject").resolve("abcde").toFile();
		assertTrue(commitFolder.exists());
		assertTrue(commitFolder.isDirectory());
	}

}
