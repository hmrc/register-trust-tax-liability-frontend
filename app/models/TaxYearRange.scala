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

import play.api.i18n.Messages

case class TaxYearRange(taxYear: TaxYear)(implicit messages: Messages) {

  private val fullDatePattern: String = "d MMMM yyyy"

  private val start = uk.gov.hmrc.time.TaxYear.current.back(taxYear.year).starts.toString(fullDatePattern)
  private val end = uk.gov.hmrc.time.TaxYear.current.back(taxYear.year).finishes.toString(fullDatePattern)

  private val startingYearForTaxYear: String = uk.gov.hmrc.time.TaxYear.current.back(taxYear.year).startYear.toString

  def yearAtStart: String = startingYearForTaxYear

  def startYear: String = start

  def endYear: String = end

  def toRange : String = {
    messages("taxYearToRange", start, end)
  }

  def andRange : String = {
    messages("taxYearAndRange", start, end)
  }

}
