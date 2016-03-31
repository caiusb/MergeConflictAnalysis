package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.{ChunkOwner, CommitStatus}

import scala.collection.JavaConversions._
import scala.collection.SortedSet

class MergeLinesProcessor extends FileProcessor with LOCDiff {

  override def getHeader = "LINES_AFFECTED"

  override def getData(status: CommitStatus, fileName: String): String = {
    val solved = status.getSolvedVersion(fileName)
    val combined = status.getCombinedFile(fileName)
    val a = combined.getVersion(ChunkOwner.A)
    val b = combined.getVersion(ChunkOwner.B)

    val diffA = asScalaBuffer(getDiff(a, solved))
    val aRange = SortedSet(diffA.map(e => e.getBeginB+1 to e.getBeginB+e.getLengthB+1).flatten:_*)
    val diffB = asScalaBuffer(getDiff(b, solved))
    val bRange = SortedSet(diffB.map(e => e.getBeginB+1 to e.getBeginB+e.getLengthB+1).flatten:_*)

    (aRange union bRange).mkString(";")
  }
}
