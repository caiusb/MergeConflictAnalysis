package edu.oregonstate.mergeproblem.mergeconflictanalysis

import java.io.{PrintStream, ByteArrayOutputStream}

import org.apache.maven.cli.MavenCli

object Builder {

  def build(projectPath: String): Boolean = {
    return runMaven(projectPath, Array("clean", "compile"))
  }

  def runMaven(projectPath: String, tasks: Array[String]): Boolean = {
    makeMavenHappy(projectPath)
    val output = new PrintStream(new ByteArrayOutputStream())
    val result = new MavenCli().doMain(tasks, projectPath, output, output)
    if (result != 0)
      return false

    return true
  }

  def clean(projectPath: String): Boolean = {
    runMaven(projectPath, Array("clean"))
  }

  private def makeMavenHappy(projectPath: String) =
    System.setProperty("maven.multiModuleProjectDirectory", projectPath)
}
