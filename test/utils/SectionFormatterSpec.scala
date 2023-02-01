/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import controllers.routes
import models.CYMinus1TaxYear
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.{AnswerRow, AnswerSection}

class SectionFormatterSpec extends SpecBase {

  "SectionFormatter" must {

    "format section as series of summary list rows" in {

      val answerSection: AnswerSection = AnswerSection(
        headingKey = "taxLiabilityBetweenYears.checkYourAnswerSectionHeading",
        rows = Seq(
          AnswerRow(
            label = "cyMinusOne.liability.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(routes.CYMinusOneLiabilityController.onPageLoad(draftId).url),
            labelArgs = Seq("6 April 2019", "5 April 2020"),
            canEdit = true
          ),
          AnswerRow(
            label = "didDeclareToHMRC.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus1TaxYear).url),
            labelArgs = Seq("6 April 2019", "5 April 2020"),
            canEdit = true
          )
        ),
        headingArgs = Seq("6 April 2019", "5 April 2020")
      )

      val result = SectionFormatter.formatAnswerSection(answerSection)

      result mustEqual Seq(
        SummaryListRow(
          key = Key(
            classes = "govuk-!-width-two-thirds",
            content = Text("Did the trust need to pay any tax from 6 April 2019 to 5 April 2020?")
          ),
          value = Value(classes = "govuk-!-width-one-half", content = HtmlContent("Yes")),
          actions = Option(Actions(items = Seq(ActionItem(href = routes.CYMinusOneLiabilityController.onPageLoad(draftId).url,
            classes = s"change-link-0",
            visuallyHiddenText = Some("Did the trust need to pay any tax from 6 April 2019 to 5 April 2020?"),
            content = Text(messages("site.edit"))
          ))))
        ),
        SummaryListRow(
          key = Key(
            classes = "govuk-!-width-two-thirds",
            content = Text("Was the tax from 6 April 2019 to 5 April 2020 declared?")
          ),
          value = Value(classes = "govuk-!-width-one-half", content = HtmlContent("No")),
          actions = Option(Actions(items = Seq(ActionItem(href = routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus1TaxYear).url,
            classes = s"change-link-1",
            visuallyHiddenText = Some("Was the tax from 6 April 2019 to 5 April 2020 declared?"),
            content = Text(messages("site.edit"))
          ))))
        )
      )
    }
  }
}
