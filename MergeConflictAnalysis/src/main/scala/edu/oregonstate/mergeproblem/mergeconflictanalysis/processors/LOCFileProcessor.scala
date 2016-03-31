package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

class LOCFileProcessor extends DiffFileProcessor with LOCDiff {

  def getHeader: String =
    "LOC_A_TO_B,LOC_A_TO_SOLVED,LOC_B_TO_SOLVED"

  def getDiffSize(aVersion: String, bVersion: String): Int =
    getDiff(aVersion, bVersion).size
}