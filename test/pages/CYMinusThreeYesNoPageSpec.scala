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

import models.CYMinus3TaxYear
import pages.behaviours.PageBehaviours

class CYMinusThreeYesNoPageSpec extends PageBehaviours {

  "CYMinusThreeYesNoPage" must {

    beRetrievable[Boolean](CYMinusThreeYesNoPage)

    beSettable[Boolean](CYMinusThreeYesNoPage)

    beRemovable[Boolean](CYMinusThreeYesNoPage)

    "implement cleanup logic when NO selected" in {
      val answers = emptyUserAnswers
        .set(CYMinusThreeYesNoPage, true).success.value
        .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), true).success.value

      val cleaned = answers.set(CYMinusThreeYesNoPage, false).success.value

      cleaned.get(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear)) mustNot be(defined)
    }
  }
}