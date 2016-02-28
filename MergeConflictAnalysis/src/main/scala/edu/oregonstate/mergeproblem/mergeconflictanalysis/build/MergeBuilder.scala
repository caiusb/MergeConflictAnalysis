package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.merge.StrategyRecursive
import org.eclipse.jgit.revwalk.RevCommit
import org.gitective.core.CommitUtils
import edu.oregonstate.mergeproblem.mergeconflictanalysis.Builder

object MergeBuilder {

  final def SUCCESS = "pass"
  final def BUILD_FAIL = "build"
  final def TEST_FAIL = "test"
  final def MERGE_FAIL = "text"

  def mergeAndBuild(git: Git, commitID: String): String = {
    val commit = CommitUtils.getCommit(git.getRepository, commitID)
    val parents = commit.getParents
    val parentResults = parents.map(p => checkoutAndBuild(git, p)).fold("")((left, right) => left + "," + right)
    val result = commitID + parentResults
    val project = git.getRepository.getWorkTree.getAbsolutePath
    if(merge(git, parents))
      if (Builder.build(project))
        if (Builder.test(project))
          return result + "," + SUCCESS
        else
          return result + "," + TEST_FAIL
      else
        return result + "," + BUILD_FAIL
    else
      return result + "," + MERGE_FAIL
  }

  def merge(git: Git, commits: Array[RevCommit]): Boolean = {
    new StrategyRecursive().newMerger(git.getRepository, true).merge(commits: _*)
  }

  def checkoutAndBuild(git: Git, p: RevCommit): String = {
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
