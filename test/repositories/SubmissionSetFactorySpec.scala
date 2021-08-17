/*
 * Copyright 2021 HM Revenue & Customs
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

package repositories

import base.SpecBase
import generators.ModelGenerators
import models.Status._
import models.TaskStatus.TaskStatus
import models.{RegistrationSubmission, TaskStatus, UserAnswers, YearReturnType}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsNull, Json}
import play.twirl.api.Html
import services.{TaxLiabilityService, TrustsStoreService}
import uk.gov.hmrc.http.HeaderCarrier
import utils.CheckYourAnswersHelper
import viewmodels.{AnswerRow, AnswerSection}

import scala.collection.immutable.Nil
import scala.concurrent.Future

class SubmissionSetFactorySpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  val mockCheckYourAnswersHelper: CheckYourAnswersHelper = mock[CheckYourAnswersHelper]
  val mockTaxLiabilityService: TaxLiabilityService = mock[TaxLiabilityService]
  val mockTrustsStoreService: TrustsStoreService = mock[TrustsStoreService]
  val factory = new SubmissionSetFactory(mockCheckYourAnswersHelper, mockTaxLiabilityService, mockTrustsStoreService)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "Submission set factory" must {

    "reset answer sections and statuses" in {
      val userAnswers: UserAnswers = emptyUserAnswers

      factory.reset(userAnswers) mustBe
        RegistrationSubmission.DataSet(
          Json.toJson(userAnswers),
          Some(InProgress),
          List(RegistrationSubmission.MappedPiece("yearsReturns", JsNull)),
          Nil
        )
    }

    "return no answer sections if not completed" in {

      forAll(arbitrary[TaskStatus].suchThat(_ != TaskStatus.Completed)) { status =>
        when(mockTrustsStoreService.getTaskStatus(any())(any(), any())).thenReturn(Future.successful(status))

        val userAnswers = emptyUserAnswers

        whenReady(factory.createFrom(userAnswers)) {
          _ mustBe RegistrationSubmission.DataSet(
            data = Json.toJson(userAnswers),
            status = Some(InProgress),
            registrationPieces = Nil,
            answerSections = Nil
          )
        }
      }
    }

    "return completed answer sections" when {

      val fakeAnswerSection: AnswerSection = AnswerSection(
        headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
        rows = List(AnswerRow("cyMinusOne.liability", Html("Yes"), None, Seq("6 April 2019", "5 April 2020"), canEdit = true)),
        sectionKey = None,
        headingArgs = Seq("6 April 2019", "5 April 2020")
      )

      val fakeRegistrationSubmissionAnswerSection = RegistrationSubmission.AnswerSection(
        headingKey = Some("taxLiabilityBetweenYears.checkYourAnswerSectionHeading"),
        rows = List(RegistrationSubmission.AnswerRow("cyMinusOne.liability", "Yes", Seq("6 April 2019", "5 April 2020"))),
        sectionKey = None,
        headingArgs = Seq("6 April 2019", "5 April 2020")
      )

      "task is completed" when {

        "no tax years to send down" in {

          when(mockTrustsStoreService.getTaskStatus(any())(any(), any())).thenReturn(Future.successful(TaskStatus.Completed))
          when(mockCheckYourAnswersHelper.apply(any())(any())).thenReturn(Seq(fakeAnswerSection))
          when(mockTaxLiabilityService.evaluateTaxYears(any())).thenReturn(Nil)

          val userAnswers = emptyUserAnswers

          whenReady(factory.createFrom(userAnswers)) {
            _ mustBe RegistrationSubmission.DataSet(
              data = Json.toJson(userAnswers),
              status = Some(Completed),
              registrationPieces = Nil,
              answerSections = List(fakeRegistrationSubmissionAnswerSection)
            )
          }
        }

        "tax years to send down" in {

          val fakeYearsReturns = List(YearReturnType("20", taxConsequence = true))

          when(mockTrustsStoreService.getTaskStatus(any())(any(), any())).thenReturn(Future.successful(TaskStatus.Completed))
          when(mockCheckYourAnswersHelper.apply(any())(any())).thenReturn(Seq(fakeAnswerSection))
          when(mockTaxLiabilityService.evaluateTaxYears(any())).thenReturn(fakeYearsReturns)

          val userAnswers = emptyUserAnswers

          whenReady(factory.createFrom(userAnswers)) {
            _ mustBe RegistrationSubmission.DataSet(
              data = Json.toJson(userAnswers),
              status = Some(Completed),
              registrationPieces = List(RegistrationSubmission.MappedPiece("yearsReturns", Json.obj("returns" -> Json.toJson(fakeYearsReturns)))),
              answerSections = List(fakeRegistrationSubmissionAnswerSection)
            )
          }
        }
      }
    }
  }

}
