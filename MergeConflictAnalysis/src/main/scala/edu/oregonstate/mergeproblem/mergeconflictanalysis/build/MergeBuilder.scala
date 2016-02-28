package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import java.util.logging.Level

import org.eclipse.jgit.api.{ResetCommand, Git}
import org.eclipse.jgit.merge.StrategyRecursive
import org.eclipse.jgit.revwalk.RevCommit
import org.gitective.core.CommitUtils
import edu.oregonstate.mergeproblem.mergeconflictanalysis.{Main, Builder}

object MergeBuilder {

  final def SUCCESS = "pass"
  final def BUILD_FAIL = "build"
  final def TEST_FAIL = "test"
  final def MERGE_FAIL = "text"

  private val logger = Main.logger

  def mergeAndBuild(git: Git, commitID: String): String = {
    logger.log(Level.INFO, "Processing " + commitID)
    val commit = CommitUtils.getCommit(git.getRepository, commitID)
    val parents = commit.getParents
    logger.log(Level.INFO, "Parents: " + parents.mkString(","))
    val project = git.getRepository.getWorkTree.getAbsolutePath
    val parentResults = parents.map(p => {
      checkoutBuildClean(git, project, p)
    }).fold("")((left, right) => left + "," + right)
    val result = commitID + parentResults
    logger.log(Level.INFO, "Parent results: " + result)
    if(merge(git, parents))
      if (Builder.build(project))
        if (Builder.test(project))
          return result + "," + SUCCESS
        else
          return result + "," + TEST_FAIL
      else
        return result + "," + BUILD_FAIL
    else {
      cleanRepo(git)
      return result + "," + MERGE_FAIL
    }
  }

  def checkoutBuildClean(git: Git, project: String, p: RevCommit): String = {
    val result = checkoutAndBuild(git, p)
    Builder.clean(project)
    return result
  }

  private def merge(git: Git, commits: Array[RevCommit]): Boolean = {
    git.checkout.setName(commits(0).getName).call
    val result = git.merge.include(commits(1)).call
    return result.getMergeStatus.isSuccessful
  }

  private def cleanRepo(git: Git) =
    git.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call()

  private def checkoutAndBuild(git: Git, p: RevCommit): String = {
    git.checkout().setName(p.getName).call()
    val project = git.getRepository.getWorkTree.getAbsolutePath
    if (Builder.build(project))
      if (Builder.test(project))
        return SUCCESS
      else
        return TEST_FAIL
    else
      return BUILD_FAIL
  }
}
