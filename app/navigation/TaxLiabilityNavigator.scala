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

import controllers.{routes => rts}
import javax.inject.Inject
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, Mode, NormalMode, UserAnswers}
import pages._
import play.api.mvc.Call

class TaxLiabilityNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, draftId: String, mode: Mode, userAnswers: UserAnswers): Call = routes(mode, draftId)(page)(userAnswers)

  private def simpleNavigation(mode: Mode, draftId: String): PartialFunction[Page, Call] = {
    case CYMinusFourEarlierYearsYesNoPage => rts.CYMinusFourLiabilityController.onPageLoad(NormalMode, draftId)
    case CYMinusThreeEarlierYearsYesNoPage => rts.CYMinusThreeLiabilityController.onPageLoad(NormalMode, draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear) => rts.CYMinusThreeLiabilityController.onPageLoad(NormalMode, draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear) => rts.CYMinusTwoLiabilityController.onPageLoad(NormalMode, draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear) => rts.CYMinusOneLiabilityController.onPageLoad(NormalMode, draftId)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear) => rts.CheckYourAnswersController.onPageLoad(draftId)
  }

  private def conditionalNavigation(mode: Mode, draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case CYMinusFourYesNoPage => ua => yesNoNav(ua, CYMinusFourYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, draftId, CYMinus4TaxYear),
      noCall = rts.CYMinusThreeLiabilityController.onPageLoad(mode, draftId)
    )
    case CYMinusThreeYesNoPage => ua => yesNoNav(ua, CYMinusThreeYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, draftId, CYMinus3TaxYear),
      noCall = rts.CYMinusTwoLiabilityController.onPageLoad(mode, draftId)
    )
    case CYMinusTwoYesNoPage => ua => yesNoNav(ua, CYMinusTwoYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, draftId, CYMinus2TaxYear),
      noCall = rts.CYMinusOneLiabilityController.onPageLoad(mode, draftId)
    )
    case CYMinusOneYesNoPage => ua => yesNoNav(ua, CYMinusOneYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, draftId, CYMinus1TaxYear),
      noCall = rts.CheckYourAnswersController.onPageLoad(draftId)
    )
  }

  private def routes(mode: Mode, draftId: String): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode, draftId) andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation(mode, draftId)
}
