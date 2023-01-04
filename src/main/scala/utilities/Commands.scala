package utilities

import utilities.Terminal.CommandOutput
import zio.ZIO

trait Commands extends Terminal {

  def getGithubToken(): ZIO[Any, Any, CommandOutput]  = runCommand("git config --get github.token")

  def getGithubUser(): ZIO[Any, Any, CommandOutput]  = runCommand("git config --get github.user")

  def getEmail(): ZIO[Any, Any, CommandOutput]  = runCommand("git config --get user.email")

  def getSigningKey(): ZIO[Any, Any, CommandOutput]  = runCommand("git config --get user.signingkey")

}
