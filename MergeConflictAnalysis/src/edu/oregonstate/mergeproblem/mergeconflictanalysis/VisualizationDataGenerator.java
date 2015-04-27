package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class VisualizationDataGenerator {
	
	private String urlFolder = "";
	private LOCIndexHtml locIndexHtml;

	public void setURLFolder(String folder) {
		this.urlFolder = folder;
		locIndexHtml = new LOCIndexHtml(urlFolder);
	}
	
	public void generateData(String projectName, List<CommitStatus> statuses, String targetFolder) {
		Path projectPath = Paths.get(targetFolder).resolve(projectName);
		projectPath.toFile().mkdir();
		statuses.forEach((status) -> {
			String sha1 = status.getSHA1();
			Path commitPath = projectPath.resolve(sha1);
			commitPath.toFile().mkdir();
			status.getListOfConflictingFiles().forEach((file) -> {
				Path filePath = commitPath.resolve(file);
				filePath.toFile().mkdirs();
				generateLOCData(locIndexHtml, status, file, filePath);
			}); 
		});
	}

	private void generateLOCData(LOCIndexHtml locIndexHtml, CommitStatus status, String fileName, Path pathToPopulate) {
		Path locPath = pathToPopulate.resolve("loc");
		locPath.toFile().mkdir();
		
		try {
			Path a = createFile(locPath, "A");
			writeToFile(a, status.getCombinedFile(fileName).getVersion(ChunkOwner.A));
			Path b = createFile(locPath, "B");
			writeToFile(b, status.getCombinedFile(fileName).getVersion(ChunkOwner.B));
			Path s = createFile(locPath, "S");
			writeToFile(s, status.getSolvedVersion(fileName));
			Path index = createFile(locPath, "index.html");
			writeToFile(index, locIndexHtml.getIndex());
		} catch (Exception e) {
		}
	}

	private void writeToFile(Path a, String contents) throws IOException {
		Files.write(a, contents.getBytes(), StandardOpenOption.WRITE);
	}

	private Path createFile(Path folder, String fileName) throws IOException {
		Path a = folder.resolve(fileName);
		a.toFile().createNewFile();
		return a;
	}

}
