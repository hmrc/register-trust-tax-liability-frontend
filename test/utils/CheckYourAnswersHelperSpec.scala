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

package utils

import base.SpecBase
import controllers.routes
import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears, TaxYearRange}
import pages._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelperSpec extends SpecBase {

  val checkYourAnswersHelper: CheckYourAnswersHelper = injector.instanceOf[CheckYourAnswersHelper]
  val taxYearRange: TaxYearRange = injector.instanceOf[TaxYearRange]

  "CheckYourAnswersHelper" must {

    "render answer rows" in {

      val userAnswers = emptyUserAnswers
        .set(CYMinusFourYesNoPage, true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears), true).success.value

        .set(CYMinusThreeYesNoPage, true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYears), true).success.value

        .set(CYMinusTwoYesNoPage, true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears), false).success.value

        .set(CYMinusOneYesNoPage, true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

      val result = checkYourAnswersHelper.apply(userAnswers)

      result mustBe Seq(
        AnswerSection(
          headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
          rows = Seq(
            AnswerRow(
              label = "cyMinusFour.liability.checkYourAnswersLabel",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusFourLiabilityController.onPageLoad(draftId).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus4TaxYears),
              canEdit = true
            ),
            AnswerRow(
              label = "didDeclareToHMRC.checkYourAnswersLabel",
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus4TaxYears).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus4TaxYears),
              canEdit = true
            )
          ),
          headingArgs = taxYearRange.toLabelArgs(CYMinus4TaxYears)
        ),
        AnswerSection(
          headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
          rows = Seq(
            AnswerRow(
              label = "cyMinusThree.liability.checkYourAnswersLabel",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusThreeLiabilityController.onPageLoad(draftId).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus3TaxYears),
              canEdit = true
            ),
            AnswerRow(
              label = "didDeclareToHMRC.checkYourAnswersLabel",
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus3TaxYears).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus3TaxYears),
              canEdit = true
            )
          ),
          headingArgs = taxYearRange.toLabelArgs(CYMinus3TaxYears)
        ),
        AnswerSection(
          headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
          rows = Seq(
            AnswerRow(
              label = "cyMinusTwo.liability.checkYourAnswersLabel",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusTwoLiabilityController.onPageLoad(draftId).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus2TaxYears),
              canEdit = true
            ),
            AnswerRow(
              label = "didDeclareToHMRC.checkYourAnswersLabel",
              answer = Html("No"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus2TaxYears).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus2TaxYears),
              canEdit = true
            )
          ),
          headingArgs = taxYearRange.toLabelArgs(CYMinus2TaxYears)
        ),
        AnswerSection(
          headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
          rows = Seq(
            AnswerRow(
              label = "cyMinusOne.liability.checkYourAnswersLabel",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusOneLiabilityController.onPageLoad(draftId).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus1TaxYear),
              canEdit = true
            ),
            AnswerRow(
              label = "didDeclareToHMRC.checkYourAnswersLabel",
              answer = Html("No"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus1TaxYear).url),
              labelArgs = taxYearRange.toLabelArgs(CYMinus1TaxYear),
              canEdit = true
            )
          ),
          headingArgs = taxYearRange.toLabelArgs(CYMinus1TaxYear)
        )
      )
    }
  }

}
