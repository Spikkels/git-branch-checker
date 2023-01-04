package utilities

import zio.ZIO
import java.io.File
import scala.sys.process._
import scala.util.{Failure, Success, Try}
import utilities.Terminal.{CommandOutput, ServiceDiff}


trait Terminal {

  /**
   * Terminal command to be run in only in local directory
   *
   * @param command terminal command
   */
  def runCommand(command: String): ZIO[Any, Any, CommandOutput] = {
    val stdout = new StringBuilder
    val stderr = new StringBuilder

    Try {
      command ! ProcessLogger(stdout append _, stderr append _)
    } match {
      case Success(status)    => ZIO.succeed(CommandOutput(status, stdout.toString(), stderr.toString()))
      case Failure(exception) => ZIO.succeed(CommandOutput(1, stdout.toString(), stderr.append(exception.getMessage).toString()))
    }
  }

  /**
   * Terminal command to be run in only in local directory
   *
   * @param command terminal command
   */
  def runCommandStopOnFail(command: String): ZIO[Any, String, CommandOutput] = {
    val stdout = new StringBuilder
    val stderr = new StringBuilder

    Try {
      command ! ProcessLogger(stdout append _, stderr append _)
    } match {
      case Success(status) if (status == 0) =>
        ZIO.succeed(CommandOutput(status, stdout.toString(), stderr.toString()))
      case Success(status) if (status == 1) =>
        ZIO.fail(s"A error when running command: $command")
      case Failure(exception) =>
        ZIO.fail(s"A error when running command: $command with exception: $exception")
    }
  }

  /**
   * Terminal command to be run in only in local directory
   *
   * @param command terminal command
   * @param folder folder name
   */
  def runCommandInFolder(command: String, folder: String): ZIO[Any, Any, CommandOutput]  = {
    val stdout = new StringBuilder
    val stderr = new StringBuilder

    Try {
      Process(s"$command", new File(folder)).!(ProcessLogger(stdout append _, stderr append _))
    } match {
      case Success(status)    => ZIO.succeed(CommandOutput(status, stdout.toString(), stderr.toString()))
      case Failure(exception) => ZIO.succeed(CommandOutput(1, stdout.toString(), stderr.append(exception.getMessage).toString()))
    }
  }


  def gitFetch(serviceNames: List[String]): ZIO[Any, Any, List[CommandOutput]] = {
    ZIO.foreachPar(serviceNames)(runCommandInFolder("git fetch", _))
  }


  def gitDiff(serviceNames: List[String],
              serviceNameOne: String,
              serviceNameTwo: String): ZIO[Any, Any, List[ServiceDiff]] = {

    def mapServiceDiff(serviceName: String, output: CommandOutput): ServiceDiff = {
      if (output.stdout.isBlank) {
        ServiceDiff(serviceName, s"$serviceNameOne -> $serviceNameTwo", false)
      } else {
        ServiceDiff(serviceName, s"$serviceNameOne -> $serviceNameTwo", true)
      }
    }

    lazy val command = s"git diff $serviceNameOne $serviceNameTwo"

    ZIO.foreachPar(serviceNames) { serviceName =>
      for {
        output       <- runCommandInFolder(command, serviceName)
        serviceDiffs  = mapServiceDiff(serviceName, output)
      } yield serviceDiffs
    }
  }

  def cloneGitRepo(httpRepoLink: List[String],
                   OrganizationName: String,
                   githubUser: String,
                   githubToken: String): ZIO[Any, Any, List[CommandOutput]] = {
    ZIO.foreachPar(httpRepoLink) { case link =>
      runCommand(s"git clone https://$githubUser:$githubToken@github.com/$OrganizationName/$link.git")
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
                                 stdout: String,
                                 stderr: String)


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
