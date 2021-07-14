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

package views

import models.CYMinusNTaxYears
import play.twirl.api.HtmlFormat
import viewmodels.{AnswerRow, AnswerSection}
import views.behaviours.SummaryListViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends SummaryListViewBehaviours {

  val messageKeyPrefix = "checkYourAnswers"

  val args = Seq("arg1", "arg2")

  val answerSections: Seq[AnswerSection] = CYMinusNTaxYears.taxYears.reverse.foldLeft[Seq[AnswerSection]](Nil)((acc, taxYear) => {
    val i = taxYear.n
    acc :+ AnswerSection(
      headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
      rows = Seq(
        AnswerRow(
          label = s"${taxYear.messagePrefix}.liability.checkYourAnswersLabel",
          answer = HtmlFormat.escape(s"Answer $i"),
          changeUrl = Some(s"change-url-$i"),
          labelArgs = args,
          canEdit = true
        )
      ),
      headingArgs = args
    )
  })

  "CheckAnswers view" must {

    val view = viewFor[CheckYourAnswersView](Some(emptyUserAnswers))

    val applyView = view.apply(answerSections, draftId)(fakeRequest, messages)

    behave like normalPage(applyView, messageKeyPrefix)

    behave like summaryListPage(applyView, answerSections)

    behave like pageWithBackLink(applyView)

    behave like pageWithASubmitButton(applyView)
  }
}
