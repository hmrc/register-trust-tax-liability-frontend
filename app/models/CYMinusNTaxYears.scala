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

import play.api.mvc.{JavascriptLiteral, PathBindable}

sealed trait CYMinusNTaxYears {
  val n: Int
  val messagePrefix : String
  override def toString: String = n.toString

  def asShortFinishYear(): String =
    uk.gov.hmrc.time.TaxYear.current.back(n).finishYear.toString.takeRight(2)
}

case object CYMinus4TaxYears extends CYMinusNTaxYears {
  override val n: Int = 4
  override val messagePrefix: String = "cyMinusFour"
}
case object CYMinus3TaxYears extends CYMinusNTaxYears {
  override val n: Int = 3
  override val messagePrefix: String = "cyMinusThree"
}
case object CYMinus2TaxYears extends CYMinusNTaxYears {
  override val n: Int = 2
  override val messagePrefix: String = "cyMinusTwo"
}
case object CYMinus1TaxYear extends CYMinusNTaxYears {
  override val n: Int = 1
  override val messagePrefix: String = "cyMinusOne"
}

object CYMinusNTaxYears {

  implicit val jsLiteral: JavascriptLiteral[CYMinusNTaxYears] = (value: CYMinusNTaxYears) => value.toString

  implicit def pathBindable(implicit intBinder: PathBindable[Int]): PathBindable[CYMinusNTaxYears] = new PathBindable[CYMinusNTaxYears] {
    override def bind(key: String, value: String): Either[String, CYMinusNTaxYears] = {

      def taxYearFromId(id: Int): Option[CYMinusNTaxYears] = {
        id match {
          case CYMinus4TaxYears.n => Some(CYMinus4TaxYears)
          case CYMinus3TaxYears.n => Some(CYMinus3TaxYears)
          case CYMinus2TaxYears.n => Some(CYMinus2TaxYears)
          case CYMinus1TaxYear.n => Some(CYMinus1TaxYear)
          case _ => None
        }
      }

      for {
        id <- intBinder.bind(key, value).right
        taxYear <- taxYearFromId(id).toRight("Not a valid tax year").right
      } yield taxYear
    }

    override def unbind(key: String, value: CYMinusNTaxYears): String = value.toString.trim.toLowerCase
  }

}