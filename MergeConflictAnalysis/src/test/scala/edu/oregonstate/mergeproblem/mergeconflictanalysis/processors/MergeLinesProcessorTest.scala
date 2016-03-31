package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger
import org.junit.Test
import org.scalatest.Matchers
import org.scalatest.junit.AssertionsForJUnit

class MergeLinesProcessorTest extends MergeGitTest with AssertionsForJUnit with Matchers {

  @Test def testDiff = {
    val conflicting = createConflictingCommit()
    val status = new InMemoryMerger(repository).recreateMerge(conflicting)

    new MergeLinesProcessor().getData(status, "A.java") should equal("1;2")
  }

  @Test def testHeader = new MergeLinesProcessor().getHeader should equal ("LINES_AFFECTED")
}
