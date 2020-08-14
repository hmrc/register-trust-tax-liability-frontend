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

import base.SpecBase
import models.RegistrationSubmission.{AnswerRow, AnswerSection}
import models.Status.{Completed, InProgress}
import models.UserAnswers
import pages.{CYMinusOneYesNoPage, CYMinusTwoYesNoPage, TaxLiabilityTaskStatus}

import scala.collection.immutable.Nil

class SubmissionSetFactorySpec extends SpecBase {

  "Submission set factory" must {

    val factory = injector.instanceOf[SubmissionSetFactory]

    "return no answer sections if not completed" in {

      factory.answerSectionsIfCompleted(emptyUserAnswers, Some(InProgress))
        .mustBe(Nil)
    }

    "return completed answer sections" when {

      "task is completed with one year" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(TaxLiabilityTaskStatus, Completed).success.value
              .set(CYMinusOneYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
        List(
          AnswerSection(
            Some("Tax liability 6 April 2019 to 5 April 2020"),
            List(AnswerRow("Did the estate need to pay any tax from 6 April 2019 to 5 April 2020?", "Yes", "")),
            None
          )
        )
      }

      "task is completed with more than one year" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(TaxLiabilityTaskStatus, Completed).success.value
          .set(CYMinusOneYesNoPage, true).success.value
          .set(CYMinusTwoYesNoPage, true).success.value

        factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
          List(
            AnswerSection(
              Some("Tax liability 6 April 2018 to 5 April 2019"),
              List(AnswerRow("Did the estate need to pay any tax from 6 April 2018 to 5 April 2019?", "Yes", "")),
              None
            ),
            AnswerSection(
              Some("Tax liability 6 April 2019 to 5 April 2020"),
              List(AnswerRow("Did the estate need to pay any tax from 6 April 2019 to 5 April 2020?", "Yes", "")),
              None
            )
          )
      }
    }
  }

}
