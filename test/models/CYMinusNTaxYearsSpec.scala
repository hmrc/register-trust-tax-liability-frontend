/*
 * Copyright 2025 HM Revenue & Customs
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

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import play.api.mvc.PathBindable
import controllers.routes._

class CYMinusNTaxYearsSpec extends AnyFreeSpec {

  "taxYears" - {
    "must list all cases in descending order" in {
      CYMinusNTaxYears.taxYears.map(_.n) mustBe Seq(4, 3, 2, 1)
    }
  }

  "case objects" - {
    "must have the correct n, messagePrefix and changeUrl" in {
      val draftId = "draft-123"

      CYMinus4TaxYears.n mustBe 4
      CYMinus4TaxYears.messagePrefix mustBe "cyMinusFour"
      CYMinus4TaxYears.toString mustBe "4"
      CYMinus4TaxYears.changeUrl(draftId) mustBe
        CYMinusFourLiabilityController.onPageLoad(draftId).url

      CYMinus3TaxYears.n mustBe 3
      CYMinus3TaxYears.messagePrefix mustBe "cyMinusThree"
      CYMinus3TaxYears.toString mustBe "3"
      CYMinus3TaxYears.changeUrl(draftId) mustBe
        CYMinusThreeLiabilityController.onPageLoad(draftId).url

      CYMinus2TaxYears.n mustBe 2
      CYMinus2TaxYears.messagePrefix mustBe "cyMinusTwo"
      CYMinus2TaxYears.toString mustBe "2"
      CYMinus2TaxYears.changeUrl(draftId) mustBe
        CYMinusTwoLiabilityController.onPageLoad(draftId).url

      CYMinus1TaxYear.n mustBe 1
      CYMinus1TaxYear.messagePrefix mustBe "cyMinusOne"
      CYMinus1TaxYear.toString mustBe "1"
      CYMinus1TaxYear.changeUrl(draftId) mustBe
        CYMinusOneLiabilityController.onPageLoad(draftId).url
    }
  }

  "pathBindable" - {
    val binder: PathBindable[CYMinusNTaxYears] =
      implicitly[PathBindable[CYMinusNTaxYears]]

    "must bind valid ids to the correct case objects" in {
      binder.bind("taxYear", "4") mustBe Right(CYMinus4TaxYears)
      binder.bind("taxYear", "3") mustBe Right(CYMinus3TaxYears)
      binder.bind("taxYear", "2") mustBe Right(CYMinus2TaxYears)
      binder.bind("taxYear", "1") mustBe Right(CYMinus1TaxYear)
    }

    "must return an error for invalid ids" in {
      binder.bind("taxYear", "0") mustBe Left("Not a valid tax year")
      binder.bind("taxYear", "5") mustBe Left("Not a valid tax year")
      binder.bind("taxYear", "abc") mustBe a[Left[_, _]]
    }

    "must unbind a case object to its lower‑case string representation" in {
      binder.unbind("taxYear", CYMinus4TaxYears) mustBe "4"
      binder.unbind("taxYear", CYMinus3TaxYears) mustBe "3"
      binder.unbind("taxYear", CYMinus2TaxYears) mustBe "2"
      binder.unbind("taxYear", CYMinus1TaxYear) mustBe "1"
    }
  }

}