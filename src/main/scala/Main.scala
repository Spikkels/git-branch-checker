import applicattion.Checker

object Main
  extends App {

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

  Checker.diffPrinter(Checker.runChecker(services))

}
















