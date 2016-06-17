package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import java.io.{BufferedOutputStream, File, OutputStream}

import edu.oregonstate.mergeproblem.mergeconflictanalysis.{AbstractAnalysis, Builder, Main, RepositoryWalker}
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

import scala.collection.JavaConversions._

object MergeOnlyBuildAnalysis extends AbstractAnalysis {
	override def doAnalysis(config: Main#Config, outputStream: BufferedOutputStream): Unit = {
		config.repositories.foreach { r =>
			val git = Git.open(new File(r))
			val repo = git.getRepository
			val mergeCommits = new RepositoryWalker(repo).getMergeCommits
			mergeCommits.foreach{ buildCommit(git,_,outputStream) }
		}
	}

	private def buildCommit(git: Git, commit: RevCommit, stream: OutputStream) = {
		git.checkout().setForce(true).setName(commit.getName).call()
		val project = git.getRepository.getWorkTree.getAbsolutePath
		Builder.clean(project)
		if (!Builder.build(project))
			stream.write((commit.getName + "build" + "\n").getBytes)
		else if (!Builder.test(project))
			stream.write((commit.getName + "test" + "\n").getBytes)
		else
			stream.write((commit.getName + "pass" + "\n").getBytes)
		Builder.clean(project)
	}
}
