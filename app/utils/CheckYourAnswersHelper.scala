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
import models.{CYMinusNTaxYears, TaxYearRange, UserAnswers}
import pages._
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                       taxYearRange: TaxYearRange) {

  def apply(userAnswers: UserAnswers)(implicit messages: Messages): Seq[AnswerSection] = {

    val bound = answerRowConverter.bind(userAnswers)

    CYMinusNTaxYears.taxYears.foldLeft[Seq[AnswerSection]](Nil)((acc, taxYear) => {

      val args = taxYearRange.toLabelArgs(taxYear)

      val answerRows: Seq[AnswerRow] = Seq(
        bound.yesNoQuestion(
          query = taxYear.page,
          labelKey = s"${taxYear.messagePrefix}.liability",
          changeUrl = Some(taxYear.changeUrl(userAnswers.draftId)),
          arguments = args
        ),
        bound.yesNoQuestion(
          query = DidDeclareTaxToHMRCYesNoPage(taxYear),
          labelKey = "didDeclareToHMRC",
          changeUrl = Some(controllers.routes.DidDeclareTaxToHMRCController.onPageLoad(userAnswers.draftId, taxYear).url),
          arguments = args
        )
      ).flatten

      answerRows match {
        case Nil => acc
        case _ => acc :+ AnswerSection(
          headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
          rows = answerRows,
          headingArgs = args
        )
      }
    })
  }

}
