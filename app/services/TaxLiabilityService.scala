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

package services

import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears, TaxYearsDue, UserAnswers, YearReturnType}
import pages.DidDeclareTaxToHMRCYesNoPage

import javax.inject.Inject

class TaxLiabilityService @Inject()(localDateService: LocalDateService) {

  def evaluateTaxYears(userAnswers: UserAnswers): List[YearReturnType] = {

    val yearsDeclared = TaxYearsDue(
      cyMinus4Due = userAnswers.get(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears)).contains(false),
      cyMinus3Due = userAnswers.get(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYears)).contains(false),
      cyMinus2Due = userAnswers.get(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears)).contains(false),
      cyMinus1Due = userAnswers.get(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear)).contains(false)
    )(localDateService.now)

    yearsDeclared.toList
  }

}
