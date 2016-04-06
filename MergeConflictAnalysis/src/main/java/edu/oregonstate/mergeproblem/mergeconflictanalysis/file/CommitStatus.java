package edu.oregonstate.mergeproblem.mergeconflictanalysis.file;

import java.util.*;
import java.util.stream.Collectors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.Util;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gitective.core.CommitUtils;

public class CommitStatus {
	private Repository repository;
	private String sha1;
	private RevCommit commit;
	private int time;
	private int aTime;
	private int bTime;
	
	private Map<String, FileStatus> fileStatuses = new HashMap<String, FileStatus>();
	private String aSHA;
	private String bSHA;

	public CommitStatus(Repository repository, RevCommit commit, Map<String, CombinedFile> conflictingFiles) {
		this.repository = repository;
		this.commit = commit;
		this.sha1 = commit.getName();
		this.time = commit.getCommitTime();

		List<RevCommit> sortedParents = Arrays.asList(commit.getParents()).stream().map((c) -> CommitUtils.getCommit(repository, c))
				.sorted((o1, o2) -> getUTCTime(o1) < getUTCTime(o2) ? -1 : 1).collect(Collectors.toList());
		RevCommit first = CommitUtils.getCommit(repository, sortedParents.get(0));
		RevCommit second = CommitUtils.getCommit(repository, sortedParents.get(1));
		aTime = first.getCommitTime();
		aSHA = first.getName();
		bTime = second.getCommitTime();
		bSHA = second.getName();
		
		for (String file : conflictingFiles.keySet()) {
			FileStatus filestatus = new FileStatus(this, file, conflictingFiles.get(file));
			fileStatuses.put(file, filestatus);
		}

		addModifiedFiles(Util.getFilesChangedByCommit(repository, commit.getName()));
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
	
	public String getASHA() {
		return aSHA;
	}
	
	public String getBSHA() {
		return bSHA;
	}
	
	private int getUTCTime(RevCommit commit) {
		return commit.getCommitTime() + getTimeZoneOffset();
	}

	public int getTimeZoneOffset() {
		return commit.getCommitterIdent().getTimeZoneOffset() * 60;
	}
}
