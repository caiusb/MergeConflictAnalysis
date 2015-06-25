package edu.oregonstate.mergeproblem.mergeconflictanalysis;

public class FileStatus {
	
	private CommitStatus parentCommitStatus;
	private CombinedFile file;
	private boolean isConflicting;
	private String name;
	private String solvedVersion;

	public FileStatus(CommitStatus parentCommitStatus, String name, CombinedFile file) {
		this(parentCommitStatus, name, file, true);
	}
	
	public FileStatus(CommitStatus parentCommitStatus, String name, CombinedFile file, boolean isConflicting) {
		this.parentCommitStatus = parentCommitStatus;
		this.name = name;
		this.file = file;
		this.isConflicting = isConflicting;
		this.solvedVersion = null;
	}
	
	public String getFileName() {
		return name;
	}
	
	public String getSolvedVersion() {
		if (solvedVersion == null)
			 solvedVersion = Util.retrieveFile(parentCommitStatus.getRepository(), parentCommitStatus.getSHA1(), name);
		return solvedVersion;
	}

	public boolean isConflicting() {
		return isConflicting;
	}

	public CombinedFile getCombinedFile() {
		return file;
	}
}
