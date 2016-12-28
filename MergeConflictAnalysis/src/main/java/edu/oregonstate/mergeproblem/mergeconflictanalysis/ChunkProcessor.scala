package edu.oregonstate.mergeproblem.mergeconflictanalysis

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.{ChunkOwner, CommitStatus}
import edu.oregonstate.mergeproblem.mergeconflictanalysis.processors.FileProcessor

class ChunkProcessor extends FileProcessor {

	override def getHeader: String = "NO_CHUNKS"

	override def getData(status: CommitStatus, fileName: String): String =
		return "" + status.getCombinedFile(fileName).getChunksForOwner(ChunkOwner.A).size()

}
