package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpecLike}

import scala.io.Source

class MergeBuilder extends MergeGitTest with FlatSpecLike with Matchers with BeforeAndAfter {

  before {
    setUp()
  }

  def getPOMContent: String =
    Source.fromFile(getClass.getResource("/example.pom.xml").getFile).mkString

  it should "correctly merge a simple example" in {
    add(testRepo, "pom.xml", getPOMContent)
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x{\nreturn 0;\n}\n}")
    branch("branch")
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x{\nreturn 0;\n}\npublic void n(){\n}\n}")
    checkout("master")
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x{\nreturn 1;\n}\n}")
    val result = merge("branch")
    result.getMergeStatus.isSuccessful should be (true)
  }
}
