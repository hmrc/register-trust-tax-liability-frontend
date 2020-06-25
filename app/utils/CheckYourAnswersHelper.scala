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
import models.{NormalMode, UserAnswers}
import pages.CYMinusFourEarlierYearsYesNoPage
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def currentYearMinus4Answers(userAnswers: UserAnswers)(implicit messages: Messages) : Option[AnswerSection] = {
    val bound = answerRowConverter.bind(userAnswers)

    val answerRows : Seq[AnswerRow] = Seq(
      bound.yesNoQuestion(
        CYMinusFourEarlierYearsYesNoPage,
        "earlierYearsLiability",
        controllers.routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode).url
      )
    ).flatten

    answerRows match {
      case Nil => None
      case _ => Some(AnswerSection(None, answerRows))
    }

  }

}

object CheckYourAnswersHelper {

}
