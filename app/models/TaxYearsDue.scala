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

package models

import java.time.LocalDate

case class TaxYearsDue(cyMinus4Due: Boolean, cyMinus3Due: Boolean, cyMinus2Due: Boolean, cyMinus1Due: Boolean)
                      (today: LocalDate) {

  private val OCTOBER_MONTH = 10
  private val OCTOBER_DAY = 5

  private val octoberDeadline =
    LocalDate.of(uk.gov.hmrc.time.TaxYear.current.starts.getYear, OCTOBER_MONTH, OCTOBER_DAY)

  def toList : List[YearReturnType] = {
    val yearsBeforeCYMinus1 = List(
      YearReturnType(taxReturnYear = CYMinus4TaxYear.asShortFinishYear(), taxConsequence = cyMinus4Due),
      YearReturnType(taxReturnYear = CYMinus3TaxYear.asShortFinishYear(), taxConsequence = cyMinus3Due),
      YearReturnType(taxReturnYear = CYMinus2TaxYear.asShortFinishYear(), taxConsequence = cyMinus2Due)
    ).filter(_.taxConsequence)

    if (cyMinus1Due) {

      lazy val evaluateCYMinus1 : Boolean = if(today.isAfter(octoberDeadline)) {
        cyMinus1Due
      } else {
        !cyMinus1Due
      }

      val cyMinus1 = YearReturnType(taxReturnYear = CYMinus1TaxYear.asShortFinishYear(), evaluateCYMinus1)

      yearsBeforeCYMinus1 :+ cyMinus1
    } else {
      yearsBeforeCYMinus1
    }
  }

}
