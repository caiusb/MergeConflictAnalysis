package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors;

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gitective.core.CommitUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AuthorProcessor implements FileProcessor {

	@Override
	public String getHeader() {
		return "NO_AUTHORS";
	}

	@Override
	public String getData(CommitStatus status, String fileName) {
		Set<String> authors = new HashSet<String>();
		String mergeCommitSHA = status.getSHA1();
		RevCommit mergeCommit = CommitUtils.getCommit(status.getRepository(), mergeCommitSHA);
		RevCommit[] parents = mergeCommit.getParents();
		RevCommit first = parents[0];
		RevCommit second = parents[1];
		RevCommit base = CommitUtils.getBase(status.getRepository(), first, second);
		if (base == null)
			return "1";
		RevWalk walk = new RevWalk(status.getRepository());
		try {
			walk.markStart(mergeCommit);
			Iterator<RevCommit> iterator = walk.iterator();
			while (iterator.hasNext()) {
				RevCommit next = iterator.next();
				if (next.equals(base))
					break;
				String author = next.getAuthorIdent().getEmailAddress();
				authors.add(author);
			}
		} catch (MissingObjectException e) {
		} catch (IncorrectObjectTypeException e) {
		} catch (IOException e) {
		} finally {
			walk.close();
		}
		return authors.size() + "";
	}

}
