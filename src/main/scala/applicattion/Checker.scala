package applicattion

import utilities.Terminal.ServiceDiff
import utilities.{Commands, Terminal}

object Checker extends App
  with Terminal
  with Commands {

  def runChecker(services: List[String], OrganizationName: String): List[ServiceDiff] = {
    val githubToken = getGithubToken()
    val githubUser = getGithubUser()

    println("Cloning Github Repos")
    cloneGitRepo(services, OrganizationName, githubUser, githubToken)

    println("Fetching Latest github updates")
    gitFetch(services)

    println("Diffing Services")
    val devStaDiff = gitDiff(services, "origin/v3-develop", "origin/v3-staging   ")
    val staUatDiff = gitDiff(services, "origin/v3-staging", "origin/v3-uat       ")
    val uatProDiff = gitDiff(services, "origin/v3-uat    ", "origin/v3-production")

    devStaDiff ++ staUatDiff ++ uatProDiff
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



