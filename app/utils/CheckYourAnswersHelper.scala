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

import com.google.inject.Inject
import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears, NormalMode, CYMinusNTaxYears, TaxYearRange, UserAnswers}
import pages._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(answerRowConverter: AnswerRowConverter, taxYearRange: TaxYearRange) {

  def cyMinusTaxYearAnswers(userAnswers: UserAnswers, taxYear: CYMinusNTaxYears)
                           (implicit messages: Messages): Option[AnswerSection] = {
    val bound = answerRowConverter.bind(userAnswers)

    val toRange = taxYearRange.toRange(taxYear)
    val page = yesNoPageForTaxYear(taxYear)
    val changeRoute = changeRouteForTaxYear(taxYear, userAnswers.draftId)

    val answerRows : Seq[AnswerRow] = Seq(
      bound.yesNoQuestion(
        page,
        s"${taxYear.messagePrefix}.liability",
        Some(changeRoute),
        toRange
      ),
      bound.yesNoQuestion(
        DidDeclareTaxToHMRCYesNoPage(taxYear),
        "didDeclareToHMRC",
        Some(controllers.routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, userAnswers.draftId, taxYear).url),
        toRange
      )
    ).flatten

    answerRows match {
      case Nil => None
      case _ =>
        Some(
          AnswerSection(
            Some(messages("taxLiabilityBetweenYears.checkYourAnswerSectionHeading", toRange)),
            answerRows
          )
        )
    }
  }

  private def changeRouteForTaxYear(taxYear: CYMinusNTaxYears, draftId: String): String = taxYear match {
    case CYMinus4TaxYears => controllers.routes.CYMinusFourLiabilityController.onPageLoad(NormalMode, draftId).url
    case CYMinus3TaxYears => controllers.routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode, draftId).url
    case CYMinus2TaxYears => controllers.routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode, draftId).url
    case CYMinus1TaxYear => controllers.routes.CYMinusOneLiabilityController.onPageLoad(NormalMode, draftId).url
  }

  private def yesNoPageForTaxYear(taxYear: CYMinusNTaxYears) : QuestionPage[Boolean] = taxYear match {
    case CYMinus4TaxYears => CYMinusFourYesNoPage
    case CYMinus3TaxYears => CYMinusThreeYesNoPage
    case CYMinus2TaxYears => CYMinusTwoYesNoPage
    case CYMinus1TaxYear => CYMinusOneYesNoPage
  }

}
