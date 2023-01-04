package applicattion

import utilities.Terminal.ServiceDiff
import utilities.{Commands, Terminal}
import zio.ZIO

object Checker extends App
  with Terminal
  with Commands {

  def runChecker(services: List[String], OrganizationName: String): ZIO[Any, Any, List[ServiceDiff]] = {

    for {
      githubToken  <- getGithubToken()
      githubUser   <- getGithubUser()
      _             = println("Cloning Github Repos")
      _            <- cloneGitRepo(services, OrganizationName, githubUser.stdout, githubToken.stdout)
      _             = println("Fetching Latest github updates")
      _            <- gitFetch(services)
      _             = println("Diffing Services")
      devStaDiff   <- gitDiff(services, "origin/v3-develop", "origin/v3-staging   ")
      staUatDiff   <- gitDiff(services, "origin/v3-staging", "origin/v3-uat       ")
      uatProDiff   <- gitDiff(services, "origin/v3-uat    ", "origin/v3-production")
    } yield devStaDiff ++ staUatDiff ++ uatProDiff
  }

  def diffPrinter(serviceDiffs: List[ServiceDiff]): Iterable[List[Unit]] = {
    serviceDiffs.groupBy(serviceDiff => serviceDiff.serviceName).map { case (serviceName, diffs) =>
      println("")
      println(serviceName)
      diffs.map { diff =>
        println(s"${diff.diffType}\tRequire merging: ${diff.requireMerge}")
      }
    }
  }
}



