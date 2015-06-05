package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.lib.Repository;

public class CommitStatus {
	private Repository repository;
	private String sha1;
	private Map<String, CombinedFile> conflictingFiles;
	private Map<String, String> solvedVersions;
	private int time;
	
	public CommitStatus(Repository repository, String sha1, Map<String, CombinedFile> conflictingFiles, int time) {
		this.repository = repository;
		this.sha1 = sha1;
		this.conflictingFiles = conflictingFiles;
		this.solvedVersions = new HashMap<String, String>(conflictingFiles.size());
		this.time = time;
		Set<String> conflictingFilesSet = conflictingFiles.keySet();
		for (String file : conflictingFilesSet) {
			String fileContents = FileRetriver.retrieveFile(repository, sha1, file);
			solvedVersions.put(file, fileContents);
		}
	}
	
	public List<String> getListOfConflictingFiles() {
		List<String> files = new ArrayList<>();
		files.addAll(conflictingFiles.keySet());
		return files;
	}
	
	public String getSolvedVersion(String fileName) {
		return solvedVersions.get(fileName);
	}
	
	public CombinedFile getCombinedFile(String fileName) {
		return conflictingFiles.get(fileName);
	}
	
	public String getSHA1() {
		return sha1;
	}
	
	public int getSolvedTime() {
		return time;
	}
	
	public Repository getRepository() {
		return repository;
	}
}
