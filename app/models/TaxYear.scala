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

import play.api.mvc.JavascriptLiteral
sealed trait TaxYear {
  val year: Int
  val messagePrefix : String
  override def toString: String = year.toString

  def asShortFinishYear(): String =
    uk.gov.hmrc.time.TaxYear.current.back(year).finishYear.toString.takeRight(2)
}

case object CYMinus4TaxYear extends TaxYear {
  override val year: Int = 4
  override val messagePrefix: String = "cyMinusFour"
}
case object CYMinus3TaxYear extends TaxYear {
  override val year: Int = 3
  override val messagePrefix: String = "cyMinusThree"
}
case object CYMinus2TaxYear extends TaxYear {
  override val year: Int = 2
  override val messagePrefix: String = "cyMinusTwo"
}
case object CYMinus1TaxYear extends TaxYear {
  override val year: Int = 1
  override val messagePrefix: String = "cyMinusOne"
}

object TaxYear {

  implicit val jsLiteral : JavascriptLiteral[TaxYear] = new JavascriptLiteral[TaxYear] {
    override def to(value: TaxYear): String = value.toString
  }

  def from(int: Int) : Option[TaxYear] = {
    int match {
      case 4 => Some(CYMinus4TaxYear)
      case 3 => Some(CYMinus3TaxYear)
      case 2 => Some(CYMinus2TaxYear)
      case 1 => Some(CYMinus1TaxYear)
      case _ => None
    }
  }

}