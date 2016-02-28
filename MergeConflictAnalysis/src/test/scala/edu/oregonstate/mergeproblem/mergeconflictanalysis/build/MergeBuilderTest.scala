package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest
import edu.oregonstate.mergeproblem.mergeconflictanalysis.build.MergeBuilder._
import org.eclipse.jgit.api.{Git, MergeResult}
import org.eclipse.jgit.revwalk.RevCommit
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpecLike}

import scala.io.Source

class MergeBuilderTest extends MergeGitTest with FlatSpecLike with Matchers with BeforeAndAfter {

  before {
    setUp()
  }

  def getPOMContent: String =
    Source.fromFile(getClass.getResource("/example.pom.xml").getFile).mkString

  def mergeSuccessfulWithPOM: MergeResult = {
    addBasicClassAndTest
    branch("branch")
    addNewMethodToClass
    checkout("master")
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x(){\nreturn 1;\n}\n}")
    val result = merge("branch")
    result
  }

  def addNewMethodToClass: RevCommit = {
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x(){\nreturn 0;\n}\npublic void n(){\n}\n}")
  }

  def addBasicClassAndTest: RevCommit = {
    add(testRepo, "pom.xml", getPOMContent)
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x(){\nreturn 0;\n}\n}")
    add(testRepo, "src/test/java/TestA.java", "import org.junit.*;\nimport static org.junit.Assert.*;\npublic class TestA{\n@Test\npublic void m(){\nassertTrue(true);\n}\n}")
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
    addBasicClassAndTest
    branch("branch")
    add("src/main/java/A.java", "public class A{\npublic int x(){\nreturn 7;}\n}")
    checkout("master")
    add("src/main/java/A.java", "public class A{\npublic int x(){\nreturn 98;}\n}")
    merge("branch").getMergeStatus.isSuccessful should be (false)
    val resolved = add("src/main/java/A.java", "public class A{\npublic int x(){\nreturn 7;\n}\npublic void n(){\n}\n}")
    mergeAndBuild(Git.open(testRepo), resolved.getName) should be (resolved.getName + "," + SUCCESS + "," + SUCCESS + "," + MERGE_FAIL)
  }

}
