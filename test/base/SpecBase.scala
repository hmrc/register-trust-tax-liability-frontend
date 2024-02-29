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

package base

import config.FrontendAppConfig
import controllers.actions._
import models.UserAnswers
import navigation.FakeNavigator
import org.scalatest.TryValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, BodyParsers, Call}
import play.api.test.FakeRequest
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}

import scala.concurrent.ExecutionContext

trait SpecBase extends PlaySpec with GuiceOneAppPerSuite with Mocked with TryValues with ScalaFutures with IntegrationPatience {

  val draftId: String = "draftId"
  val userInternalId = "id"

  val fakeNavigator = new FakeNavigator()

  def emptyUserAnswers: UserAnswers = UserAnswers(draftId, Json.obj(), userInternalId)

  def onwardRoute: Call = Call("GET", "/foo")

  def injector: Injector = app.injector

  def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  def trustsAuth: TrustsAuthorisedFunctions = injector.instanceOf[TrustsAuthorisedFunctions]

  def injectedParsers: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]

  implicit def executionContext: ExecutionContext = injector.instanceOf[ExecutionContext]

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  private def fakeDraftIdAction(userAnswers: Option[UserAnswers]): FakeDraftIdRetrievalActionProvider =
    new FakeDraftIdRetrievalActionProvider(userAnswers)

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None,
                                   affinityGroup: AffinityGroup = AffinityGroup.Organisation,
                                   enrolments: Enrolments = Enrolments(Set.empty[Enrolment])
                                  ): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].toInstance(
          new FakeIdentifierAction(affinityGroup, frontendAppConfig)(injectedParsers, trustsAuth, enrolments)
        ),
        bind[DraftIdRetrievalActionProvider].toInstance(fakeDraftIdAction(userAnswers)),
        bind[RegistrationsRepository].toInstance(registrationsRepository)
      )

}
