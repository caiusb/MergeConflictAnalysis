package edu.oregonstate.mergeproblem.mergeconflictanalysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.BlobUtils;
import org.gitective.core.CommitUtils;
import org.gitective.core.GitException;

public class Util {
	
	private static class LocalDiffEntry extends DiffEntry {

		public LocalDiffEntry(final String path) {
			oldPath = path;
			newPath = path;
		}

		private LocalDiffEntry setOldMode(final FileMode mode) {
			oldMode = mode;
			return this;
		}

		private LocalDiffEntry setNewMode(final FileMode mode) {
			newMode = mode;
			return this;
		}

		private LocalDiffEntry setChangeType(final ChangeType type) {
			changeType = type;
			return this;
		}

		private LocalDiffEntry setNewId(final AbbreviatedObjectId id) {
			newId = id;
			return this;
		}
	}
	
	public static String retrieveFile(Repository repository, String sha1, String filename) {
		String content = null;
		try {
			content = BlobUtils.getContent(repository, sha1, filename);
		} catch (GitException e) {
			return "";
		}
		if (content == null)
			content = "";
		return content;
	}
	
	public static List<String> getFilesChangedByCommit(Repository repository, String sha1) {
		List<String> changedFiles = new ArrayList<String>();
		TreeWalk treeWalk = new TreeWalk(repository);
		RevWalk walk = new RevWalk(repository);
		RevCommit commit = CommitUtils.getCommit(repository, sha1);
		RevTree mergeTree = commit.getTree();
		try {
			treeWalk.addTree(mergeTree);
			RevCommit[] parents = commit.getParents();
			for (RevCommit parent : parents) {
				treeWalk.addTree(walk.parseTree(parent));
			}
			List<DiffEntry> diffs = processTreeWalk(treeWalk);
			for (DiffEntry diffEntry : diffs) {
				changedFiles.add(diffEntry.getNewPath());
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			treeWalk.close();
			walk.close();
		}
		return changedFiles;
	}
	
	private static List<DiffEntry> processTreeWalk(TreeWalk walk) throws Exception {
		ArrayList<DiffEntry> diffs = new ArrayList<DiffEntry>();
		final MutableObjectId currentId = new MutableObjectId();
		final int currentTree = 0;
		while (walk.next()) {
			final int currentMode = walk.getRawMode(currentTree);
			int parentMode = 0;
			boolean same = false;
			for (int i = 0; i < currentTree; i++) {
				final int mode = walk.getRawMode(i);
				same = mode == currentMode && walk.idEqual(currentTree, i);
				if (same)
					break;
				parentMode |= mode;
			}
			if (same)
				continue;

			final LocalDiffEntry diff = new LocalDiffEntry(
					walk.getPathString());
			diff.setOldMode(FileMode.fromBits(parentMode));
			diff.setNewMode(FileMode.fromBits(currentMode));
			walk.getObjectId(currentId, currentTree);
			diff.setNewId(AbbreviatedObjectId.fromObjectId(currentId));
			if (parentMode == 0 && currentMode != 0)
				diff.setChangeType(ChangeType.ADD);
			else if (parentMode != 0 && currentMode == 0)
				diff.setChangeType(ChangeType.DELETE);
			else
				diff.setChangeType(ChangeType.MODIFY);
			diffs.add(diff);
		}
		
		return diffs;
	}
}
