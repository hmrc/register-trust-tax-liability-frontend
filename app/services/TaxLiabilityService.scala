package services

import java.time.LocalDate

import org.joda.time.{LocalDate => JodaLocalDate}
import connectors.EstatesConnector
import javax.inject.Inject
import models.TaxLiabilityYear
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.{ExecutionContext, Future}

class TaxLiabilityService @Inject()(estatesConnector: EstatesConnector)(implicit ec: ExecutionContext, hc: HeaderCarrier) {

  private val deadlineMonth = 12
  private val deadlineDay = 22
  private val currentTaxYearStart = LocalDate.of(TaxYear.current.starts.getYear, TaxYear.current.starts.getMonthOfYear, TaxYear.current.starts.getDayOfMonth)
  private val decemberDeadline = LocalDate.of(TaxYear.current.starts.getYear, deadlineMonth, deadlineDay)

  def getFirstYearOfTaxLiability(): Future[TaxLiabilityYear] = {
    val oldestYearToShow = if (!(LocalDate.now().isBefore(currentTaxYearStart) || LocalDate.now().isAfter(decemberDeadline))) {
      TaxYear.current.back(4)
    } else {
      TaxYear.current.back(3)
    }

    getTaxYearOfDeath().map{ taxYearOfDeath =>
      if (taxYearOfDeath.startYear > oldestYearToShow.startYear) {
        TaxLiabilityYear(oldestYearToShow, true)
      } else {
        TaxLiabilityYear(taxYearOfDeath, false)
      }
    }
  }

  def getIsTaxLiabilityLate(taxYear: TaxYear, alreadyPaid: Boolean): Boolean = {
    if ((!(LocalDate.now().isBefore(currentTaxYearStart) || LocalDate.now().isAfter(decemberDeadline))) &&
      (taxYear.startYear == TaxYear.current.previous.startYear)) {
      false
    } else if (alreadyPaid) {
      false
    } else {
      true
    }
  }

  private def getTaxYearOfDeath(): Future[TaxYear] = {
    estatesConnector.getDateOfDeath().map { date =>
      if ((date.getMonthValue < 4 || (date.getMonthValue == 4 && date.getDayOfMonth < 6))) {
        TaxYear(date.getYear - 1)
      } else {
        TaxYear(date.getYear)
      }
    }
  }
}
