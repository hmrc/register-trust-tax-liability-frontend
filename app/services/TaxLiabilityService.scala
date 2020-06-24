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

package services

import java.time.LocalDate

import connectors.EstatesConnector
import javax.inject.Inject
import models.TaxLiabilityYear
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.{ExecutionContext, Future}

class TaxLiabilityService @Inject()(estatesConnector: EstatesConnector,
                                    localDateService: LocalDateService
                                   )(implicit ec: ExecutionContext) {

  private val deadlineMonth = 12
  private val deadlineDay = 22
  private val currentTaxYearStart = LocalDate.of(TaxYear.current.starts.getYear, TaxYear.current.starts.getMonthOfYear, TaxYear.current.starts.getDayOfMonth)
  private val decemberDeadline = LocalDate.of(TaxYear.current.starts.getYear, deadlineMonth, deadlineDay)

  def getFirstYearOfTaxLiability()(implicit hc: HeaderCarrier): Future[TaxLiabilityYear] = {
    val oldestYearToShow = if (!(localDateService.now.isBefore(currentTaxYearStart) || localDateService.now.isAfter(decemberDeadline))) {
      TaxYear.current.back(4)
    } else {
      TaxYear.current.back(3)
    }

    getTaxYearOfDeath().map{ taxYearOfDeath =>
      if (taxYearOfDeath.startYear < oldestYearToShow.startYear) {
        TaxLiabilityYear(oldestYearToShow, earlierYears = true)
      } else {
        TaxLiabilityYear(taxYearOfDeath, earlierYears = false)
      }
    }
  }

  def getIsTaxLiabilityLate(taxYear: TaxYear, alreadyPaid: Boolean): Boolean = {
    if ((!(localDateService.now.isBefore(currentTaxYearStart) || localDateService.now.isAfter(decemberDeadline))) &&
      (taxYear.startYear == TaxYear.current.previous.startYear)) {
      false
    } else if (alreadyPaid) {
      false
    } else {
      true
    }
  }

  private def getTaxYearOfDeath()(implicit hc: HeaderCarrier): Future[TaxYear] = {
    estatesConnector.getDateOfDeath().map { dateOfDeath =>
      val beforeApril = dateOfDeath.getMonthValue < 4
      val between1stAnd5thApril = dateOfDeath.getMonthValue == 4 && dateOfDeath.getDayOfMonth < 6

      if (beforeApril || between1stAnd5thApril) {
        TaxYear(dateOfDeath.getYear - 1)
      } else {
        TaxYear(dateOfDeath.getYear)
      }
    }
  }
}
