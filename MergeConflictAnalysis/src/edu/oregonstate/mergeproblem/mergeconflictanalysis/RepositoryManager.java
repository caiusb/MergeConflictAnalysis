package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

public class RepositoryManager {

	private File tempFolder;
	private Git tempRepo;

	private File remoteFile;
	private Git remoteRepo;

	public RepositoryManager(File remoteFile) throws Exception {
		this.remoteFile = remoteFile;
		tempFolder = new File("/Volumes/RAM Disk/tempgit/");
		tempRepo = Git.init().setDirectory(tempFolder).call();
		tempRepo.lsRemote().setRemote(remoteFile.getAbsolutePath()).call();
		remoteRepo = Git.open(remoteFile);

		StoredConfig targetConfig = tempRepo.getRepository().getConfig();
		targetConfig.setString("branch", "master", "remote", "origin");
		targetConfig.setString("branch", "master", "merge", "refs/heads/master");
		RemoteConfig config = new RemoteConfig(targetConfig, "origin");

		config.addURI(new URIish(remoteFile.getPath()));
		config.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		config.update(targetConfig);
	}

	public void pull(RevCommit revCommit) throws Exception {
		remoteRepo.getRepository().updateRef(Constants.HEAD).setNewObjectId(revCommit.getId());
		tempRepo.pull().call();
	}

	protected Git getTempRepo() {
		return tempRepo;
	}
	
	protected void clean() {
		tempFolder.delete();
	}

}
