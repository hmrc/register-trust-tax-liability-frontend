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

import base.SpecBase
import connectors.EstatesConnector
import models.TaxLiabilityYear
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.Future

class TaxLiabilityServiceSpec extends SpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  def setCurrentDate(date: LocalDate): LocalDateService = new LocalDateService {
    override def now: LocalDate = date
  }

  "getFirstYearOfTaxLiability" must {

    "return the cy minus four tax liability and and true to earlier years" when {

      "the current date is before the december deadline and date of death is more than 4 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2015, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2016), earlierYears = true)
      }

      "the current date is on the december deadline and date of death is more than 4 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 22)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2015, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2016), earlierYears = true)
      }
    }

    "return the cy minus three tax liability and and true to earlier years" when {

      "the current date is after the december deadline and date of death is more than 3 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateAfterDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2015, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2017), earlierYears = true)
      }
    }

    "return the cy minus four tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 4 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2016, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2016), earlierYears = false)
      }
    }

    "return the cy minus three tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 3 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2017, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2017), earlierYears = false)
      }

      "the current date is after the december deadline and date of death is 3 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2017, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2017), earlierYears = false)
      }
    }

    "return the cy minus two tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 2 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2018, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2018), earlierYears = false)
      }

      "the current date is after the december deadline and date of death is 2 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2018, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2018), earlierYears = false)
      }
    }

    "return the cy minus one tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 1 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2019, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2019), earlierYears = false)
      }

      "the current date is after the december deadline and date of death is 3 years ago" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2019, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability()

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2019), earlierYears = false)
      }
    }
  }

  "getTaxYearOfDeath" must {
    "return the correct tax year the date of death falls in" when {
      "date of death is between Jan 1st and April 5th (inclusive)" in {

      val mockEstatesConnector = mock[EstatesConnector]

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
        .build()

      val dateOfDeath = LocalDate.of(2018, 1, 1)

      when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

      val service = application.injector.instanceOf[TaxLiabilityService]

      val result = service.getTaxYearOfDeath()

      result.futureValue mustEqual TaxYear(2017)
      }

      "date of death is between April 6th and Dec 31st (inclusive)" in {

        val mockEstatesConnector = mock[EstatesConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .build()

        val dateOfDeath = LocalDate.of(2018, 6, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getTaxYearOfDeath()

        result.futureValue mustEqual TaxYear(2018)
      }
    }
  }

  "getIsTaxLiabilityLate" must {
    "return false if current date is before the December deadline and the tax year is CY-1" in {

      val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
        .build()

      val service = application.injector.instanceOf[TaxLiabilityService]

      val result = service.getIsTaxLiabilityLate(TaxYear(2019), alreadyPaid = false)

      result mustEqual false
    }

    "return false if the tax year is before CY-1 but already paid is true" in {

      val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
        .build()

      val service = application.injector.instanceOf[TaxLiabilityService]

      val result = service.getIsTaxLiabilityLate(TaxYear(2018), alreadyPaid = true)

      result mustEqual false
    }

    "return true if the tax year is before CY-1 and already paid is false" in {

      val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
        .build()

      val service = application.injector.instanceOf[TaxLiabilityService]

      val result = service.getIsTaxLiabilityLate(TaxYear(2018), alreadyPaid = false)

      result mustEqual true
    }
  }
}
