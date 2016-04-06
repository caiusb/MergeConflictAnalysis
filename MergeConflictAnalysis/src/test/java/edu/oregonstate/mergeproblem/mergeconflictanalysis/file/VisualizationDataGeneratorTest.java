package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	public void testCreateCommitFolder() throws Exception {
		RevCommit commit = createConflictingCommit();
		CommitStatus commitStatus = new CommitStatus(repository, commit, new HashMap<String, CombinedFile>());
		generator.generateData(projectName, asList(commitStatus), tempFolderAbsolutePath);
		Path commitPath = tempDirectory.resolve(projectName).resolve(commit.getName());
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
	public void testSolvedContents() throws Exception {
		Path locFolder = generateLOCPath();
		
		assertFileContains(locFolder, "public class A{}", "S");
	}
	
	@Test
	public void testIndexGeneration() throws Exception {
		Path locFolder = generateLOCPath();
		
		assertFileContains(locFolder, new LOCIndexHtml().getIndex(), "index.html");
	}

	private void assertFileContains(Path path, String fileContents, String fileName) throws IOException {
		Path aPath = path.resolve(fileName);
		assertFileContains(aPath, fileContents);
	}

	private void assertFileContains(Path filePath, String fileContents) throws IOException {
		String aContents = getFileContents(filePath);
		assertEquals(fileContents, aContents);
	}

	private String getFileContents(Path filePath) throws IOException {
		byte[] bytes = Files.readAllBytes(filePath);
		String aContents = new String(bytes);
		return aContents;
	}

	private Path generateLOCPath() throws Exception {
		Path fileFolder = generateFileFolder();
		
		Path locFolder = fileFolder.resolve("loc");
		return locFolder;
	}

	private Path generateFileFolder() throws Exception {
		RevCommit commit = createConflictingCommit();
		CommitStatus commitStatus = new InMemoryMerger(repository).recreateMerge(commit);
		new VisualizationDataGenerator().generateData(projectName, asList(commitStatus), tempFolderAbsolutePath);
		Path fileFolder = resolveFileFolder(commit, getConflictingFileName(commitStatus));
		return fileFolder;
	}

	private String getConflictingFileName(CommitStatus commitStatus) {
		return commitStatus.getListOfConflictingFiles().get(0);
	}

	private List<CommitStatus> asList(CommitStatus commitStatus) {
		return Arrays.asList(new CommitStatus[]{commitStatus});
	}
	
	@Test
	public void testCreateASTPath() throws Exception {
		Path astFolder = generateASTFolder();
		assertIsFolder(astFolder);
	}

	private Path generateASTFolder() throws Exception {
		Path fileFolder = generateFileFolder();
		Path astFolder = fileFolder.resolve("ast");
		return astFolder;
	}
	
	@Test
	public void testCreateASTIndex() throws Exception {
		Path astFolder = generateASTFolder();
		Path index = astFolder.resolve("index.html");
		assertTrue(index.toFile().exists());
		assertFileContains(astFolder, new ASTFileGenerator().generateIndex(), "index.html");
	}
	
	@Test
	public void testCreateASTDiffAtoB() throws Exception {
		Path atob = generateASTFolder().resolve("atob.html");
		assertTrue(atob.toFile().exists());
		assertFileContains(atob, "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/><title>GumTree</title><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\" rel=\"stylesheet\"/><link type=\"text/css\" href=\"//res/web/gumtree.css\" rel=\"stylesheet\"/></head><body><div class=\"container-fluid\"><div class=\"row\"><div class=\"col-lg-12\"><div class=\"btn-toolbar pull-right\"><div class=\"btn-group\"><a class=\"btn btn-default btn-xs\" id=\"legend\" href=\"#\" data-toggle=\"popover\" data-html=\"true\" data-placement=\"bottom\" data-content=\"<span class=&quot;del&quot;>&nbsp;&nbsp;</span> deleted<br><span class=&quot;add&quot;>&nbsp;&nbsp;</span> added<br><span class=&quot;mv&quot;>&nbsp;&nbsp;</span> moved<br><span class=&quot;upd&quot;>&nbsp;&nbsp;</span> updated<br>\" data-original-title=\"Legend\" title=\"Legend\" role=\"button\">Legend</a><a class=\"btn btn-default btn-xs\" id=\"shortcuts\" href=\"#\" data-toggle=\"popover\" data-html=\"true\" data-placement=\"bottom\" data-content=\"<b>n</b> next<br><b>t</b> top<br><b>b</b> bottom\" data-original-title=\"Shortcuts\" title=\"Shortcuts\" role=\"button\">Shortcuts</a></div></div></div></div><div class=\"row\"><div class=\"col-lg-6 max-height\"><h5>A</h5><pre class=\"pre max-height\">public class <span class=\"marker\" id=\"mapping-1\"></span><span class=\"token upd\" id=\"move-src-1\" data-title=\"TypeDeclaration/SimpleName\">conflictingA3</span>{}\n" + 
				"</pre></div><div class=\"col-lg-6 max-height\"><h5>B</h5><pre class=\"pre max-height\">public class <span class=\"marker\" id=\"mapping-2\"></span><span class=\"token upd\" id=\"move-dst-1\" data-title=\"TypeDeclaration/SimpleName\">A2</span>{}\n" + 
				"</pre></div></div></div><script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script><script type=\"text/javascript\" src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script><script type=\"text/javascript\" src=\"//res/web/diff.js\"></script><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\" rel=\"stylesheet\"/><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css\" rel=\"stylesheet\"/></body></html>");
	}
	
	@Test
	public void testCreateASTDiffAtoS() throws Exception {
		Path atos = generateASTFolder().resolve("atos.html");
		assertTrue(atos.toFile().exists());
		assertFileContains(atos, "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/><title>GumTree</title><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\" rel=\"stylesheet\"/><link type=\"text/css\" href=\"//res/web/gumtree.css\" rel=\"stylesheet\"/></head><body><div class=\"container-fluid\"><div class=\"row\"><div class=\"col-lg-12\"><div class=\"btn-toolbar pull-right\"><div class=\"btn-group\"><a class=\"btn btn-default btn-xs\" id=\"legend\" href=\"#\" data-toggle=\"popover\" data-html=\"true\" data-placement=\"bottom\" data-content=\"<span class=&quot;del&quot;>&nbsp;&nbsp;</span> deleted<br><span class=&quot;add&quot;>&nbsp;&nbsp;</span> added<br><span class=&quot;mv&quot;>&nbsp;&nbsp;</span> moved<br><span class=&quot;upd&quot;>&nbsp;&nbsp;</span> updated<br>\" data-original-title=\"Legend\" title=\"Legend\" role=\"button\">Legend</a><a class=\"btn btn-default btn-xs\" id=\"shortcuts\" href=\"#\" data-toggle=\"popover\" data-html=\"true\" data-placement=\"bottom\" data-content=\"<b>n</b> next<br><b>t</b> top<br><b>b</b> bottom\" data-original-title=\"Shortcuts\" title=\"Shortcuts\" role=\"button\">Shortcuts</a></div></div></div></div><div class=\"row\"><div class=\"col-lg-6 max-height\"><h5>A</h5><pre class=\"pre max-height\">public class <span class=\"marker\" id=\"mapping-1\"></span><span class=\"token upd\" id=\"move-src-1\" data-title=\"TypeDeclaration/SimpleName\">conflictingA3</span>{}\n" +
				"</pre></div><div class=\"col-lg-6 max-height\"><h5>Solved</h5><pre class=\"pre max-height\">public class <span class=\"marker\" id=\"mapping-2\"></span><span class=\"token upd\" id=\"move-dst-1\" data-title=\"TypeDeclaration/SimpleName\">A</span>{}</pre></div></div></div><script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script><script type=\"text/javascript\" src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script><script type=\"text/javascript\" src=\"//res/web/diff.js\"></script><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\" rel=\"stylesheet\"/><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css\" rel=\"stylesheet\"/></body></html>");
	}
	
	@Test
	public void testCreateASTDiffBtoS() throws Exception {
		Path btos = generateASTFolder().resolve("btos.html");
		assertTrue(btos.toFile().exists());
		assertFileContains(btos, "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf8\"/><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/><title>GumTree</title><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\" rel=\"stylesheet\"/><link type=\"text/css\" href=\"//res/web/gumtree.css\" rel=\"stylesheet\"/></head><body><div class=\"container-fluid\"><div class=\"row\"><div class=\"col-lg-12\"><div class=\"btn-toolbar pull-right\"><div class=\"btn-group\"><a class=\"btn btn-default btn-xs\" id=\"legend\" href=\"#\" data-toggle=\"popover\" data-html=\"true\" data-placement=\"bottom\" data-content=\"<span class=&quot;del&quot;>&nbsp;&nbsp;</span> deleted<br><span class=&quot;add&quot;>&nbsp;&nbsp;</span> added<br><span class=&quot;mv&quot;>&nbsp;&nbsp;</span> moved<br><span class=&quot;upd&quot;>&nbsp;&nbsp;</span> updated<br>\" data-original-title=\"Legend\" title=\"Legend\" role=\"button\">Legend</a><a class=\"btn btn-default btn-xs\" id=\"shortcuts\" href=\"#\" data-toggle=\"popover\" data-html=\"true\" data-placement=\"bottom\" data-content=\"<b>n</b> next<br><b>t</b> top<br><b>b</b> bottom\" data-original-title=\"Shortcuts\" title=\"Shortcuts\" role=\"button\">Shortcuts</a></div></div></div></div><div class=\"row\"><div class=\"col-lg-6 max-height\"><h5>B</h5><pre class=\"pre max-height\">public class <span class=\"marker\" id=\"mapping-1\"></span><span class=\"token upd\" id=\"move-src-1\" data-title=\"TypeDeclaration/SimpleName\">A2</span>{}\n" +
				"</pre></div><div class=\"col-lg-6 max-height\"><h5>Solved</h5><pre class=\"pre max-height\">public class <span class=\"marker\" id=\"mapping-2\"></span><span class=\"token upd\" id=\"move-dst-1\" data-title=\"TypeDeclaration/SimpleName\">A</span>{}</pre></div></div></div><script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-1.11.2.min.js\"></script><script type=\"text/javascript\" src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script><script type=\"text/javascript\" src=\"//res/web/diff.js\"></script><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\" rel=\"stylesheet\"/><link type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css\" rel=\"stylesheet\"/></body></html>");
	}
	
	@Test
	public void testCommitWithNoJavaFiles() throws Exception {
		HashMap<String, CombinedFile> conflictingFiles = new HashMap<String, CombinedFile>();
		conflictingFiles.put("a.txt", new CombinedFile());
		add("a.txt", "one");
		branch("branch");
		add("a.txt", "two");
		checkout("master");
		add("a.txt", "three");
		merge("branch");
		RevCommit commit = add("a.txt", "bla");
		CommitStatus status = new CommitStatus(repository, commit, conflictingFiles);
		
		new VisualizationDataGenerator().generateData(projectName, asList(status), tempFolderAbsolutePath);
		Path commitDir = tempDirectory.resolve(tempDirectory).resolve("abcde");
		assertFalse(commitDir.toFile().exists());		
	}
}
