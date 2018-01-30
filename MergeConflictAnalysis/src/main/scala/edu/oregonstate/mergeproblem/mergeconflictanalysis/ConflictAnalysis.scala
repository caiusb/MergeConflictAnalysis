package edu.oregonstate.mergeproblem.mergeconflictanalysis

import java.io.{BufferedOutputStream, File}

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger
import org.eclipse.jgit.api.Git

import scala.collection.JavaConverters._

object ConflictAnalysis extends AbstractAnalysis {

	override def doAnalysis(config: Main#Config, output: BufferedOutputStream) = {
		config.repositories.asScala.foreach(repoLocation => {
			val git = Git.open(new File(repoLocation))
			val mc = new RepositoryWalker(git.getRepository).getMergeCommits
			val merger = new InMemoryMerger(git.getRepository)
			val statuses = mc.asScala.par.map(merger.recreateMerge(_))
			statuses.foreach(s => {
				output.write((s.getSHA1 + (if(s.getListOfConflictingFiles.isEmpty) ",true\n" else ",false\n")).getBytes())
			})
		})
	}

}
