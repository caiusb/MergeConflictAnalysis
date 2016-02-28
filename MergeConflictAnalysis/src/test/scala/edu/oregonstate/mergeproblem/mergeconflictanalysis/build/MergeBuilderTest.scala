package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest
import edu.oregonstate.mergeproblem.mergeconflictanalysis.build.MergeBuilder._
import org.eclipse.jgit.api.{Git, MergeResult}
import org.eclipse.jgit.revwalk.RevCommit
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpecLike}

import scala.io.Source

class MergeBuilderTest extends MergeGitTest with FlatSpecLike with Matchers with BeforeAndAfter {

  before {
    setUp
  }

  after {
    deleteRepo
  }

  def getPOMContent: String = {
    getResourceContent("/simple-good-pom/pom.xml")
  }

  def getResourceContent(s: String): String = {
    Source.fromFile(getClass.getResource(s).getFile).mkString
  }

  def mergeSuccessfulWithPOM: MergeResult = {
    addBasicClassAndTest
    branch("branch")
    addNewMethodToClass
    checkout("master")
    add(testRepo, "src/main/java/A.java", getResourceContent("/merging/changed-class.java"))
    val result = merge("branch")
    result
  }

  def addNewMethodToClass: RevCommit = {
    add(testRepo, "src/main/java/A.java", getResourceContent("/merging/alternative-class.java"))
  }

  def addBasicClassAndTest: RevCommit = {
    add(testRepo, "pom.xml", getPOMContent)
    add(testRepo, "src/main/java/A.java", getResourceContent("/merging/simple-class.java"))
    add(testRepo, "src/test/java/TestA.java", getResourceContent("/merging/passing-test.java"))
  }

  it should "correctly merge a simple example" in {
    val result = mergeSuccessfulWithPOM
    result.getMergeStatus.isSuccessful should be (true)
  }

  it should "correcly report three successful builds" in {
    val result = mergeSuccessfulWithPOM
    val newHead = result.getNewHead.getName
    mergeAndBuild(Git.open(testRepo), newHead) should be (newHead + "," + SUCCESS + "," + SUCCESS + "," + SUCCESS)
  }

  it should "correctly report a merge failure" in {
    conflictingMergePOM
    merge("branch").getMergeStatus.isSuccessful should be (false)
    val resolved = add("src/main/java/A.java", getResourceContent("/merging/solution-class.java"))
    mergeAndBuild(Git.open(testRepo), resolved.getName) should be (resolved.getName + "," + SUCCESS + "," + SUCCESS + "," + MERGE_FAIL)
  }

  def conflictingMergePOM: RevCommit = {
    addBasicClassAndTest
    branch("branch")
    add("src/main/java/A.java", getResourceContent("/merging/changed-class.java"))
    checkout("master")
    add("src/main/java/A.java", getResourceContent("/merging/conflicting-class.java"))
  }

  it should "correctly report a test failure in a parent" in {
    addBasicClassAndTest
    branch("branch")
    addNewMethodToClass
    add("src/test/java/TestA.java", getResourceContent("/merging/failing-test.java"))
    checkout("master")
    add("src/main/java/A.java", getResourceContent("/merging/changed-class.java"))
    val commit = merge("branch")
    val name = commit.getNewHead.getName
    mergeAndBuild(Git.open(testRepo), name) should be (name + "," + SUCCESS + "," + TEST_FAIL + "," + TEST_FAIL)
  }
}
