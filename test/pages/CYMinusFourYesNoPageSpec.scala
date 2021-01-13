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

import models.CYMinus4TaxYear
import pages.behaviours.PageBehaviours

class CYMinusFourYesNoPageSpec extends PageBehaviours {

  "CYMinusFourYesNoPage" must {

    beRetrievable[Boolean](CYMinusFourYesNoPage)

    beSettable[Boolean](CYMinusFourYesNoPage)

    beRemovable[Boolean](CYMinusFourYesNoPage)

    "implement cleanup logic when NO selected" in {
      val answers = emptyUserAnswers
        .set(CYMinusFourYesNoPage, true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), true).success.value

      val cleaned = answers.set(CYMinusFourYesNoPage, false).success.value

      cleaned.get(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear)) mustNot be(defined)
    }
  }
}