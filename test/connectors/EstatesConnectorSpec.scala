/*
 * Copyright 2020 HM Revenue & Customs
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

package connectors

import java.time.LocalDate

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{okJson, urlEqualTo, _}
import models.{YearReturnType, YearsReturns}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

import scala.concurrent.ExecutionContext

class EstatesConnectorSpec extends SpecBase
  with ScalaFutures
  with IntegrationPatience
  with WireMockHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  "estates connector" must {

    "return OK with the deceased date of death" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      val json = Json.parse(
        """
          |"2010-10-10"
          |""".stripMargin)

      server.stubFor(
        get(urlEqualTo("/estates/date-of-death"))
          .willReturn(okJson(json.toString))
      )

      val futureResult = connector.getDateOfDeath()

      whenReady(futureResult) {
        r =>
          r mustBe LocalDate.of(2010,10,10)
      }

      application.stop()
    }

    "save tax consequences" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      server.stubFor(
        post(urlEqualTo("/estates/tax-liability"))
          .withRequestBody(equalTo(
            Json.stringify(Json.parse("""
              |{
              | "returns": [
              |   {"taxReturnYear":"20", "taxConsequence": true}
              | ]
              |}
              |""".stripMargin))))
          .willReturn(ok())
      )

      val futureResult = connector.saveTaxConsequence(YearsReturns(List(YearReturnType("20", true))))

      whenReady(futureResult) {
        r =>
          r.status mustBe OK
      }

      application.stop()
    }

    "reset tax liability transforms" in {
      val application = applicationBuilder()
        .configure(
          Seq(
            "microservice.services.estates.port" -> server.port(),
            "auditing.enabled" -> false
          ): _*
        ).build()

      implicit def ec: ExecutionContext = application.injector.instanceOf[ExecutionContext]

      val connector = application.injector.instanceOf[EstatesConnector]

      server.stubFor(
        post(urlEqualTo("/estates/reset-tax-liability"))
          .willReturn(ok())
      )

      val futureResult = connector.resetTaxLiability()

      whenReady(futureResult) {
        r =>
          r.status mustBe OK
      }

      application.stop()
    }

  }

}
