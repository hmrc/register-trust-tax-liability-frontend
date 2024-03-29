/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

trait Formatters {

  private[mappings] def stringFormatterWithArguments(errorKey: String,
                                                     args: Seq[Any]): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None | Some("") => Left(Seq(FormError(key, errorKey, args)))
        case Some(s) => Right(s)
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def booleanFormatterWithArguments(requiredKey: String,
                                                      invalidKey: String,
                                                      args: Seq[Any]): Formatter[Boolean] = new Formatter[Boolean] {

    private val baseFormatter = stringFormatterWithArguments(requiredKey, args)

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Boolean] =
      baseFormatter
        .bind(key, data)
        .flatMap {
        case "true" => Right(true)
        case "false" => Right(false)
        case _ => Left(Seq(FormError(key, invalidKey, args)))
      }

    def unbind(key: String, value: Boolean): Map[String, String] = Map(key -> value.toString)
  }

}
