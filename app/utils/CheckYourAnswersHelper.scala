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

package utils

import com.google.inject.Inject
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, NormalMode, TaxYear, TaxYearRange, UserAnswers}
import pages._
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def earlierThan4YearsAnswers(userAnswers: UserAnswers)(implicit messages: Messages) : Option[AnswerSection] = {
    val bound = answerRowConverter.bind(userAnswers)

    val date = TaxYearRange(CYMinus4TaxYear).yearAtStart

    val answerRows : Seq[AnswerRow] = Seq(
      bound.yesNoQuestion(
        CYMinusFourEarlierYearsYesNoPage,
        "earlierYearsLiability",
        Some(controllers.routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode).url),
        date
      )
    ).flatten

    answerRows match {
      case Nil => None
      case _ =>
        Some(
          AnswerSection(
            Some(messages("earlierYearsLiability.checkYourAnswerSectionHeading", date)),
            answerRows
          )
        )
    }
  }

  def earlierThan3YearsAnswers(userAnswers: UserAnswers)(implicit messages: Messages) : Option[AnswerSection] = {
    val bound = answerRowConverter.bind(userAnswers)

    val date = TaxYearRange(CYMinus3TaxYear).yearAtStart

    val answerRows : Seq[AnswerRow] = Seq(
      bound.yesNoQuestion(
        CYMinusThreeEarlierYearsYesNoPage,
        "earlierYearsLiability",
        Some(controllers.routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(NormalMode).url),
        date
      )
    ).flatten

    answerRows match {
      case Nil => None
      case _ =>
        Some(
          AnswerSection(
            Some(messages("earlierYearsLiability.checkYourAnswerSectionHeading", date)),
            answerRows
          )
        )
    }
  }

  def cyMinusTaxYearAnswers(userAnswers: UserAnswers, taxYear: TaxYear)
                           (implicit messages: Messages): Option[AnswerSection] = {
    val bound = answerRowConverter.bind(userAnswers)

    val toRange = TaxYearRange(taxYear).toRange
    val page = yesNoPageForTaxYear(taxYear)
    val changeRoute = changeRouteForTaxYear(taxYear)

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
        Some(controllers.routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, taxYear).url),
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

  private def changeRouteForTaxYear(taxYear: TaxYear): String = taxYear match {
    case CYMinus4TaxYear => controllers.routes.CYMinusFourLiabilityController.onPageLoad(NormalMode).url
    case CYMinus3TaxYear => controllers.routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode).url
    case CYMinus2TaxYear => controllers.routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode).url
    case CYMinus1TaxYear => controllers.routes.CYMinusOneLiabilityController.onPageLoad(NormalMode).url
  }

  private def yesNoPageForTaxYear(taxYear: TaxYear) : QuestionPage[Boolean] = taxYear match {
    case CYMinus4TaxYear => CYMinusFourYesNoPage
    case CYMinus3TaxYear => CYMinusThreeYesNoPage
    case CYMinus2TaxYear => CYMinusTwoYesNoPage
    case CYMinus1TaxYear => CYMinusOneYesNoPage
  }

}
