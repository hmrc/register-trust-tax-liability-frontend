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

package navigation

import controllers.{routes => rts}
import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears, UserAnswers}
import pages._
import play.api.mvc.Call

import javax.inject.Inject

class TaxLiabilityNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: UserAnswers): Call = routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(draftId) andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case CYMinusFourEarlierYearsYesNoPage => rts.CYMinusFourLiabilityController.onPageLoad(draftId)
    case CYMinusThreeEarlierYearsYesNoPage => rts.CYMinusThreeLiabilityController.onPageLoad(draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears) => rts.CYMinusThreeLiabilityController.onPageLoad(draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYears) => rts.CYMinusTwoLiabilityController.onPageLoad(draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears) => rts.CYMinusOneLiabilityController.onPageLoad(draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear) => rts.CheckYourAnswersController.onPageLoad(draftId)
  }

  private def conditionalNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case CYMinusFourYesNoPage => ua => yesNoNav(ua, CYMinusFourYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus4TaxYears),
      noCall = rts.CYMinusThreeLiabilityController.onPageLoad(draftId)
    )
    case CYMinusThreeYesNoPage => ua => yesNoNav(ua, CYMinusThreeYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus3TaxYears),
      noCall = rts.CYMinusTwoLiabilityController.onPageLoad(draftId)
    )
    case CYMinusTwoYesNoPage => ua => yesNoNav(ua, CYMinusTwoYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus2TaxYears),
      noCall = rts.CYMinusOneLiabilityController.onPageLoad(draftId)
    )
    case CYMinusOneYesNoPage => ua => yesNoNav(ua, CYMinusOneYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(draftId, CYMinus1TaxYear),
      noCall = rts.CheckYourAnswersController.onPageLoad(draftId)
    )
  }

}
