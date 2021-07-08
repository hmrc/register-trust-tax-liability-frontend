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

import models._
import pages.TaxLiabilityTaskStatus
import play.api.i18n.Messages
import play.api.libs.json.{JsNull, Json}
import services.TaxLiabilityService
import utils.CheckYourAnswersHelper
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

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

  private def mappedDataIfCompleted(userAnswers: UserAnswers, status: Option[Status]): List[RegistrationSubmission.MappedPiece] = {
    if (status.contains(Status.Completed)) {
      taxLiabilityService.evaluateTaxYears(userAnswers) match {
        case Nil => List.empty
        case yearsReturns =>
          val payload = Json.obj("returns" -> Json.toJson(yearsReturns))
          List(RegistrationSubmission.MappedPiece("yearsReturns", payload))
      }
    } else {
      List.empty
    }
  }

  def answerSectionsIfCompleted(userAnswers: UserAnswers, status: Option[Status])
                               (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

    if (status.contains(Status.Completed)) {
      checkYourAnswersHelper(userAnswers).map(convertForSubmission).toList
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
