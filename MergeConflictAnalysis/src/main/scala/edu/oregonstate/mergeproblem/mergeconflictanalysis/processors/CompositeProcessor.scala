package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus

import scala.collection.mutable.ListBuffer

class CompositeProcessor extends FileProcessor {
	private val processors  = new ListBuffer[FileProcessor]

	def addProcessor(processor: FileProcessor) =
		processors :+ processor

	def getHeader: String =
		processors map { _.getHeader } mkString ","

	def getData(status: CommitStatus, fileName: String): String =
		processors map { _.getData(status, fileName) } mkString ","
}