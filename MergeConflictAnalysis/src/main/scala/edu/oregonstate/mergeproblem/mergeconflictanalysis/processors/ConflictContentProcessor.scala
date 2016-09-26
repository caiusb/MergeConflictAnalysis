package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

import edu.oregonstate.mergeproblem.mergeconflictanalysis.Util
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.Chunk.ChunkSource
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ChunkOwner._
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.{ChunkOwner, CommitStatus}
import org.eclipse.jgit.diff.{DiffAlgorithm, RawText, RawTextComparator}
import org.gitective.core.CommitUtils

import scala.collection.JavaConversions._
import scala.collection.mutable

object ConflictContentProcessor {

	def getHeader: String = "SHA,FILE_NAME,A_EMAIL,B_EMAIL,S_EMAIL,A_CONFLICT,B_CONFLICT,S_CONFLICT,S_DIFF_A,S_DIFF_B,A_TOTAL,B_TOTAL"

	def getData(status: CommitStatus, fileName: String): String = {
		val cf = status.getCombinedFile(fileName)
		val a = cf.getVersion(A)
		val b = cf.getVersion(B)
		val cl = cf.getConflictingLines
		val s = status.getSolvedVersion(fileName)

		val diffAlgo = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS)

		val conflictingLines = cf.getConflictingLines.map(l => (l, cf.getChunksForLine(l))).toMap
		val solved = Util.retrieveFile(status.getRepository, status.getSHA1, fileName)

		val aLines = cf.getChunksForOwner(ChunkOwner.A).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct
		val bLines = cf.getChunksForOwner(ChunkOwner.B).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct

		val diffAS = diffAlgo.diff(RawTextComparator.DEFAULT, new RawText(a.getBytes), new RawText(s.getBytes))
		val diffBS = diffAlgo.diff(RawTextComparator.DEFAULT, new RawText(b.getBytes), new RawText(s.getBytes))

		val newSLinesToA = diffAS.flatMap(e => Range(e.getBeginB, e.getEndB)).distinct.diff(aLines)
		val newSLinesToB = diffBS.flatMap(e => Range(e.getBeginB, e.getEndB)).distinct.diff(bLines)
		val newSLines = newSLinesToA.union(newSLinesToB)

		val aContribution = cf.getChunksForOwner(ChunkOwner.A).map(_.getContent).mkString.split("\n")
		val bContribution = cf.getChunksForOwner(ChunkOwner.B).map(_.getContent).mkString.split("\n")

		val repo = status.getRepository
		val aAuthor = CommitUtils.getCommit(repo, status.getASHA).getCommitterIdent.getEmailAddress
		val bAuthor = CommitUtils.getCommit(repo, status.getBSHA).getCommitterIdent.getEmailAddress
		val sAuthor = CommitUtils.getCommit(repo, status.getSHA1).getCommitterIdent.getEmailAddress

		val aMergedLine = cf.getChunkForSource(ChunkSource.A).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct
		val bMergedLine = cf.getChunkForSource(ChunkSource.B).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct

		return status.getSHA1 + "," + fileName + "," + aAuthor + "," + bAuthor + "," + sAuthor +
			"," + aLines.size + "," + bLines.size + "," + newSLines.size +
			"," + newSLinesToA + "," + newSLinesToB +
			"," + aMergedLine.size + "," + bMergedLine.size
	}
}
