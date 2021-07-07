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

import base.SpecBase
import models.{CYMinus1TaxYear, CYMinus2TaxYears, CYMinus3TaxYears, CYMinus4TaxYears, YearReturnType}
import org.joda.time.{DateTime, DateTimeUtils}
import pages.DidDeclareTaxToHMRCYesNoPage
import play.api.inject.bind
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate

class TaxLiabilityServiceSpec extends SpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  def setCurrentDate(date: LocalDate): LocalDateService = new LocalDateService {
    override def now: LocalDate = date
  }

  def setCurrentDateTime(date: LocalDate) = {
    DateTimeUtils.setCurrentMillisFixed(new DateTime(date.toString).getMillis)
  }

  "TaxLiabilityService" must {

    "evaluate answers to CY-1, CY-2, CY-3 and CY-4" when {

      "need to pay tax for CY-1, CY-2, CY-3, CY-4 years (Before 5 October)" must {

        "generate a list with 4 tax consequences" in {
          val userAnswers = emptyUserAnswers
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

          val today = LocalDate.of(2020, 5, 5)

          setCurrentDateTime(today)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
            .build()

          val service = application.injector.instanceOf[TaxLiabilityService]

          val expected = service.evaluateTaxYears(userAnswers)

          expected mustBe List(
            YearReturnType(taxReturnYear = "17", taxConsequence = true),
            YearReturnType(taxReturnYear = "18", taxConsequence = true),
            YearReturnType(taxReturnYear = "19", taxConsequence = true),
            YearReturnType(taxReturnYear = "20", taxConsequence = false)
          )
        }

        "when CY-1 is late (after 5 October)" in {
          val userAnswers = emptyUserAnswers
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

          val today = LocalDate.of(2020, 10, 6)

          setCurrentDateTime(today)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
            .build()

          val service = application.injector.instanceOf[TaxLiabilityService]

          val expected = service.evaluateTaxYears(userAnswers)

          expected mustBe List(
            YearReturnType(taxReturnYear = "17", taxConsequence = true),
            YearReturnType(taxReturnYear = "18", taxConsequence = true),
            YearReturnType(taxReturnYear = "19", taxConsequence = true),
            YearReturnType(taxReturnYear = "20", taxConsequence = true)
          )
        }

      }

      "need to pay tax for CY-1, CY-2 (before 5 October)" when {

        "generate a list with 2 tax consequences" in {
          val userAnswers = emptyUserAnswers
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

          val today = LocalDate.of(2020, 5, 5)

          setCurrentDateTime(today)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
            .build()

          val service = application.injector.instanceOf[TaxLiabilityService]

          val expected = service.evaluateTaxYears(userAnswers)

          expected mustBe List(
            YearReturnType(taxReturnYear = "19", taxConsequence = true),
            YearReturnType(taxReturnYear = "20", taxConsequence = false)
          )
        }

      }

      "need to pay tax for CY-2, CY-4" when {

        "generate a list with 2 tax consequences" in {
          val userAnswers = emptyUserAnswers
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYears), false).success.value
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYears), false).success.value

          val today = LocalDate.of(2020, 5, 5)

          setCurrentDateTime(today)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
            .build()

          val service = application.injector.instanceOf[TaxLiabilityService]

          val expected = service.evaluateTaxYears(userAnswers)

          expected mustBe List(
            YearReturnType(taxReturnYear = "17", taxConsequence = true),
            YearReturnType(taxReturnYear = "19", taxConsequence = true)
          )
        }

      }

    }

    "must evaluate answers for CY-1" when {

      "tax owed is late (after 5 October)" must {

        "generate a list with 1 tax consequence (false)" in {
          val userAnswers = emptyUserAnswers
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

          val today = LocalDate.of(2020, 10, 6)

          setCurrentDateTime(today)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
            .build()

          val service = application.injector.instanceOf[TaxLiabilityService]

          val expected = service.evaluateTaxYears(userAnswers)

          expected mustBe List(
            YearReturnType(taxReturnYear = "20", taxConsequence = true)
          )
        }
      }

      "tax owed is not late (before 5 October)" must {

        "generate a list with 1 tax consequence (true)" in {
          val userAnswers = emptyUserAnswers
            .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

          val today = LocalDate.of(2020, 10, 5)

          setCurrentDateTime(today)

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
            .build()

          val service = application.injector.instanceOf[TaxLiabilityService]

          val expected = service.evaluateTaxYears(userAnswers)

          expected mustBe List(
            YearReturnType(taxReturnYear = "20", taxConsequence = false)
          )
        }
      }
    }
  }
}
