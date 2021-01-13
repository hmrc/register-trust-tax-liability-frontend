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

package pages

import models.CYMinus1TaxYear
import pages.behaviours.PageBehaviours

class CYMinusOneYesNoPageSpec extends PageBehaviours {

  "CYMinusOneYesNoPage" must {

    beRetrievable[Boolean](CYMinusOneYesNoPage)

    beSettable[Boolean](CYMinusOneYesNoPage)

    beRemovable[Boolean](CYMinusOneYesNoPage)

    "implement cleanup logic when NO selected" in {
      val answers = emptyUserAnswers
        .set(CYMinusOneYesNoPage, true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), true).success.value

      val cleaned = answers.set(CYMinusOneYesNoPage, false).success.value

      cleaned.get(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear)) mustNot be(defined)
    }
  }
}