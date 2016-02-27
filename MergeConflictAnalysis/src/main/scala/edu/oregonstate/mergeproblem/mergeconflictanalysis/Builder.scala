package edu.oregonstate.mergeproblem.mergeconflictanalysis

import java.io.{File, PrintStream, ByteArrayOutputStream}

import org.apache.maven.shared.invoker.{InvocationOutputHandler, DefaultInvoker, DefaultInvocationRequest}

import scala.collection.JavaConversions._

object Builder {

  val nullOutputHandler = new InvocationOutputHandler {
    override def consumeLine(line: String) = {}
  }

  def runMaven(projectPath: String, tasks: Seq[String]): Boolean =
  runMaven(projectPath, tasks, new PrintStream(new ByteArrayOutputStream()))

  def runMaven(projectPath: String, tasks: Seq[String], out: PrintStream): Boolean = {
    //makeMavenHappy(projectPath)
    val request = new DefaultInvocationRequest
    request.setPomFile(new File(projectPath + "/pom.xml"))
    request.setGoals(seqAsJavaList(tasks))
    request.setInteractive(false)
    val invoker = new DefaultInvoker
    invoker.setErrorHandler(nullOutputHandler)
    invoker.setOutputHandler(nullOutputHandler)
    val result = invoker.execute(request)
    if (result.getExitCode != 0)
      return false
    return true
  }

  def build(projectPath: String): Boolean = {
    return runMaven(projectPath, Seq("clean", "compile"))
  }

  def test(projectPath: String): Boolean =
    runMaven(projectPath, Seq("clean", "test"))

  def test(projectPath: String, out: PrintStream): Boolean =
    runMaven(projectPath, Seq("clean", "test"), out)

  def clean(projectPath: String): Boolean = {
    runMaven(projectPath, Seq("clean"))
  }

  private def makeMavenHappy(projectPath: String) =
    System.setProperty("maven.multiModuleProjectDirectory", projectPath)
}
