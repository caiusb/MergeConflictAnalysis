package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;

public class CommitStatus {
	private Repository repository;
	private String sha1;
	private int time;
	
	private Map<String, FileStatus> fileStatuses = new HashMap<String, FileStatus>();
	
	public CommitStatus(Repository repository, String sha1, Map<String, CombinedFile> conflictingFiles, int time) {
		this.repository = repository;
		this.sha1 = sha1;
		this.time = time;
		
		for (String file : conflictingFiles.keySet()) {
			FileStatus filestatus = new FileStatus(this, file, conflictingFiles.get(file));
			fileStatuses.put(file, filestatus);
		}
	}
	
	public List<String> getListOfConflictingFiles() {
		return fileStatuses.values().stream().filter(status -> status.isConflicting())
				.map((status) -> status.getFileName()).collect(Collectors.toList());
	}
	
	public String getSolvedVersion(String fileName) {
		return fileStatuses.get(fileName).getSolvedVersion();
	}
	
	public CombinedFile getCombinedFile(String fileName) {
		return fileStatuses.get(fileName).getCombinedFile();
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
	
	public void addModifiedFiles(List<String> modifiedFiles) {
		
	}
	
	public List<String> getModifiedFiles() {
		List<String> list = Collections.emptyList();
		list.addAll(fileStatuses.keySet());
		return list;
	}
}
