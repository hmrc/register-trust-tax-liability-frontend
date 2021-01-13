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
import models.UserAnswers
import pages.QuestionPage
import play.api.i18n.Messages
import viewmodels.AnswerRow

class AnswerRowConverter @Inject()() {

  def bind(userAnswers: UserAnswers)
          (implicit messages: Messages): Bound = new Bound(userAnswers)

  class Bound(userAnswers: UserAnswers)(implicit messages: Messages) {

    def yesNoQuestion(query: QuestionPage[Boolean],
                      labelKey: String,
                      changeUrl: Option[String],
                      arguments: Any*): Option[AnswerRow] = {
      userAnswers.get(query) map {x =>
        AnswerRow(
          messages(s"$labelKey.checkYourAnswersLabel", arguments: _*),
          CheckAnswersFormatters.yesOrNo(x),
          changeUrl,
          canEdit = changeUrl.isDefined
        )
      }
    }

  }
}
