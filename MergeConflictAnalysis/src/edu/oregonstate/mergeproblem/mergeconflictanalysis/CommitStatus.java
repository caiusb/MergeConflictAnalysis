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
		initModifiedFiles();
	}
	
	public List<String> getListOfConflictingFiles() {
		return conflictingFiles;
	}
	
	public String getSolvedVersion(String fileName) {
		return solvedVersions.get(fileName);
	}
	
	public CombinedFile getCombinedFile(String fileName) {
		CombinedFile combinedFile = combinedFiles.get(fileName);
		return combinedFile;
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
		return modifiedFiles;
	}

	private void initModifiedFiles() {
		modifiedFiles = Util.getFilesChangedByCommit(repository, sha1);
		for (String file : modifiedFiles) {
			if (conflictingFiles.contains(file))
				continue;
			String contents = Util.retrieveFile(repository, file, sha1);
			CombinedFile combinedFile = new CombinedFile();
			combinedFile.addChunk(ChunkOwner.BOTH, contents);
			combinedFiles.put(file, combinedFile);
		}
	}
}
