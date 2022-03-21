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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.play.language.LanguageUtils
import uk.gov.hmrc.time.TaxYear

import javax.inject.Inject

class TaxYearRange @Inject()(languageUtils: LanguageUtils) {

  private def taxYearOf(cYMinusNTaxYears: CYMinusNTaxYears): TaxYear = uk.gov.hmrc.time.TaxYear.current.back(cYMinusNTaxYears.n)

  def startDate(cYMinusNTaxYears: CYMinusNTaxYears)(implicit messages: Messages): String =
    languageUtils.Dates.formatDate(taxYearOf(cYMinusNTaxYears).starts)

  def endDate(cYMinusNTaxYears: CYMinusNTaxYears)(implicit messages: Messages): String =
    languageUtils.Dates.formatDate(taxYearOf(cYMinusNTaxYears).finishes)

  def yearAtStart(cYMinusNTaxYears: CYMinusNTaxYears): String = taxYearOf(cYMinusNTaxYears).startYear.toString

  def toRange(cYMinusNTaxYears: CYMinusNTaxYears)(implicit messages: Messages): String = {
    messages("taxYearToRange", startDate(cYMinusNTaxYears), endDate(cYMinusNTaxYears))
  }

  def toLabelArgs(cYMinusNTaxYears: CYMinusNTaxYears)(implicit messages: Messages): Seq[String] =
    Seq(startDate(cYMinusNTaxYears), endDate(cYMinusNTaxYears))
}
