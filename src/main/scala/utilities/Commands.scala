package utilities

trait Commands extends Terminal {

  def getGithubToken(): String = runCommand("git config --get github.token").stdout.toString()

  def getGithubUser(): String = runCommand("git config --get github.user").stdout.toString()

  def getEmail() = runCommand("git config --get user.email").stdout.toString()

  def getSigningKey(): String = runCommand("git config --get user.signingkey").stdout.toString()

}
