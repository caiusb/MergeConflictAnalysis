package edu.oregonstate.mergeproblem.mergeconflictanalysis

import java.io.File

import org.apache.maven.shared.invoker.{DefaultInvocationRequest, DefaultInvoker, InvocationOutputHandler, InvocationResult}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object MavenBuilder {

  val nullOutputHandler = new InvocationOutputHandler {
    override def consumeLine(line: String) = {}
  }

  private class Handler extends InvocationOutputHandler {
    private val builder = new ListBuffer[String]
    override def consumeLine(line: String): Unit = builder += line
    def getOutput = builder.toList
  }

  def runMaven(projectPath: String, tasks: Seq[String],
               outputHandler: InvocationOutputHandler = nullOutputHandler,
               errorHandler: InvocationOutputHandler = nullOutputHandler): Boolean =
    if (invoke(projectPath, tasks, outputHandler, errorHandler).getExitCode != 0)
      return false
    else
      return true

  private def invoke(projectPath: String, tasks: Seq[String],
                     outputHandler: InvocationOutputHandler,
                     errorHandler: InvocationOutputHandler): InvocationResult = {
    val request = new DefaultInvocationRequest
    request.setPomFile(new File(projectPath + "/pom.xml"))
    request.setGoals(seqAsJavaList(tasks))
    request.setInteractive(false)
    val invoker = new DefaultInvoker
    invoker.setOutputHandler(outputHandler)
    invoker.setErrorHandler(errorHandler)
    val result = invoker.execute(request)
    result
  }

  def build(projectPath: String): Boolean =
    runMaven(projectPath, Seq("clean", "compile"))

  def test(projectPath: String): Boolean =
    runMaven(projectPath, Seq("clean", "test"))

  def clean(projectPath: String): Boolean =
    runMaven(projectPath, Seq("clean"))

  private def makeMavenHappy(projectPath: String) =
    System.setProperty("maven.multiModuleProjectDirectory", projectPath)

  def getClasspathEntries(projectPath: String): List[String] = {
    val handler = new Handler()
    runMaven(projectPath, Seq("dependency:build-classpath"), handler)
    handler.getOutput.find( ! _.startsWith("[INFO]")) match {
      case Some(l) => l.split(":").toList
      case None => List()
    }
  }
}
