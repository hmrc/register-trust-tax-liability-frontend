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

package views.behaviours

import play.twirl.api.HtmlFormat
import viewmodels.AnswerSection

trait SummaryListViewBehaviours extends ViewBehaviours {

  def summaryListPage(view: HtmlFormat.Appendable,
                      answerSections: Seq[AnswerSection] = Nil): Unit = {

    "behave like a page with a summary list" in {

      val doc = asDocument(view)

      val h2s = doc.getElementsByClass("govuk-heading-m")
      h2s.size mustBe answerSections.size

      val summaryLists = doc.getElementsByClass("govuk-summary-list")
      summaryLists.size mustBe answerSections.size

      for ((answerSection, i) <- answerSections.zipWithIndex) {

        val h2 = h2s.get(i)
        h2.text mustBe messages(answerSection.headingKey, answerSection.headingArgs: _*)

        val summaryList = summaryLists.get(i)
        val listRows = summaryList.getElementsByClass("govuk-summary-list__row")
        listRows.size mustBe answerSection.rows.size

        for ((answerRow, j) <- answerSection.rows.zipWithIndex) {
          val listRow = listRows.get(j)

          val key = listRow.getElementsByClass("govuk-summary-list__key").first()
          key.text mustBe messages(answerRow.label, answerRow.labelArgs: _*)

          val value = listRow.getElementsByClass("govuk-summary-list__value").first()
          value.text mustBe answerRow.answer.toString()

          val actions = listRow.getElementsByClass("govuk-summary-list__actions").first()
          actions.getElementsByTag("a").attr("href") must include(answerRow.changeUrl.getOrElse(""))
        }
      }
    }
  }
}
