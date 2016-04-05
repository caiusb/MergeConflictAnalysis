package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

import edu.oregonstate.mergeproblem.mergeconflictanalysis.MergeGitTest
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.InMemoryMerger
import org.junit.Test
import org.scalatest.Matchers
import org.scalatest.junit.AssertionsForJUnit

class PreMergeAffectedNodesProcessorTest extends MergeGitTest with AssertionsForJUnit with Matchers {

  @Test def testHeader =
    new PreMergeAffectedNodesProcessor().getHeader should equal ("AFFECTED_NODES")

  @Test def testGetNodesInConflict = {
    val commit = createConflictingCommit()
    val status = new InMemoryMerger(repository).recreateMerge(commit)
    new PreMergeAffectedNodesProcessor().getData(status, "A.java") should equal ("SimpleName")
  }

  @Test def testGetMultipleNodesOfTheSameType = {
    add("A.java", "public class A{\npublic void m(){\nint x=3;\nint y=5;\n}\n}")
    branch("branch")
    add("A.java", "public class A{\npublic void m(){\nint x=4;\nint y=4;\n}\n")
    checkout("master")
    add("A.java", "public class A{\npublic void m(){\nint x=43;\nint y=43;\n}\n")
    val result = merge("branch")
    result.getMergeStatus.isSuccessful should be (false)
    val resolved = add("A.java", "public class A{\npublic void m(){\nint x=4;\nint y=3;\n}\n")
    val status = new InMemoryMerger(repository).recreateMerge(resolved)
    new PreMergeAffectedNodesProcessor().getData(status, "A.java") should equal ("NumberLiteral")
  }

  @Test def testGetMultipleNodesOfDifferentKinds = {
    add("A.java", "public class A{\npublic void m(){\nint x=3;\nint y=5;\n}\n}")
    branch("branch")
    add("A.java", "public class A{\npublic void m(){\nint x=4;\nint z=5;\n}\n")
    checkout("master")
    add("A.java", "public class A{\npublic void m(){\nint x=43;\nint t=5;\n}\n")
    val result = merge("branch")
    result.getMergeStatus.isSuccessful should be (false)
    val resolved = add("A.java", "public class A{\npublic void m(){\nint x=4;\nint p=5;\n}\n")
    val status = new InMemoryMerger(repository).recreateMerge(resolved)
    new PreMergeAffectedNodesProcessor().getData(status, "A.java") should equal ("NumberLiteral;SimpleName")
  }
}
