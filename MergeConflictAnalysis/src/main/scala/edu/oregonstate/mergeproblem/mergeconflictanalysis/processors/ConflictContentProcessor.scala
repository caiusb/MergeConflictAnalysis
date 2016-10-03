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

	def getHeader: String = "SHA,FILE_NAME,A_EMAIL,B_EMAIL,S_EMAIL," +
		"A_NAME,B_NAME,S_NAME" +
		"A_CONFLICT,B_CONFLICT,S_NEW," +
		"S_NEW_A,S_NEW_B," +
		"A_S,B_S" +
		"A_TOTAL,B_TOTAL"

	def getData(status: CommitStatus, fileName: String): String = {
		val cf = status.getCombinedFile(fileName)
		val a = cf.getVersion(A)
		val b = cf.getVersion(B)
		val s = status.getSolvedVersion(fileName)

		val diffAlgo = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS)

		val aLines = cf.getChunksForOwner(ChunkOwner.A).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct
		val bLines = cf.getChunksForOwner(ChunkOwner.B).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct

		val diffAS = diffAlgo.diff(RawTextComparator.DEFAULT, new RawText(a.getBytes), new RawText(s.getBytes))
		val diffBS = diffAlgo.diff(RawTextComparator.DEFAULT, new RawText(b.getBytes), new RawText(s.getBytes))

		val newSLinesToA = diffAS.flatMap(e => Range(e.getBeginA, e.getEndA)).distinct.diff(aLines)
		val newSLinesToB = diffBS.flatMap(e => Range(e.getBeginA, e.getEndA)).distinct.diff(bLines)
		val newSLines = newSLinesToA.union(newSLinesToB)

		val linesInAandS = aLines.diff(newSLinesToA)
		val linesInBandS = bLines.diff(newSLinesToB)

		val repo = status.getRepository
		val aEmail = CommitUtils.getCommit(repo, status.getASHA).getCommitterIdent.getEmailAddress
		val aName = CommitUtils.getCommit(repo, status.getASHA).getCommitterIdent.getName
		val bEmail = CommitUtils.getCommit(repo, status.getBSHA).getCommitterIdent.getEmailAddress
		val bName = CommitUtils.getCommit(repo, status.getBSHA).getCommitterIdent.getName
		val sEmail = CommitUtils.getCommit(repo, status.getSHA1).getCommitterIdent.getEmailAddress
		val sName = CommitUtils.getCommit(repo, status.getSHA1).getCommitterIdent.getName

		val aMergedLine = cf.getChunkForSource(ChunkSource.A).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct
		val bMergedLine = cf.getChunkForSource(ChunkSource.B).flatMap(c => Range(c.getBeginLine, c.getEndLine)).distinct

		return status.getSHA1 + "," + fileName + "," + aEmail + "," + bEmail + "," + sEmail +
			"," + aName + "," + bName + "," + sName +
			"," + aLines.size + "," + bLines.size + "," + newSLines.size +
			"," + newSLinesToA.size + "," + newSLinesToB.size +
		 	"," + linesInAandS.size + "," + linesInBandS.size +
			"," + aMergedLine.size + "," + bMergedLine.size
	}
}
