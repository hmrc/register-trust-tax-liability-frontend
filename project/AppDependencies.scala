import sbt.*

object AppDependencies {

  val bootstrapVersion = "8.6.0"

  private val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"         %% "play-frontend-hmrc-play-30"             % "12.0.0",
    "uk.gov.hmrc"         %% "play-conditional-form-mapping-play-30"  % "3.3.0",
    "uk.gov.hmrc"         %% "domain-play-30"                         % "11.0.0",
    "uk.gov.hmrc"         %% "bootstrap-frontend-play-30"             % bootstrapVersion,
    "uk.gov.hmrc"         %% "tax-year"                               % "6.0.0"
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-30"   % bootstrapVersion,
    "org.scalatest"               %% "scalatest"                % "3.2.19",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.jsoup"                   %  "jsoup"                    % "1.19.1",
    "org.playframework"           %% "play-test"                % "3.0.7",
    "org.scalacheck"              %% "scalacheck"               % "1.18.1",
    "io.github.wolfendale"        %% "scalacheck-gen-regexp"    % "1.1.0",
    "org.wiremock"                % "wiremock-standalone"       % "3.12.1",
    "com.vladsch.flexmark"         % "flexmark-all"               % "0.64.8"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
