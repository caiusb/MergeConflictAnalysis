package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

import org.eclipse.jgit.diff.{RawText, RawTextComparator, DiffAlgorithm}
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm

trait LOCDiff {

  private val diffAlgorithm = DiffAlgorithm.getAlgorithm(SupportedAlgorithm.MYERS);

  def getDiff(a: String, b:String) =
    diffAlgorithm.diff(RawTextComparator.DEFAULT, new RawText(a.getBytes()), new RawText(b.getBytes))

}
