/*
 * Copyright 2022 HM Revenue & Customs
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
import models.{RegistrationSubmission, UserAnswers, YearReturnType}
import org.mockito.ArgumentMatchers.any
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsNull, Json}
import play.twirl.api.Html
import services.TaxLiabilityService
import utils.CheckYourAnswersHelper
import viewmodels.{AnswerRow, AnswerSection}

import scala.collection.immutable.Nil

class SubmissionSetFactorySpec extends SpecBase with ScalaCheckPropertyChecks with ModelGenerators {

  private val mockCheckYourAnswersHelper: CheckYourAnswersHelper = mock[CheckYourAnswersHelper]
  private val mockTaxLiabilityService: TaxLiabilityService = mock[TaxLiabilityService]
  private val factory = new SubmissionSetFactory(mockCheckYourAnswersHelper, mockTaxLiabilityService)

  "Submission set factory" must {

    "reset answer sections and statuses" in {
      val userAnswers: UserAnswers = emptyUserAnswers

      factory.reset(userAnswers) mustBe
        RegistrationSubmission.DataSet(
          Json.toJson(userAnswers),
          List(RegistrationSubmission.MappedPiece("yearsReturns", JsNull)),
          Nil
        )
    }

    "return no answer sections if not completed" in {
      val userAnswers = emptyUserAnswers

      when(mockTaxLiabilityService.evaluateTaxYears(any())).thenReturn(Nil)
      when(mockCheckYourAnswersHelper.apply(any())(any())).thenReturn(Nil)

      factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
          data = Json.toJson(userAnswers),
          registrationPieces = Nil,
          answerSections = Nil
        )
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

          when(mockCheckYourAnswersHelper.apply(any())(any())).thenReturn(Seq(fakeAnswerSection))
          when(mockTaxLiabilityService.evaluateTaxYears(any())).thenReturn(Nil)

          val userAnswers = emptyUserAnswers

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
              data = Json.toJson(userAnswers),
              registrationPieces = Nil,
              answerSections = List(fakeRegistrationSubmissionAnswerSection)
            )
        }

        "tax years to send down" in {

          val fakeYearsReturns = List(YearReturnType("20", taxConsequence = true))

          when(mockCheckYourAnswersHelper.apply(any())(any())).thenReturn(Seq(fakeAnswerSection))
          when(mockTaxLiabilityService.evaluateTaxYears(any())).thenReturn(fakeYearsReturns)

          val userAnswers = emptyUserAnswers

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
              data = Json.toJson(userAnswers),
              registrationPieces = List(RegistrationSubmission.MappedPiece("yearsReturns", Json.obj("returns" -> Json.toJson(fakeYearsReturns)))),
              answerSections = List(fakeRegistrationSubmissionAnswerSection)
            )
        }
      }
    }
  }

}
