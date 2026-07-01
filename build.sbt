import play.sbt.routes.RoutesKeys
import sbt.Def
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

ThisBuild / scalaVersion := "2.13.18"
ThisBuild / majorVersion := 0

lazy val microservice = Project("register-trust-tax-liability-frontend", file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(CodeCoverageSettings())
  .settings(
    RoutesKeys.routesImport ++= Seq(
      "models._",
      "models.CYMinusNTaxYears._"
    ),
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "models.Mode",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 8838,
    libraryDependencies ++= AppDependencies()
  )
  .settings(inConfig(Test)(testSettings))

lazy val testSettings: Seq[Def.Setting[?]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
