package edu.oregonstate.mergeproblem.mergeconflictanalysis

import org.gitective.tests.GitTestCase
import org.scalatest.{BeforeAndAfter, FlatSpecLike, Matchers}

class BuilderTest extends GitTestCase with FlatSpecLike with Matchers with BeforeAndAfter {

  val goodProject = getClass.getResource("/simple-good-pom").getPath
  import Builder._

  it should "correctly build a simple example" in {
    build(goodProject) should be (true)
    clean(goodProject)
  }

  it should "correctly fail on compilation error" in {
    val errorProject = getClass.getResource("/simple-bad-pom").getPath
    build(errorProject) should be (false)
    clean(errorProject)
  }

  it should "correctly test" in {
    test(goodProject) should be (true)
    clean(goodProject)
  }

  it should "fain on test failure" in {
    val testFailProject = getClass.getResource("/test-fail").getPath
    test(testFailProject) should be (false)
    clean(testFailProject)
  }

  it should "correctly clean a project" in {
    build(goodProject)
    getClass.getResource("/simple-good-pom/target") should not be (null)
    clean(goodProject)
    getClass.getResource("/simple-good-pom/target") should be (null)
  }
}