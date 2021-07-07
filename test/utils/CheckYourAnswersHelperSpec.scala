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

package utils

import base.SpecBase
import controllers.routes
import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears, NormalMode, TaxYearRange}
import pages._
import play.twirl.api.Html
import uk.gov.hmrc.play.language.LanguageUtils
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelperSpec extends SpecBase {

  val languageUtils: LanguageUtils = injector.instanceOf[LanguageUtils]
  val taxYearRange: TaxYearRange = new TaxYearRange(languageUtils)

  "Check your answers helper" when {

    "CY-4" must {

      "render answers for CY-4 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus4TaxYears
        val taxYearRangeDisplay = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusFourYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value


        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay)),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusFour.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusFourLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

    "CY-3" must {

      "render answers for CY-3 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus3TaxYears
        val taxYearRangeDisplay = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusThreeYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value


        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay)),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusThree.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

    "CY-2" must {

      "render answers for CY-2 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus2TaxYears

        val taxYearRangeDisplay = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusTwoYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay)),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusTwo.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

    "CY-1" must {

      "render answers for CY-1 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus1TaxYear
        val taxYearRangeDisplay = taxYearRange.toRange(taxYear)

        val userAnswers = emptyUserAnswers
          .set(CYMinusOneYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", taxYearRangeDisplay)),
          rows = Seq(
            AnswerRow(
              label = messages("cyMinusOne.liability.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusOneLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = messages("didDeclareToHMRC.checkYourAnswersLabel", taxYearRangeDisplay),
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

  }

}
