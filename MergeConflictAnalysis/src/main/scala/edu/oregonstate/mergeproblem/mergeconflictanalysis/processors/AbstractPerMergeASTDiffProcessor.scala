package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors
import edu.oregonstate.mergeproblem.mergeconflictanalysis.file.ASTDiff
import fr.labri.gumtree.actions.model.Action
import fr.labri.gumtree.tree.Tree
import java._

abstract class AbstractPerMergeASTDiffProcessor extends AbstractPreMergeProcessor {

  override protected def getResults(a: String, b: String): String = {
    val diff = new ASTDiff()
    val aTree = diff.getTree(a)
    val bTree = diff.getTree(b)
    val actions = diff.getActions(aTree, bTree)
    getResults(aTree, bTree, actions)
  }

  def getResults(aTree: Tree, bTree: Tree, actions: util.List[Action]): String
}