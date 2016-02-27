package edu.oregonstate.mergeproblem.mergeconflictanalysis.build

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest
import org.scalatest.{Matchers, FlatSpecLike}

import scala.io.Source

class MergeBuilder extends MergeGitTest with FlatSpecLike with Matchers {

  def getPOMContent: String =
    Source.fromFile(getClass.getResource("example.pom.xml").getFile).mkString

  it should "correctly merge a simple example" in {
    add(testRepo, "pom.xml", getPOMContent)
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x{\nreturn 0;\n}}")
    checkout("branch")
    add(testRepo, "src/main/java/A.java", "public class A{\npublic int x{\return 0;\n}\npublic void n(){\n}}")
    checkout("master")
    add(testRepo, "scr/main/java/A.java", "public class A{\npublic int x{\nreturn 1;\n}}")
    val result = merge("branch")
    result.getMergeStatus.isSuccessful should be (true)
  }
}
