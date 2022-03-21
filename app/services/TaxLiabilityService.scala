/*
 * Copyright 2022 HM Revenue & Customs
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

import models.{CYMinus1TaxYear, CYMinusNTaxYears, UserAnswers, YearReturnType}
import pages.DidDeclareTaxToHMRCYesNoPage

import javax.inject.Inject

class TaxLiabilityService @Inject()(taxYearService: TaxYearService) {

  final val MonthsToSubtract: Int = 6 // October 5th

  def evaluateTaxYears(userAnswers: UserAnswers): List[YearReturnType] = {

    val halfwayThroughTaxYear =
      taxYearService.currentTaxYear.finishes.minusMonths(MonthsToSubtract)

    CYMinusNTaxYears.taxYears.foldLeft[List[YearReturnType]](Nil)((acc, taxYear) => {
      if (userAnswers.get(DidDeclareTaxToHMRCYesNoPage(taxYear)).contains(false)) {
        taxYear match {
          case CYMinus1TaxYear =>
            acc :+ YearReturnType(
              taxReturnYear = taxYearService.nTaxYearsAgoFinishYear(taxYear.n),
              taxConsequence = taxYearService.currentDate.isAfter(halfwayThroughTaxYear)
            )
          case _ =>
            acc :+ YearReturnType(
              taxReturnYear = taxYearService.nTaxYearsAgoFinishYear(taxYear.n),
              taxConsequence = true
            )
        }
      } else {
        acc
      }
    })
  }

}
