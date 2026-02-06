/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.mvc._
import play.api.mvc.Action
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, Enrolments}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import uk.gov.hmrc.auth.core.MissingBearerToken
import uk.gov.hmrc.auth.core.retrieve.Retrieval

import scala.concurrent.Future

class IdentifierActionSpec extends SpecBase {

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]

  private val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  private def identifierAction: IdentifierAction = {
    val parser: BodyParsers.Default = injector.instanceOf[BodyParsers.Default]
    val trustsAuth                  = new TrustsAuthorisedFunctions(mockAuthConnector, appConfig)
    new IdentifierAction(parser, trustsAuth, appConfig)
  }

  class Harness(identifierAction: IdentifierAction) {
    def onPageLoad(): Action[AnyContent] = identifierAction(_ => Results.Ok)
  }

  "IdentifierAction" must {

    "allow requests that are already IdentifierRequest to proceed" in {
      when(mockAuthConnector.authorise(any(), any[Retrieval[_]]())(any(), any()))
        .thenReturn(Future.failed(MissingBearerToken()))

      val idRequest = IdentifierRequest(
        fakeRequest,
        "id",
        AffinityGroup.Organisation,
        Enrolments(Set()),
        None
      )

      val controller = new Harness(identifierAction)
      val result     = controller.onPageLoad()(idRequest)

      status(result) mustBe SEE_OTHER
    }

    "redirect normal requests to login" in {
      when(mockAuthConnector.authorise(any(), any[Retrieval[_]]())(any(), any()))
        .thenReturn(Future.failed(MissingBearerToken()))

      val controller = new Harness(identifierAction)
      val result     = controller.onPageLoad()(fakeRequest)

      status(result)               mustBe SEE_OTHER
      redirectLocation(result).value must startWith(appConfig.loginUrl)
    }

    "composeAction should redirect unauthenticated requests to login" in {
      when(mockAuthConnector.authorise(any(), any())(any(), any()))
        .thenReturn(Future.failed(MissingBearerToken()))

      val action                              = identifierAction
      val actionBuilder: DefaultActionBuilder = injector.instanceOf[DefaultActionBuilder]
      val inner: Action[AnyContent]           = actionBuilder(_ => Results.Ok)
      val composed: Action[AnyContent]        = action.composeAction(inner)

      val result = composed(fakeRequest)

      status(result)               mustBe SEE_OTHER
      redirectLocation(result).value must startWith(appConfig.loginUrl)
    }
  }

}
