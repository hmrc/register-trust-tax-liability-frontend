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

package models.requests

import models.TaxYear
import play.api.mvc.PathBindable

object TaxYearBindable {

  implicit def pathBindable(implicit intBinder: PathBindable[Int]) = new PathBindable[TaxYear] {
    override def bind(key: String, value: String): Either[String, TaxYear] = {
      for {
        id <- intBinder.bind(key, value).right
        taxYear <- TaxYear.from(id).toRight("Not a valid tax year").right
      } yield taxYear
    }

    override def unbind(key: String, value: TaxYear): String = value.toString.trim.toLowerCase
  }

}
