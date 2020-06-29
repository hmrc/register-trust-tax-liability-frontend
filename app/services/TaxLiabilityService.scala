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
import org.joda.time.DateTime
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.{ExecutionContext, Future}

class TaxLiabilityService @Inject()(estatesConnector: EstatesConnector,
                                    localDateService: LocalDateService
                                   )(implicit ec: ExecutionContext) {

  private val APRIL = 4
  private val TAX_YEAR_START_DAY = 6

  private val DEADLINE_MONTH = 12
  private val DEADLINE_DAY = 22

  private val LAST_4_TAX_YEARS = 4
  private val LAST_3_TAX_YEARS = 3

  private val currentTaxYearStartDate = LocalDate.of(
    TaxYear.current.starts.getYear,
    TaxYear.current.starts.getMonthOfYear,
    TaxYear.current.starts.getDayOfMonth
  )

  private val decemberDeadline = LocalDate.of(TaxYear.current.starts.getYear, DEADLINE_MONTH, DEADLINE_DAY)

  def getFirstYearOfTaxLiability()(implicit hc: HeaderCarrier): Future[TaxLiabilityYear] = {

    val today = localDateService.now

    val todayIsAfterTaxYearStart : Boolean = today.isAfter(currentTaxYearStartDate.minusDays(1))
    val isNotAfterDecemberDeadline : Boolean = today.isBefore(decemberDeadline.plusDays(1))

    val oldestYearToShow = if (todayIsAfterTaxYearStart && isNotAfterDecemberDeadline) {
      TaxYear.current.back(LAST_4_TAX_YEARS)
    } else {
      TaxYear.current.back(LAST_3_TAX_YEARS)
    }

    getTaxYearOfDeath().map{ taxYearOfDeath =>

      val deathWasBeforeMaximum4Years = taxYearOfDeath.startYear < oldestYearToShow.startYear

      if (deathWasBeforeMaximum4Years) {
        TaxLiabilityYear(oldestYearToShow, earlierYears = true)
      } else {
        TaxLiabilityYear(taxYearOfDeath, earlierYears = false)
      }
    }
  }

  def getIsTaxLiabilityLate(taxYear: TaxYear, alreadyPaid: Boolean): Boolean = {
    val currentDateIsBeforeDecemberDeadline = !(localDateService.now.isBefore(currentTaxYearStartDate) || localDateService.now.isAfter(decemberDeadline))
    val taxYearIsCyMinusOne = taxYear.startYear == TaxYear.current.previous.startYear

    if (currentDateIsBeforeDecemberDeadline && taxYearIsCyMinusOne) {
      false
    } else if (alreadyPaid) {
      false
    } else {
      true
    }
  }

  def getTaxYearOfDeath()(implicit hc: HeaderCarrier): Future[TaxYear] = {
    estatesConnector.getDateOfDeath().map { dateOfDeath =>
      val beforeApril = dateOfDeath.getMonthValue < APRIL
      val between1stAnd5thApril = dateOfDeath.getMonthValue == APRIL && dateOfDeath.getDayOfMonth < TAX_YEAR_START_DAY

      if (beforeApril || between1stAnd5thApril) {
        TaxYear(dateOfDeath.getYear - 1)
      } else {
        TaxYear(dateOfDeath.getYear)
      }
    }
  }
}
