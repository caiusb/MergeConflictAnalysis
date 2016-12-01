package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors
import com.brindescu.conflict.detector.ConflictDetector
import edu.oregonstate.mergeproblem.mergeconflictanalysis.Util
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.CommitStatus
import org.eclipse.jgit.revwalk.RevCommit
import org.gitective.core.CommitUtils

class IsASTConflict extends FileProcessor {

	override def getHeader: String = "IS_AST_CONFLICT"

	override def getData(status: CommitStatus, fileName: String): String = {
		val a = Util.retrieveFile(status.getRepository, status.getASHA, fileName)
		val b = Util.retrieveFile(status.getRepository, status.getBSHA, fileName)
		val baseCommit = CommitUtils.getBase(status.getRepository, status.getASHA, status.getBSHA)
		if (baseCommit == null)
			return "False"
		val baseSHA = baseCommit.getName
		val base = Util.retrieveFile(status.getRepository, baseSHA, fileName)
		if (base == null)
			return "NA"

		if (ConflictDetector.findConflictBetween(a, b, base))
			return "True"
		else
			return "False"
	}
}
