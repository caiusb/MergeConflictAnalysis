package edu.oregonstate.mergeproblem.mergeconflictanalysis

import java.io.File

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.ConflictContentProcessor
import org.eclipse.jgit.api.Git

import scala.collection.JavaConversions._

object ContentsAnalysis extends App {

	override def main(args: Array[String]) = {
		val repoLocation = args(0)
		val git = Git.open(new File(repoLocation))
		val mc = new RepositoryWalker(git.getRepository).getMergeCommits
		val merger = new InMemoryMerger(git.getRepository)
		val statuses = mc.par.map(merger.recreateMerge(_))
		println(ConflictContentProcessor.getHeader)
		print(statuses.par.filter{_.getListOfConflictingFiles.size() > 0 	}
			.map{s => s.getListOfConflictingFiles.map(ConflictContentProcessor.getData(s,_)).mkString("\n")}.mkString("\n"))
	}
}