package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VisualizationDataGeneratorTest extends MergeGitTest {
	
	private static final String projectName = "testProject";
	private VisualizationDataGenerator generator;
	private Path tempDirectory;
	private String tempFolderAbsolutePath;

	@Before
	public void before() throws Exception {
		super.before();
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
		generator.generateData(projectName, new ArrayList<CommitStatus>(), tempFolderAbsolutePath);
		
		Path projectPath = tempDirectory.resolve(projectName);
		assertIsFolder(projectPath);
	}

	private void assertIsFolder(Path projectPath) {
		File projectFolder = projectPath.toFile();
		assertTrue(projectFolder.exists());
		assertTrue(projectFolder.isDirectory());
	}
	
	@Test
	public void testCreateCommitFolder() {
		CommitStatus commitStatus = new CommitStatus(null, "abcde", new HashMap<String, CombinedFile>());
		generator.generateData(projectName, asList(commitStatus), tempFolderAbsolutePath);
		Path commitPath = tempDirectory.resolve(projectName).resolve("abcde");
		assertIsFolder(commitPath);
	}
	
	@Test
	public void testCreateFileFolderWithSimplePath() throws Exception {
		RevCommit conflictingCommit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(conflictingCommit);
		generator.generateData(projectName, asList(commitStatus), tempFolderAbsolutePath);
		
		Path filePath = resolveFileFolder(conflictingCommit, getConflictingFileName(commitStatus));
		assertIsFolder(filePath);
	}

	private Path resolveFileFolder(RevCommit conflictingCommit, String fileName) {
		Path filePath = tempDirectory.resolve(projectName).resolve(conflictingCommit.getName()).resolve(fileName);
		return filePath;
	}
	
	@Test
	public void testCreateFileFolderWithComplicatedPath() throws Exception {
		add("some/package/here/A.java", "bla");
		branch("branch");
		add("some/package/here/A.java", "bla1");
		checkout("master");
		add("some/package/here/A.java", "bla2");
		MergeResult result = merge("branch");
		RevCommit resolved = resolveMergeConflict(result, "some/package/here/A.java");
		
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(resolved);
		generator.generateData(projectName, asList(commitStatus), tempFolderAbsolutePath);
		
		Path some = resolveFileFolder(resolved, "some");
		assertIsFolder(some);
		
		Path packg = some.resolve("package");
		assertIsFolder(packg);
		
		Path here = packg.resolve("here");
		assertIsFolder(here);
		
		Path file = here.resolve("A.java");
		assertIsFolder(file);
	}
	
	@Test
	public void testCreateLOCDiffForFile() throws Exception {
		Path locFolder = generateLOCPath();
		assertIsFolder(locFolder);
		
		Path locAFile = locFolder.resolve("A");
		assertTrue(locAFile.toFile().exists());
		assertFalse(locAFile.toFile().isDirectory());
		Path locBFile = locFolder.resolve("B");
		assertTrue(locBFile.toFile().exists());
		assertFalse(locBFile.toFile().isDirectory());
	}
	
	@Test
	public void testLOCDiffContents() throws Exception {
		Path locFolder = generateLOCPath();
		
		assertFileContains(locFolder, "public class conflictingA3{}\n", "A");
		assertFileContains(locFolder, "public class A2{}\n", "B");
	}
	
	@Test
	public void testIndexGeneration() throws Exception {
		Path locFolder = generateLOCPath();
		
		assertFileContains(locFolder, new LOCIndexHtml().getIndex(), "index.html");
	}

	private void assertFileContains(Path locFolder, String fileContents, String fileName) throws IOException {
		Path aPath = locFolder.resolve(fileName);
		byte[] bytes = Files.readAllBytes(aPath);
		String aContents = new String(bytes);
		assertEquals(fileContents, aContents);
	}

	private Path generateLOCPath() throws Exception {
		RevCommit commit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(commit);
		new VisualizationDataGenerator().generateData(projectName, asList(commitStatus), tempFolderAbsolutePath);
		Path fileFolder = resolveFileFolder(commit, getConflictingFileName(commitStatus));
		
		Path locFolder = fileFolder.resolve("loc");
		return locFolder;
	}

	private String getConflictingFileName(CommitStatus commitStatus) {
		return commitStatus.getListOfConflictingFiles().get(0);
	}

	private List<CommitStatus> asList(CommitStatus commitStatus) {
		return Arrays.asList(new CommitStatus[]{commitStatus});
	}

}
