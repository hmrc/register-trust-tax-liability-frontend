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

import org.joda.time.{LocalDate => JodaDate}
import play.api.i18n.Messages
import uk.gov.hmrc.play.language.LanguageUtils

import java.time.{LocalDate => JavaDate}
import javax.inject.Inject

class TaxYearRange @Inject()(languageUtils: LanguageUtils) {

  private def taxYearYear(taxYear: TaxYear) = uk.gov.hmrc.time.TaxYear.current.back(taxYear.year)

  implicit class JodaToJava(date: JodaDate) {
    def toJavaDate: JavaDate = JavaDate.of(date.getYear, date.getMonthOfYear, date.getDayOfMonth)
  }

  def startYear(taxYear: TaxYear)(implicit messages: Messages): String =
    languageUtils.Dates.formatDate(taxYearYear(taxYear).starts.toJavaDate)

  def endYear(taxYear: TaxYear)(implicit messages: Messages): String =
    languageUtils.Dates.formatDate(taxYearYear(taxYear).finishes.toJavaDate)

  def yearAtStart(taxYear: TaxYear): String = taxYearYear(taxYear).startYear.toString

  def toRange(taxYear: TaxYear)(implicit messages: Messages): String = {
    messages("taxYearToRange", startYear(taxYear), endYear(taxYear))
  }
}
