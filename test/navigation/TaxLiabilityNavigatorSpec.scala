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

package navigation

import base.SpecBase
import models.NormalMode
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class TaxLiabilityNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new TaxLiabilityNavigator

  "Business navigator" when {

    "add journey navigation" must {

      val mode = NormalMode

      "CY-4 Earlier Years Yes/No page -> Yes -> CY-4 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusFourEarlierYearsYesNoPage, true).success.value

        navigator.nextPage(CYMinusFourEarlierYearsYesNoPage, mode, answers)
          .mustBe(controllers.routes.CYMinusFourLiabilityController.onPageLoad(mode))
      }

      "CY-4 Earlier Years Yes/No page -> No -> CY-4 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusFourEarlierYearsYesNoPage, false).success.value

        navigator.nextPage(CYMinusFourEarlierYearsYesNoPage, mode, answers)
          .mustBe(controllers.routes.CYMinusFourLiabilityController.onPageLoad(mode))
      }

      "CY-3 Earlier Years Yes/No page -> Yes -> CY-3 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusThreeEarlierYearsYesNoPage, true).success.value

        navigator.nextPage(CYMinusThreeEarlierYearsYesNoPage, mode, answers)
          .mustBe(controllers.routes.CYMinusThreeLiabilityController.onPageLoad(mode))
      }

      "CY-3 Earlier Years Yes/No page -> No -> CY-3 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusThreeEarlierYearsYesNoPage, false).success.value

        navigator.nextPage(CYMinusThreeEarlierYearsYesNoPage, mode, answers)
          .mustBe(controllers.routes.CYMinusThreeLiabilityController.onPageLoad(mode))
      }
//
//      "CY-1 Declared Yes/No page -> Check details page" in {
//        navigator.nextPage(CYMinusOneDeclaredYesNoPage, mode, emptyUserAnswers)
//          .mustBe(controllers.routes.CheckDetailsController.onPageLoad())
//      }
    }
  }
}
