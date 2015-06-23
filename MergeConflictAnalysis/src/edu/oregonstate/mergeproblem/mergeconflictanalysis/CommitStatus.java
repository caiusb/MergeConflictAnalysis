package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;

public class CommitStatus {
	private Repository repository;
	private String sha1;
	private Map<String, CombinedFile> combinedFiles;
	private Map<String, String> solvedVersions;
	private int time;
	private List<String> conflictingFiles;
	private List<String> modifiedFiles = null;
	
	@SuppressWarnings("resource")
	public CommitStatus(Repository repository, String sha1, Map<String, CombinedFile> conflictingFiles, int time) {
		this.repository = repository;
		this.sha1 = sha1;
		this.combinedFiles = conflictingFiles;
		this.solvedVersions = new HashMap<String, String>(conflictingFiles.size());
		this.conflictingFiles = new ArrayList<>();
		this.conflictingFiles.addAll(conflictingFiles.keySet());
		this.time = time;
		for (String file : this.conflictingFiles) {
			String fileContents = Util.retrieveFile(repository, sha1, file);
			solvedVersions.put(file, fileContents);
		}
	}
	
	public List<String> getListOfConflictingFiles() {
		return conflictingFiles;
	}
	
	public String getSolvedVersion(String fileName) {
		return solvedVersions.get(fileName);
	}
	
	public CombinedFile getCombinedFile(String fileName) {
		return combinedFiles.get(fileName);
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
	
	public List<String> getModifiedFiles() {
		if (modifiedFiles == null)
			modifiedFiles = Util.getFilesChangedByCommit(repository, sha1);
		return modifiedFiles;
	}
}
