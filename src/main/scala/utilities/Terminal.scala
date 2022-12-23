package utilities

import utilities.Terminal.{CommandOutput, ServiceDiff}

import java.io.File
import scala.sys.process._
import scala.util.{Failure, Success, Try}

trait Terminal {

  def runCommand(command: String): CommandOutput = {
    val stdout = new StringBuilder
    val stderr = new StringBuilder

    val status = command ! ProcessLogger(stdout append _, stderr append _)

    CommandOutput(status, stdout, stderr)
  }

  def runCommandInFolder(command: String, folder: String): CommandOutput = {
    val stdout = new StringBuilder
    val stderr = new StringBuilder

    val status = Process(s"$command", new File(folder)).!(ProcessLogger(stdout append _, stderr append _))

    CommandOutput(status, stdout, stderr)
  }

  def gitFetch(serviceNames: List[String]): List[CommandOutput] = {
    lazy val stdout = new StringBuilder
    lazy val stderr = new StringBuilder

    serviceNames.map { name =>

      Try {
        runCommandInFolder("git fetch", name)
      } match {
        case Success(value) =>
          value
        case Failure(err) =>
          println(s"A error occurred while fetching service $name")
          CommandOutput(1, stdout, stderr.append(err))
      }
    }
  }

  def gitDiff(serviceNames: List[String],
              serviceNameOne: String,
              serviceNameTwo: String): List[ServiceDiff] = {
    lazy val command = s"git diff $serviceNameOne $serviceNameTwo"

    serviceNames.map { serviceName =>
      val output = runCommandInFolder(command, serviceName)

      if (output.stdout.toString().isBlank) {
        ServiceDiff(serviceName, s"$serviceNameOne -> $serviceNameTwo", false)
      } else {
        ServiceDiff(serviceName, s"$serviceNameOne -> $serviceNameTwo", true)
      }
    }
  }

  def cloneGitRepo(httpRepoLink: List[String],
                   githubUser: String,
                   githubToken: String): List[Unit] = {

    httpRepoLink.map { link =>
      runCommand(s"git clone https://$githubUser:$githubToken@github.com/fullfacing/$link.git")
    }
  }
}

object Terminal {
  /**
   * Command line commands output
   *
   * @param status 0 is successfully executed and 1 is unsuccessfully executed
   * @param stdout contains successfully executed standard output
   * @param stderr contains unsuccessfully executed error output
   */
  final case class CommandOutput(status: Int,
                                 stdout: StringBuilder,
                                 stderr: StringBuilder)


  /**
   * Command line commands output
   *
   * @param serviceName  Name of Service
   * @param diffType     branches being diffed
   * @param requireMerge If diffs are the same then False else true
   */
  final case class ServiceDiff(serviceName: String,
                               diffType: String,
                               requireMerge: Boolean)
}
