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

package repositories

import javax.inject.Inject
import models._
import pages.TaxLiabilityTaskStatus
import play.api.i18n.Messages
import play.api.libs.json.{JsNull, Json}
import services.TaxLiabilityService
import utils.{AnswerRowConverter, CheckYourAnswersHelper}
import viewmodels.{AnswerRow, AnswerSection}

class SubmissionSetFactory @Inject()(checkYourAnswersHelper: CheckYourAnswersHelper,
                                    taxLiabilityService: TaxLiabilityService) {

  def reset(userAnswers: UserAnswers): RegistrationSubmission.DataSet = {
    RegistrationSubmission.DataSet(
      data = Json.toJson(userAnswers),
      status = Some(Status.InProgress),
      registrationPieces = List(RegistrationSubmission.MappedPiece("yearsReturns", JsNull)),
      answerSections = Nil
    )
  }

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {
    val status = userAnswers.get(TaxLiabilityTaskStatus).orElse(Some(Status.InProgress))

    RegistrationSubmission.DataSet(
      Json.toJson(userAnswers),
      status,
      mappedDataIfCompleted(userAnswers, status),
      answerSectionsIfCompleted(userAnswers, status)
    )
  }

  private def mappedDataIfCompleted(userAnswers: UserAnswers, status: Option[Status]) = {
    if (status.contains(Status.Completed)) {
      taxLiabilityService.evaluateTaxYears(userAnswers) match {
        case Nil => List.empty
        case yearsReturns =>
          List(RegistrationSubmission.MappedPiece("yearsReturns", Json.toJson(yearsReturns)))
      }
    } else {
      List.empty
    }
  }

  def answerSectionsIfCompleted(userAnswers: UserAnswers, status: Option[Status])
                               (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

    if (status.contains(Status.Completed)) {

      val taxFor4Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(userAnswers, CYMinus4TaxYear)
      val taxFor3Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(userAnswers, CYMinus3TaxYear)
      val taxFor2Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(userAnswers, CYMinus2TaxYear)
      val taxFor1Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(userAnswers, CYMinus1TaxYear)

      val sections = List(
        taxFor4Years,
        taxFor3Years,
        taxFor2Years,
        taxFor1Years
      ).flatten

      sections.map(convertForSubmission)

    } else {
      List.empty
    }
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(section.headingKey, section.rows.map(convertForSubmission), section.sectionKey)
  }
}
