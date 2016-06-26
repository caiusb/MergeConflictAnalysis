package edu.oregonstate.mergeproblem.mergeconflictanalysis

import java.io.File

import org.apache.maven.shared.invoker.{DefaultInvocationRequest, DefaultInvoker, InvocationOutputHandler, InvocationResult}

import scala.collection.JavaConversions._

object Builder {

  val nullOutputHandler = new InvocationOutputHandler {
    override def consumeLine(line: String) = {}
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
}
