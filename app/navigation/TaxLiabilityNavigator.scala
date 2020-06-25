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

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case CYMinusFourEarlierYearsYesNoPage => rts.CYMinusFourLiabilityController.onPageLoad(NormalMode)
    case CYMinusThreeEarlierYearsYesNoPage => rts.CYMinusThreeLiabilityController.onPageLoad(NormalMode)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear) => rts.CYMinusThreeLiabilityController.onPageLoad(NormalMode)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear) => rts.CYMinusTwoLiabilityController.onPageLoad(NormalMode)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear) => rts.CYMinusOneLiabilityController.onPageLoad(NormalMode)
    case DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear) => rts.CheckYourAnswersController.onPageLoad()
  }

  private def conditionalNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case CYMinusFourYesNoPage => ua => yesNoNav(ua, CYMinusFourYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, CYMinus4TaxYear),
      noCall = rts.CYMinusThreeLiabilityController.onPageLoad(mode)
    )
    case CYMinusThreeYesNoPage => ua => yesNoNav(ua, CYMinusThreeYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, CYMinus3TaxYear),
      noCall = rts.CYMinusTwoLiabilityController.onPageLoad(mode)
    )
    case CYMinusTwoYesNoPage => ua => yesNoNav(ua, CYMinusTwoYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, CYMinus2TaxYear),
      noCall = rts.CYMinusOneLiabilityController.onPageLoad(mode)
    )
    case CYMinusOneYesNoPage => ua => yesNoNav(ua, CYMinusOneYesNoPage,
      yesCall = rts.DidDeclareTaxToHMRCController.onPageLoad(mode, CYMinus1TaxYear),
      noCall = rts.CheckYourAnswersController.onPageLoad()
    )
  }

  private def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_:UserAnswers) => c) orElse
      conditionalNavigation(mode)
}
