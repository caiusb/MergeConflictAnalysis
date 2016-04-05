package edu.oregonstate.mergeproblem.mergeconflictanalysis.processors

import java.util

import fr.labri.gumtree.actions.model.Action
import fr.labri.gumtree.gen.jdt.JdtTree
import fr.labri.gumtree.tree.Tree

import scala.collection.JavaConversions._

class PreMergeAffectedNodesProcessor extends AbstractPerMergeASTDiffProcessor {

  override def getHeader: String = "AFFECTED_NODES"

  override def getResults(aTree: Tree, bTree: Tree, actions: util.List[Action]): String =
    asScalaBuffer(actions).map(a => a.getNode.asInstanceOf[JdtTree].getContainedNode.getClass.getSimpleName).distinct.mkString(";")
}
