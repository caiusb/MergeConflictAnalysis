package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class VisualizationDataGenerator {
	
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
				Path locPath = filePath.resolve("loc");
				locPath.toFile().mkdir();
				
				Path a = locPath.resolve("A");
				Path b = locPath.resolve("B");
				Path index=locPath.resolve("index.html");
				try {
					a.toFile().createNewFile();
					Files.write(a, status.getCombinedFile(file).getVersion(ChunkOwner.A).getBytes(), StandardOpenOption.WRITE);
					b.toFile().createNewFile();
					Files.write(b, status.getCombinedFile(file).getVersion(ChunkOwner.B).getBytes(), StandardOpenOption.WRITE);
					index.toFile().createNewFile();
					Files.write(index, LOCIndexHtml.LOC_INDEX.getBytes(), StandardOpenOption.WRITE);
				} catch (Exception e) {
				}
			}); 
		});
	}

}
