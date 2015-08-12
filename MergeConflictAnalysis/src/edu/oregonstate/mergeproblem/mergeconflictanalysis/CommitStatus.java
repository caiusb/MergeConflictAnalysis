package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.Repository;

public class CommitStatus {
	private Repository repository;
	private String sha1;
	private int time;
	private int aTime;
	private int bTime;
	
	private Map<String, FileStatus> fileStatuses = new HashMap<String, FileStatus>();
	private String aSHA;
	private String bSHA;
	private int timeOffset;
	
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
	
	public void addModifiedFiles(Map<String, String> modifiedFiles) {
		for (String file : modifiedFiles.keySet()) {
			if (fileStatuses.containsKey(file))
				continue;
			CombinedFile combinedFile = new CombinedFile().addChunk(ChunkOwner.BOTH, modifiedFiles.get(file));
			combinedFile.setATime(aTime);
			combinedFile.setBTime(bTime);
			FileStatus fileStatus = new FileStatus(this, file, combinedFile, false);
			fileStatuses.put(file, fileStatus);
		}
	}
	
	public List<String> getModifiedFiles() {
		List<String> list = new ArrayList<String>();
		list.addAll(fileStatuses.keySet());
		return list;
	}
	
	public void setTimes(int aTime, int bTime) {
		this.aTime = aTime;
		this.bTime = bTime;
	}

	public void setSHAs(String aSHA, String bSHA) {
		this.aSHA = aSHA;
		this.bSHA = bSHA;
	}
	
	public String getASHA() {
		return aSHA;
	}
	
	public String getBSHA() {
		return bSHA;
	}
	
	public void setTimeOffset(int offset) {
		this.timeOffset = offset;
	}

	public int getTimeOffset() {
		return timeOffset;
	}
}
