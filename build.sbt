ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "dev.zio" %% "zio" % "2.0.5"

lazy val root = (project in file("."))
  .settings(
    name := "git-branch-checker"
  )
