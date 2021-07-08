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

import base.SpecBase
import models.Status.{Completed, InProgress}
import models.{RegistrationSubmission, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.libs.json.{JsNull, Json}
import play.twirl.api.Html
import services.TaxLiabilityService
import utils.CheckYourAnswersHelper
import viewmodels.{AnswerRow, AnswerSection}

import scala.collection.immutable.Nil

class SubmissionSetFactorySpec extends SpecBase {

  "Submission set factory" must {

    val mockCheckYourAnswersHelper = mock[CheckYourAnswersHelper]
    val taxLiabilityService = injector.instanceOf[TaxLiabilityService]
    val factory = new SubmissionSetFactory(mockCheckYourAnswersHelper, taxLiabilityService)

    "reset answer sections and statuses" in {
      val userAnswers: UserAnswers = emptyUserAnswers

      factory.reset(userAnswers) mustBe
        RegistrationSubmission.DataSet(
          Json.toJson(userAnswers),
          Some(InProgress),
          List(RegistrationSubmission.MappedPiece("yearsReturns", JsNull)),
          Nil
        )
    }

    "return no answer sections if not completed" in {

      factory.answerSectionsIfCompleted(emptyUserAnswers, Some(InProgress))
        .mustBe(Nil)
    }

    "return completed answer sections" when {

      val fakeAnswerSection: AnswerSection = AnswerSection(
        headingKey = Some("Tax liability 6 April 2019 to 5 April 2020"),
        rows = List(AnswerRow("Did the trust need to pay any tax from 6 April 2019 to 5 April 2020?", Html("Yes"), None, canEdit = true)),
        sectionKey = None
      )

      "task is completed" in {

        when(mockCheckYourAnswersHelper.apply(any())(any())).thenReturn(Seq(fakeAnswerSection))

        factory.answerSectionsIfCompleted(emptyUserAnswers, Some(Completed)) mustBe
          List(
            RegistrationSubmission.AnswerSection(
              Some("Tax liability 6 April 2019 to 5 April 2020"),
              List(RegistrationSubmission.AnswerRow("Did the trust need to pay any tax from 6 April 2019 to 5 April 2020?", "Yes", "")),
              None
            )
          )
      }
    }
  }

}
