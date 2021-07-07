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
import generators.DateGenerators
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import uk.gov.hmrc.time.TaxYear

class TaxYearServiceSpec extends SpecBase with ScalaCheckPropertyChecks with DateGenerators with BeforeAndAfterEach {

  val taxYearService: TaxYearService = mock[TaxYearService]

  override def beforeEach(): Unit = {
    reset(taxYearService)
    when(taxYearService.currentTaxYear).thenReturn(TaxYear(arbitraryStartYear))
    when(taxYearService.nTaxYearsAgoFinishYear(any())).thenCallRealMethod()
  }

  "TaxYearService" when {

    "nTaxYearsAgoFinishYear" must {
      "return last two digits of tax year finish year" in {
        case class Test(input: Int, expectedOutput: String)

        val tests = Seq(
          Test(1, "20"),
          Test(2, "19"),
          Test(3, "18"),
          Test(4, "17")
        )

        tests.foreach { test =>
          val result = taxYearService.nTaxYearsAgoFinishYear(test.input)
          result mustBe test.expectedOutput
        }
      }
    }
  }

}
