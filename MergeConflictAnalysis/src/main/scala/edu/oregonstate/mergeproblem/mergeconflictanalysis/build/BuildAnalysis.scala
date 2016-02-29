package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import java.io.{File, BufferedOutputStream}

import edu.oregonstate.mergeproblem.mergeconflictanalysis.{MergeFilter, RepositoryWalker, Main, AbstractAnalysis}
import org.eclipse.jgit.api.Git

import scala.collection.JavaConversions._

object BuildAnalysis extends AbstractAnalysis {

  override def doAnalysis(config: Main#Config, outputStream: BufferedOutputStream) = {
    asScalaBuffer(config.repositories).foreach(r => {
      val git = Git.open(new File(r))
      val repo = git.getRepository
      val mergeCommits = asScalaBuffer(new RepositoryWalker(repo).getMergeCommits)
      val result = mergeCommits.map(c => MergeBuilder.mergeAndBuild(git, c.getName, outputStream))
    })
  }
}
