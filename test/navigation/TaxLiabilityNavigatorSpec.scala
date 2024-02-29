/*
 * Copyright 2024 HM Revenue & Customs
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
import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._

class TaxLiabilityNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks  {

  val navigator = new TaxLiabilityNavigator

  "tax liability navigator" when {

    "add journey navigation" must {

      "CY-4 Earlier Years info page -> Yes -> CY-4 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusFourEarlierYearsYesNoPage, true).success.value

        navigator.nextPage(CYMinusFourEarlierYearsYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CYMinusFourLiabilityController.onPageLoad(draftId))
      }

      "CY-4 Earlier Years info page -> No -> CY-4 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusFourEarlierYearsYesNoPage, false).success.value

        navigator.nextPage(CYMinusFourEarlierYearsYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CYMinusFourLiabilityController.onPageLoad(draftId))
      }

      "CY-3 Earlier Years info page -> Yes -> CY-3 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusThreeEarlierYearsYesNoPage, true).success.value

        navigator.nextPage(CYMinusThreeEarlierYearsYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CYMinusThreeLiabilityController.onPageLoad(draftId))
      }

      "CY-3 Earlier Years info page -> No -> CY-3 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusThreeEarlierYearsYesNoPage, false).success.value

        navigator.nextPage(CYMinusThreeEarlierYearsYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CYMinusThreeLiabilityController.onPageLoad(draftId))
      }





      "CY-4 Liability Yes/No page -> Yes -> CY-4 Did you declare page" in {
        val answers = emptyUserAnswers
          .set(CYMinusFourYesNoPage, true).success.value

        navigator.nextPage(CYMinusFourYesNoPage, draftId, answers)
          .mustBe(controllers.routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus4TaxYears))
      }

      "CY-4 Did you declare -> Any -> CY-3 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears), true).success.value

        navigator.nextPage(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears), draftId, answers)
          .mustBe(controllers.routes.CYMinusThreeLiabilityController.onPageLoad(draftId))
      }

      "CY-4 Liability Yes/No page -> No -> CY-3 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusFourYesNoPage, false).success.value

        navigator.nextPage(CYMinusFourYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CYMinusThreeLiabilityController.onPageLoad(draftId))
      }




      "CY-3 Liability Yes/No page -> Yes -> CY-3 Did you declare page" in {
        val answers = emptyUserAnswers
          .set(CYMinusThreeYesNoPage, true).success.value

        navigator.nextPage(CYMinusThreeYesNoPage, draftId, answers)
          .mustBe(controllers.routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus3TaxYears))
      }

      "CY-3 Did you declare -> Any -> CY-2 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYears), true).success.value

        navigator.nextPage(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYears), draftId, answers)
          .mustBe(controllers.routes.CYMinusTwoLiabilityController.onPageLoad(draftId))
      }

      "CY-3 Liability Yes/No page -> No -> CY-2 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusThreeYesNoPage, false).success.value

        navigator.nextPage(CYMinusThreeYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CYMinusTwoLiabilityController.onPageLoad(draftId))
      }




      "CY-2 Liability Yes/No page -> Yes -> CY-2 Did you declare page" in {
        val answers = emptyUserAnswers
          .set(CYMinusTwoYesNoPage, true).success.value

        navigator.nextPage(CYMinusTwoYesNoPage, draftId, answers)
          .mustBe(controllers.routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus2TaxYears))
      }

      "CY-2 Did you declare -> Any -> CY-1 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears), true).success.value

        navigator.nextPage(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears), draftId, answers)
          .mustBe(controllers.routes.CYMinusOneLiabilityController.onPageLoad(draftId))
      }

      "CY-2 Liability Yes/No page -> No -> CY-1 Liability Yes/No page" in {
        val answers = emptyUserAnswers
          .set(CYMinusTwoYesNoPage, false).success.value

        navigator.nextPage(CYMinusTwoYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CYMinusOneLiabilityController.onPageLoad(draftId))
      }



      "CY-1 Liability Yes/No page -> Yes -> CY-1 Did you declare page" in {
        val answers = emptyUserAnswers
          .set(CYMinusOneYesNoPage, true).success.value

        navigator.nextPage(CYMinusOneYesNoPage, draftId, answers)
          .mustBe(controllers.routes.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus1TaxYear))
      }

      "CY-1 Did you declare -> Any -> Check your answers" in {
        val answers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), true).success.value

        navigator.nextPage(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), draftId, answers)
          .mustBe(controllers.routes.CheckYourAnswersController.onPageLoad(draftId))
      }

      "CY-1 Liability Yes/No page -> No -> Check your answers" in {
        val answers = emptyUserAnswers
          .set(CYMinusOneYesNoPage, false).success.value

        navigator.nextPage(CYMinusOneYesNoPage, draftId, answers)
          .mustBe(controllers.routes.CheckYourAnswersController.onPageLoad(draftId))
      }

    }
  }
}
