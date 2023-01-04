import applicattion.Checker
import utilities.Terminal
import zio._
import zio.Console._


object MyApp extends ZIOAppDefault with Terminal {
  def run = myAppLogic


    val organizationName = "fullfacing"

    val services: List[String] = List(
      "fng-api-aggregation",
      "fng-api-production",
      "fng-api-report",
      "fng-api-hardware",
      "fng-api-email",
      "fng-api-auth",
      "fng-svc-sap",
      "fng-api-client"
    )

  val myAppLogic =
    for {
      diffs    <- Checker.runChecker(services, organizationName)
      _         = Checker.diffPrinter(diffs)

    } yield ()


}

















