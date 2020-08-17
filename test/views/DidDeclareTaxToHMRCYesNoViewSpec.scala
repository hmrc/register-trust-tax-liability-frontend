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

package views

import forms.YesNoFormProviderWithArguments
import models.{CYMinus1TaxYear, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.DidDeclareTaxToHMRCYesNoView

class DidDeclareTaxToHMRCYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "didDeclareToHMRC"

  val form: Form[Boolean] = new YesNoFormProviderWithArguments()
    .withPrefix(messageKeyPrefix, Seq("6 April 2019", "5 April 2020"))

  "DidDeclareTaxToHMRCYesNo view" must {

    val view = viewFor[DidDeclareTaxToHMRCYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, draftId, CYMinus1TaxYear, "6 April 2019 to 5 April 2020", NormalMode)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, "6 April 2019 to 5 April 2020")

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, Some("6 April 2019 to 5 April 2020"))

    behave like pageWithASubmitButton(applyView(form))
  }
}
