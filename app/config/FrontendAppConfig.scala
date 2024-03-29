/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.{Lang, Messages}
import play.api.mvc.Call
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject()(val configuration: Configuration,
                                  servicesConfig: ServicesConfig,
                                  contactFrontendConfig: ContactFrontendConfig) {

  val repositoryKey: String = "taxLiability"

  final val ENGLISH = "en"
  final val WELSH = "cy"

  val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl.get}/contact/beta-feedback?service=${contactFrontendConfig.serviceId.get}"

  lazy val authUrl: String = servicesConfig.baseUrl("auth")
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = configuration.get[String]("urls.logout")
  val appName: String = configuration.get[String]("appName")

  lazy val logoutAudit: Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.logout")

  lazy val countdownLength: Int = configuration.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = configuration.get[Int]("timeout.length")

  lazy val registrationStartUrl: String = configuration.get[String]("urls.registrationStart")

  lazy val trustsUrl: String = servicesConfig.baseUrl("trusts")
  lazy val createAgentServicesAccountUrl: String = configuration.get[String]("urls.createAgentServicesAccount")
  lazy val maintainATrustFrontendUrl: String = configuration.get[String]("urls.maintainATrust")
  lazy val trustsStoreUrl: String = servicesConfig.baseUrl("trusts-store")

  def registrationProgressUrl(draftId: String): String =
    configuration.get[String]("urls.registrationProgress").replace(":draftId", draftId)

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  def registerTrustAsTrusteeUrl: String = configuration.get[String]("urls.registerTrustAsTrustee")

  def helplineUrl(implicit messages: Messages): String = {
    val path = messages.lang.code match {
      case WELSH => "urls.welshHelpline"
      case _ => "urls.trustsHelpline"
    }
    configuration.get[String](path)
  }
}
