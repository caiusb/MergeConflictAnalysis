package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VisualizationDataGenerator {
	
	public void generateData(String projectName, List<CommitStatus> statuses, String targetFolder) {
		Path projectPath = Paths.get(targetFolder).resolve(projectName);
		projectPath.toFile().mkdir();
		statuses.forEach((status) -> {
			String sha1 = status.getSHA1();
			projectPath.resolve(sha1).toFile().mkdir();
		});
	}

}
