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

package models

abstract class TaxYear(val year: Int) {
  override def toString: String = s"$year"
}

case object CYMinus4TaxYear extends TaxYear(4)
case object CYMinus3TaxYear extends TaxYear(3)
case object CYMinus2TaxYear extends TaxYear(2)
case object CYMinus1TaxYear extends TaxYear(1)

object TaxYear {

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