package edu.oregonstate.mergeproblem.mergeconflictanalysis

import org.gitective.tests.GitTestCase
import org.scalatest.{FlatSpecLike, Matchers}

class BuilderTest extends GitTestCase with FlatSpecLike with Matchers {

  it should "correctly build a simple example" in {
    Builder.build(getClass.getResource("/simple-good-pom").getPath) should be (true)
  }

  it should "correctly fail on compilation error" in {
    Builder.build(getClass.getResource("/simple-bad-pom").getPath) should be (false)
  }
}