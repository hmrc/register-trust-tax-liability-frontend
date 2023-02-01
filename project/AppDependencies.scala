import play.core.PlayVersion
import sbt._

object AppDependencies {

  private val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"         %% "play-frontend-hmrc"             % "6.2.0-play-28",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping"  % "1.12.0-play-28",
    "uk.gov.hmrc"         %% "domain"                         % "8.1.0-play-28",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-28"     % "7.13.0",
    "uk.gov.hmrc"         %% "tax-year"                       % "3.0.0"
  )

  private val test: Seq[ModuleID] = Seq(
    "org.scalatest"               %% "scalatest"                % "3.2.15",
    "org.scalatestplus.play"      %% "scalatestplus-play"       % "5.1.0",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.jsoup"                   %  "jsoup"                    % "1.15.3",
    "com.typesafe.play"           %% "play-test"                % PlayVersion.current,
    "org.mockito"                 %% "mockito-scala-scalatest"  % "1.17.12",
    "org.scalacheck"              %% "scalacheck"               % "1.17.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.github.tomakehurst"      % "wiremock-standalone"       % "2.27.2",
    "com.vladsch.flexmark"        % "flexmark-all"              % "0.62.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
