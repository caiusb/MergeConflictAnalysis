package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class VisualizationDataGenerator {
	
	private String urlFolder = "";

	public void setURLFolder(String folder) {
		this.urlFolder = folder;
	}
	
	public void generateData(String projectName, List<CommitStatus> statuses, String targetFolder) {
		LOCIndexHtml locIndexHtml = new LOCIndexHtml(urlFolder);
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
		
		Path a = locPath.resolve("A");
		Path b = locPath.resolve("B");
		Path s = locPath.resolve("S");
		Path index=locPath.resolve("index.html");
		try {
			a.toFile().createNewFile();
			Files.write(a, status.getCombinedFile(fileName).getVersion(ChunkOwner.A).getBytes(), StandardOpenOption.WRITE);
			b.toFile().createNewFile();
			Files.write(b, status.getCombinedFile(fileName).getVersion(ChunkOwner.B).getBytes(), StandardOpenOption.WRITE);
			s.toFile().createNewFile();
			Files.write(s, status.getSolvedVersion(fileName).getBytes(), StandardOpenOption.WRITE);
			index.toFile().createNewFile();
			Files.write(index, locIndexHtml.getIndex().getBytes(), StandardOpenOption.WRITE);
		} catch (Exception e) {
		}
	}

}
